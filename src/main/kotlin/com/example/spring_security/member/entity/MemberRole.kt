package com.example.spring_security.member.entity

import com.example.spring_security.common.enum.Role
import jakarta.persistence.*

@Entity
class MemberRole(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id : Long?,

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    val role : Role,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = ForeignKey(name = "fk_member_role_member_id"))
    val member: Member
)