package com.example.spring_security.member.entity

//어노테이션 확인 잘하기
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash(value = "refresh", timeToLive = 1000 * 60 * 60 * 24 * 30L)
class RefreshToken (
    @Id
    @Indexed
    val memberId: Long,

    @Indexed
    val refreshToken: String,
)