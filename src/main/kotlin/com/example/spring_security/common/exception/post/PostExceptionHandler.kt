package com.example.spring_security.common.exception.post

import com.example.spring_security.common.dto.BaseResponse
import com.example.spring_security.common.enum.ResultStatus
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Order(value = 1)
@RestControllerAdvice
class PostExceptionHandler {

    @ExceptionHandler(PostException::class)
    protected fun noPostExceptionHandler(exception : PostException) :
            ResponseEntity<BaseResponse<Any>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                BaseResponse(
                    status = ResultStatus.ERROR.name, resultMsg = exception.msg
                )
            )
    }
}