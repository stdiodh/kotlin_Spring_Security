package com.example.spring_security.member.repository

import com.example.spring_security.member.entity.ResetCode
import org.springframework.data.jpa.repository.JpaRepository

interface PasswordResetCodeRepository : JpaRepository<ResetCode, Long> {
    fun findByCodeAndEmail(code: String, email: String): ResetCode?
}