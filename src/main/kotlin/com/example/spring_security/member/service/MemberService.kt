package com.example.spring_security.member.service

import com.example.spring_security.common.authority.JwtTokenProvider
import com.example.spring_security.common.dto.CustomUser
import com.example.spring_security.common.dto.TokenInfo
import com.example.spring_security.common.enum.Role
import com.example.spring_security.common.exception.member.InvalidEmailException
import com.example.spring_security.member.dto.LoginDto
import com.example.spring_security.member.dto.MemberRequestDto
import com.example.spring_security.member.dto.MemberResponseDto
import com.example.spring_security.member.dto.PasswordResetRequestDto
import com.example.spring_security.member.entity.Member
import com.example.spring_security.member.entity.MemberRole
import com.example.spring_security.member.entity.RefreshToken
import com.example.spring_security.member.entity.ResetCode
import com.example.spring_security.member.repository.MemberRepository
import com.example.spring_security.member.repository.MemberRoleRepository
import com.example.spring_security.member.repository.PasswordResetCodeRepository
import com.example.spring_security.member.repository.RefreshTokenRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import java.time.LocalDateTime

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    //인증관리자
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val javaMailSender : JavaMailSender,
    private val passwordResetCodeRepository: PasswordResetCodeRepository,
    private val templateEngine : SpringTemplateEngine
) {
    //회원가입
    fun signUp(memberRequestDto: MemberRequestDto) : String {
        // 이메일 중복 검사
        var member: Member? = memberRepository.findByEmail(memberRequestDto.email)
        if (member != null){
            throw InvalidEmailException(fieldName = "email", message = "이미 가입한 이메일입니다!")
        }

        // 엔티티로 변환 및 저장
        member = memberRequestDto.toEntity()
        memberRepository.save(member)

        // 회원에게 기본 권한 부여 (MEMBER)
        val memberRole = MemberRole(
            id = null,
            role = Role.MEMBER,
            member = member,
        )
        memberRoleRepository.save(memberRole)

        return "회원가입이 완료되었습니다."
    }

    //로그인
    fun login(loginDto: LoginDto) : TokenInfo {
        // 인증 토큰 생성 (Spring Security에서 username/password로 사용)
        val authenticationToken = UsernamePasswordAuthenticationToken(loginDto.email, loginDto.password)

        // 실제 인증 수행 (UserDetailsService 호출 + 비밀번호 검증)
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        val memberId = (authentication.principal as CustomUser).id
        val result = refreshTokenRepository.findByMemberId(memberId)
        if(result != null){
            throw RuntimeException("이미 로그인한 사용자입니다.")
        }

        val accessToken = jwtTokenProvider.createAccessToken(authentication)
        val refreshToken = jwtTokenProvider.createRefreshToken()

        val data = RefreshToken(
            memberId = memberId,
            refreshToken = refreshToken
        )

        refreshTokenRepository.save(data)

        // 인증된 객체를 기반으로 JWT 토큰 생성
        return TokenInfo(grantType = "Bearer", accessToken = accessToken, refreshToken = refreshToken)
    }

    //로그아웃
    fun logout(id: Long) : String {
        val member = memberRepository.findByIdOrNull(id)
            ?: throw RuntimeException("존재하지 않는 사용자입니다!")
        val refreshToken = refreshTokenRepository.findByMemberId(id)
            ?: throw RuntimeException("로그인 하지 않는 사용자입니다!")

        refreshTokenRepository.delete(refreshToken)
        return "로그아웃 되었습니다."
    }

    //내 정보 조회
    fun searchMyInfo(id: Long) : MemberResponseDto {
        val member = memberRepository.findByIdOrNull(id)
            ?: throw RuntimeException("존재하지 않는 사용자입니다!")
        return member.toResponse()
    }

    //비밀번호 재설정 코드
    fun sendPasswordResetCode(email: String): String {
        val member = memberRepository.findByEmail(email) ?: throw InvalidEmailException(fieldName = "email", message = "해당 사용자를 찾을 수 없습니다.")

        val code = (100000 .. 999999).random().toString()
        val expirationTime = LocalDateTime.now().plusMinutes(15)

        val passwordResetCode = ResetCode(
            code = code,
            email = email,
            expiryDate = expirationTime
        )
        passwordResetCodeRepository.save(passwordResetCode)

        val context = Context().apply {
            setVariable("email", email)
            setVariable("code", code)
        }
        val html = templateEngine.process("reset-password-code.html", context)

        val mimeMessage = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, "utf-8")
        helper.setTo(email)
        helper.setSubject("비밀번호 재설정 코드 안내")
        helper.setText(html, true)

        javaMailSender.send(mimeMessage)

        return "메일이 발송되었습니다."
    }

    fun validationResetCode(code: String, email: String): Boolean {
        val passwordResetCode = passwordResetCodeRepository.findByCodeAndEmail(code, email)
        return passwordResetCode != null && passwordResetCode.expiryDate.isAfter(LocalDateTime.now())
    }

    fun passwordSave(member: Member){
        memberRepository.save(member)
    }

    fun handlePasswordReset(passwordResetRequestDto: PasswordResetRequestDto) : String {
        val member = memberRepository.findByEmail(passwordResetRequestDto.email)
            ?: throw InvalidEmailException(fieldName = "email", message = "사용자를 찾을 수 없습니다.")

        if(!validationResetCode(passwordResetRequestDto.code, passwordResetRequestDto.email)){
            throw InvalidEmailException(fieldName = "code", message = "인증코드가 정확하지 않습니다!")
        }

        member.password = passwordResetRequestDto.newPassword
        passwordSave(member)

        return "비밀번호가 변경되었습니다."
    }
}