package com.example.spring_security.common.config

import com.example.spring_security.common.authority.JwtAuthenticationFilter
import com.example.spring_security.common.authority.JwtTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

// 이 클래스는 스프링 설정 파일임을 나타냄
@Configuration
// Spring Security 기능을 활성화 (자동 구성 꺼지고 개발자가 직접 설정)
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider // JWT 관련 기능을 제공하는 클래스 주입
) {

    // SecurityFilterChain 빈 정의 (스프링 시큐리티 핵심 설정)
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // CSRF 보안 비활성화 (REST API의 경우 주로 사용하지 않음)
            .csrf { it.disable() }

            // CORS 설정 비활성화 (테스트용. 실제 운영에서는 활성화 필요)
            .cors { it.disable() }

            // 기본 로그인 방식 (브라우저 팝업창) 비활성화
            .httpBasic { it.disable() }

            // 세션 방식이 아닌 JWT 방식 사용 (세션을 생성하지 않음)
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            // 권한 설정
            .authorizeHttpRequests{
                it.requestMatchers("/api/member/join", "/api/member/login",
                    "/api/member/reset-password-code", "/api/member/reset-password/request").anonymous()
                    .requestMatchers("/api/aws/s3/**").permitAll()
                    .requestMatchers("/api/**").hasRole("MEMBER")
                    .anyRequest().permitAll()
            }

            // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 등록
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )

        // 최종적으로 SecurityFilterChain 객체 반환
        return http.build()
    }

    // 비밀번호 암호화 빈 등록 (BCrypt 등 다양한 알고리즘 제공)
    @Bean
    fun passwordEncorder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()
}