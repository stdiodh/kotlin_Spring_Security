package com.example.Spring_Security.Member.Repository

import com.example.Spring_Security.Member.Entity.MemberRole
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRoleRepository : JpaRepository<MemberRole, Long?>