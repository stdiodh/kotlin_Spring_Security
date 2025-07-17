package com.example.spring_security.common.enum

//이넘 클래스 사용 이유 : 개발자 입장에서 쉽게 보기 위해 + 유지보수 재사용성 증가
//파라미터 안 상수 메시지 추가
enum class ResultStatus (val msg : String) {
    SUCCESS("요청이 성공했습니다!"),
    ERROR("에러가 발생하였습니다.")
}