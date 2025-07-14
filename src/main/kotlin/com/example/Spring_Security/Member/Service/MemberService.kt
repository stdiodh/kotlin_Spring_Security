package com.example.Spring_Security.Member.Service

import com.example.Spring_Security.Common.Authority.JwtTokenProvider
import com.example.Spring_Security.Common.Dto.TokenInfo
import com.example.Spring_Security.Common.Enum.Role
import com.example.Spring_Security.Common.Exception.InvalidEmailException
import com.example.Spring_Security.Member.Dto.LoginDto
import com.example.Spring_Security.Member.Dto.MemberRequestDto
import com.example.Spring_Security.Member.Entity.Member
import com.example.Spring_Security.Member.Entity.MemberRole
import com.example.Spring_Security.Member.Repository.MemberRepository
import com.example.Spring_Security.Member.Repository.MemberRoleRepository
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