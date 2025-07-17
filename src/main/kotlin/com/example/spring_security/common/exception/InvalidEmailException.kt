package com.example.spring_security.common.exception

class InvalidEmailException(
    val fieldName : String = "",
    message : String = "에러가 발생했습니다."
) : RuntimeException(message)