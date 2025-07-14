package com.example.Spring_Security.Common.Exception.Member

import com.example.Spring_Security.Common.Dto.BaseResponse
import com.example.Spring_Security.Common.Enum.ResultStatus
import com.example.Spring_Security.Common.Exception.InvalidEmailException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

//우선순위
@Order(value = 1)
@RestControllerAdvice
class MemberExceptionHanderler {
    @ExceptionHandler(InvalidEmailException::class)
    protected fun invalidEmailExceptionHandler(exception: InvalidEmailException)
            : ResponseEntity<BaseResponse<Map<String, String>>>{
        val error = mapOf(exception.fieldName to (exception.message?: "Not Exception Massage"))
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                BaseResponse(
                    status = ResultStatus.ERROR.name,
                    data = error,
                    resultMsg = ResultStatus.ERROR.msg,
                )
            )
    }
    @ExceptionHandler(BadCredentialsException::class)
    protected fun badCredentialExcptionHandler(exception: BadCredentialsException)
            : ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf("로그인 실패" to "이메일 혹은 비밀번호를 확인하세요.")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            BaseResponse(
                status = ResultStatus.ERROR.name,
                data = errors,
                resultMsg = ResultStatus.ERROR.msg
            )
        )
    }
}