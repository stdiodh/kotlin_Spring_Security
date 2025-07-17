package com.example.spring_security.member.repository

import com.example.spring_security.member.entity.MemberRole
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRoleRepository : JpaRepository<MemberRole, Long?>