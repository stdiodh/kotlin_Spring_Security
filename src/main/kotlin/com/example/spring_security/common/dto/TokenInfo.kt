package com.example.spring_security.common.dto

//토큰 정보 클래스
class TokenInfo (
    val grantType : String,
    val accessToken : String,
    val refreshToken : String
)