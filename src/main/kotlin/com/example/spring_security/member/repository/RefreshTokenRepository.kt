package com.example.spring_security.member.repository

import com.example.spring_security.member.entity.RefreshToken
import org.springframework.data.repository.CrudRepository

interface RefreshTokenRepository : CrudRepository<RefreshToken, String> {
    fun findByMemberId(memberId: Long): RefreshToken?
}