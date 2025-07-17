package com.example.spring_security.common.dto

import com.example.spring_security.common.enum.ResultStatus

/**
 * status	String	응답 상태 코드 (예: "SUCCESS", "FAIL")
 * data	T?	응답에 담을 실제 데이터 (제네릭 타입)
 * resultMsg	String	상태에 대한 메시지 (예: "요청 성공", "이메일 중복" 등)
 */

class BaseResponse<T>(
    var status : String = ResultStatus.SUCCESS.name,
    var data : T? = null,
    var resultMsg : String = ResultStatus.SUCCESS.msg
)