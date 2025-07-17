package com.example.spring_security.common.exception

import com.example.spring_security.common.dto.BaseResponse
import com.example.spring_security.common.enum.ResultStatus
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

//RestController 예외처리 전용 클래스
@Order(value = 2)
@RestControllerAdvice
class CommonExceptionHandler {
    //특정 컨트롤러에서(MethodArgumentNotValidException)만 발생하는 예외
    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun methodArgumentNotValidExceptionHandler(
        exception: MethodArgumentNotValidException
    ) : ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mutableMapOf<String, String>()
        exception.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMsg = error.defaultMessage
            errors[fieldName] = errorMsg ?: "에러 메시지가 존재하지 않습니다!"
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            BaseResponse(
                status = ResultStatus.ERROR.name,
                data = errors,
                resultMsg = ResultStatus.ERROR.msg,
            )
        )
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    protected fun notFoundApiUrlExceptionHandler(exception: NoHandlerFoundException) :
            ResponseEntity<BaseResponse<Any>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            BaseResponse(status = ResultStatus.ERROR.name, resultMsg = "존재하지 않는 Api Url 입니다.")
        )
    }

    @ExceptionHandler(Exception::class)
    protected fun defaultExceptionHandler(exception: Exception) :
            ResponseEntity<BaseResponse<Any>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            BaseResponse(status = ResultStatus.ERROR.name, resultMsg = ResultStatus.ERROR.msg)
        )
    }
}