package com.example.spring_security.common.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

//사용자 정의 클래스
/**
 * CustomUser	Spring Security의 User 클래스를 상속한 커스텀 사용자 클래스
 * id: Long	사용자 고유 식별자. Spring Security 기본 User 클래스에는 없기 때문에 추가
 * email	사용자의 로그인 ID 역할. Spring Security에서는 username으로 사용됨
 * password	사용자 비밀번호
 * authority	사용자의 권한 정보 (ROLE_USER, ROLE_ADMIN 등)
 * User(...)	부모 클래스인 User의 생성자를 호출함. 여기서 이메일은 username으로 사용됨
 */
class CustomUser (
    val id : Long,
    email : String,
    password : String,
    authority : Collection<GrantedAuthority>
) : User(email, password, authority)