package com.example.Spring_Security.Member.Repository

import com.example.Spring_Security.Member.Entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long>{
    fun findByEmail(email : String) : Member?
}