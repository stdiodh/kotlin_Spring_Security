package com.example.spring_security.common.authority

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean

// JWT 인증 필터 - Spring Security의 요청 필터 체인에서 실행됨
class JwtAuthenticationFilter (
    private val jwtTokenProvider: JwtTokenProvider // 의존성 주입된 JWT 제공자
) : GenericFilterBean() {

    /**
     * 요청(request)이 필터 체인을 통과할 때 실행되는 메서드
     * JWT 토큰을 추출하고, 유효하다면 인증 객체를 SecurityContext에 설정함
     */
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        // HTTP 요청으로 캐스팅
        val token = resolveToken(request as HttpServletRequest)

        // 토큰이 존재하고 유효하다면
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰으로부터 인증(Authentication) 객체 추출
            val authentication = jwtTokenProvider.getAuthentication(token)

            // Spring Security의 SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().authentication = authentication
        }

        // 다음 필터로 요청 전달
        chain?.doFilter(request, response)
    }

    /**
     * HTTP 요청의 Authorization 헤더에서 JWT 추출
     * 형식: "Bearer {access_token}"
     */
    private fun resolveToken(request: HttpServletRequest) : String? {
        val bearerToken = request.getHeader("Authorization") // 헤더에서 Authorization 값 가져옴

        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            bearerToken.substring(7) // "Bearer " 이후 토큰만 추출
        } else {
            null // 토큰이 없거나 형식이 잘못된 경우 null 반환
        }
    }
}
