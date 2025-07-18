package com.example.spring_security.member.service

import com.example.spring_security.common.authority.JwtTokenProvider
import com.example.spring_security.common.dto.TokenInfo
import com.example.spring_security.common.enum.Role
import com.example.spring_security.common.exception.member.InvalidEmailException
import com.example.spring_security.member.dto.LoginDto
import com.example.spring_security.member.dto.MemberRequestDto
import com.example.spring_security.member.entity.Member
import com.example.spring_security.member.entity.MemberRole
import com.example.spring_security.member.repository.MemberRepository
import com.example.spring_security.member.repository.MemberRoleRepository
import jakarta.transaction.Transactional
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    //인증관리자
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
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

        // 인증된 객체를 기반으로 JWT 토큰 생성
        return jwtTokenProvider.createToken(authentication)
    }
}