package com.example.spring_security.member.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class ResetCode (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,
    val code : String,
    val email : String,
    val expiryDate : LocalDateTime
)
