package com.example.spring_security.member.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.core.annotation.Order

data class PasswordResetRequestDto(
    @Order(1)
    @field:Email(message = "올바르지 않은 이메일 형식입니다.")
    @field:NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @JsonProperty("email")
    private val _email: String,

    @Order(2)
    @field:NotBlank(message = "인증 코드는 필수 입력 항목입니다.")
    @JsonProperty("code")
    private val _code: String,

    @Order(3)
    @field:NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @field:Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()])[a-zA-Z0-9!@#\$%^&*()]{8,20}\$",
        message = "올바르지 못한 비밀번호 형식입니다!"
    )
    @JsonProperty("newPassword")
    private val _newPassword: String
) {
    val email : String
        get() = _email
    val code : String
        get() = _code
    val newPassword : String
        get() = _newPassword
}