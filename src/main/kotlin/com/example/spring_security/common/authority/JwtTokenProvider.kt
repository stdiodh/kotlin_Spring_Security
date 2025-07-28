package com.example.spring_security.common.authority

import com.example.spring_security.common.dto.CustomUser
import com.example.spring_security.common.dto.TokenInfo
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

// JWT 만료 시간: 30분 (1000ms * 60초 * 30분)
const val EXPIRATION_MILLISECONDS : Long = 1000 * 60 * 30L
const val REFRESH_EXPIRATION_MILLISECONDS : Long = 1000 * 60 * 60 * 24 * 30L

// 스프링 컴포넌트로 등록되는 클래스 (빈으로 등록됨)
// 빈이란? 스프링 컨테이너에 의해 관리되는 재사용 가능한 소프트웨어 컴포넌트
@Component
class JwtTokenProvider {

    // application.yml 등에서 jwt.secret 값을 주입받음 (Base64 인코딩된 시크릿 키)
    @Value("\${jwt.secret}")
    lateinit var secretKey : String

    // 시크릿 키를 디코딩하여 서명용 HMAC 키로 변환 (최초 사용 시 1회 생성됨)
    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)) }

    /**
     * JWT 토큰 생성 함수
     * 로그인 후 사용자 인증 정보(Authentication)를 바탕으로 토큰을 생성
     */
    fun createAccessToken(authentication : Authentication) : String {
        // 사용자의 권한(roles/authorities)을 하나의 문자열로 결합
        val authorities : String = authentication
            .authorities
            .joinToString(",", transform = GrantedAuthority::getAuthority)

        val now = Date() // 현재 시간
        val accessExpiration = Date(now.time + EXPIRATION_MILLISECONDS) // 만료 시간 = 현재 + 30분

        val userId = (authentication.principal as CustomUser).id

        // JWT Access Token 생성
        return Jwts
            .builder()
            .subject(authentication.name) // 사용자 식별자 (주로 이메일 또는 username)
            .claim("auth", authorities)   // 사용자 권한 정보 추가.
            .claim("userId", userId)
            .issuedAt(now)                // 토큰 발급 시간
            .expiration(accessExpiration) // 토큰 만료 시간
            .signWith(key, Jwts.SIG.HS256) // HMAC SHA-256 알고리즘으로 서명
            .compact()                    // 최종적으로 JWT 문자열 생성
    }

    /**
     * JWT 토큰을 파싱하여 사용자 인증 객체(Authentication)로 변환
     * SecurityContextHolder 에 저장할 때 사용
     */
    fun getAuthentication(token: String) : Authentication {
        val claims : Claims = getClaims(token)

        // auth claim이 없으면 예외 발생
        val auth = claims["auth"] ?: throw RuntimeException("유효하지 않은 토큰입니다!")
        val userId = claims["userId"] ?: throw RuntimeException("유효하지 않은 토큰입니다!")

        // 권한 문자열을 ,로 분리하여 GrantedAuthority 리스트로 변환
        val authorities : Collection<GrantedAuthority> = (auth as String)
            .split(",")
            .map { SimpleGrantedAuthority(it) }

        // Spring Security UserDetails 생성
        // Claims.SUBJECT 는 subject 값 (username)
        val principal : UserDetails = CustomUser(userId.toString().toLong(), Claims.SUBJECT, "", authorities)

        // 인증 객체 반환
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    fun createRefreshToken() : String {
        val now = Date()

        //refresh 토큰 유효시간
        val refreshExpiration = Date(now.time + REFRESH_EXPIRATION_MILLISECONDS)
        return Jwts
            .builder()
            .issuedAt(now)
            .expiration(refreshExpiration)
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }

    /**
     * JWT 토큰 유효성 검증
     * 형식 오류, 만료 여부 등을 검사함
     */
    fun validateToken(token: String) : Boolean {
        return try {
            getClaims(token) // 내부적으로 파싱 가능하면 유효한 토큰
            true
        } catch (e : Exception) {
            println(e.message) // 디버깅용 예외 메시지 출력
            false
        }
    }

    /**
     * JWT Claims 추출 함수
     * 서명을 검증하고 payload(Claims)를 가져옴
     */
    private fun getClaims(token : String) : Claims =
        Jwts
            .parser()              // 파서 빌더 생성
            .verifyWith(key)       // 서명 검증용 키 설정
            .build()               // 파서 빌드
            .parseSignedClaims(token) // 서명된 JWT 파싱
            .payload               // payload = Claims 반환

}
