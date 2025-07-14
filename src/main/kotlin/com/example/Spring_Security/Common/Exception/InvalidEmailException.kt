package com.example.Spring_Security.Common.Exception

class InvalidEmailException(
    val fieldName : String = "",
    message : String = "에러가 발생했습니다."
) : RuntimeException(message)