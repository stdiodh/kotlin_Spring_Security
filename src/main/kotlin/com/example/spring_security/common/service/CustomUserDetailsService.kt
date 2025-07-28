package com.example.spring_security.common.service

import com.example.spring_security.common.dto.CustomUser
import com.example.spring_security.member.entity.Member
import com.example.spring_security.member.repository.MemberRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

// 인증 시 DB에서 사용자 정보를 조회하는 서비스 (스프링 빈 등록)
@Service
class CustomUserDetailsService(
    private val memberRepository: MemberRepository,     // 사용자 정보를 가져올 Repository
    private val passwordEncoder: PasswordEncoder        // 비밀번호 인코딩기 (보통 BCrypt)
) : UserDetailsService {

    /**
     * 사용자의 이메일(username)로 DB에서 사용자 정보를 조회해
     * UserDetails 객체로 반환 (Spring Security가 호출함)
     */
    override fun loadUserByUsername(username: String): UserDetails {
        return memberRepository.findByEmail(username)
            ?.let { createUserDetails(it) }  // 유저가 존재하면 UserDetails로 변환
            ?: throw UsernameNotFoundException("해당하는 유저는 존재하지 않습니다!")  // 없으면 예외
    }

    /**
     * DB에서 가져온 Member 엔티티를 Spring Security의 UserDetails로 변환
     */
    private fun createUserDetails(member: Member): UserDetails {
        return CustomUser(
            member.id!!,
            member.email,  // username
            passwordEncoder.encode(member.password), // password (주의: 이미 인코딩된 값이면 재인코딩하면 안 됨!)
            member.role!!.map { SimpleGrantedAuthority("ROLE_${it.role}") } // 권한 변환
        )
    }
}
