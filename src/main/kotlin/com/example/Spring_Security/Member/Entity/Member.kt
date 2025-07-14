package com.example.Spring_Security.Member.Entity

import com.example.Spring_Security.Common.Enum.Gender
import com.example.Spring_Security.Member.Dto.MemberResponseDto
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(name = "uk_member_email", columnNames = ["email"])]
)
class Member (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long?,

    //유니크 키, 업데이트 불가
    @Column(nullable = false, length = 100, updatable = false)
    val email : String,

    @Column(nullable = false, length = 100)
    val password : String,

    @Column(nullable = false, length = 20)
    val name : String,

    @Column(nullable = false, length = 20)
    @Temporal(TemporalType.DATE)
    val birthday : LocalDate,

    @Column(nullable = false, length = 5)
    @Enumerated(EnumType.STRING)
    val gender : Gender,
){
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    val role : List<MemberRole>? = null

    fun toResponse() : MemberResponseDto = MemberResponseDto(
        email = email,
        password = password,
        nickName = name,
        birthday = birthday,
        gender = gender
    )
}