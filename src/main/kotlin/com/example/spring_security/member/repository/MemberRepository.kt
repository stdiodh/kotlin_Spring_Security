package com.example.spring_security.member.repository

import com.example.spring_security.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long>{
    fun findByEmail(email : String) : Member?
}