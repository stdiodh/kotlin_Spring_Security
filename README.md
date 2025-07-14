# 🗂️ 3주차 - Spring Security
> 현재 많이 사용하는 회원가입 방식 중 토큰 방식을 이해하고 Spring Security를 활용하여 인증 및 인가 로직을 구현해보자!

본 문서는 Spring Security를 이용한 회원가입 및 인증/인가 구현 방법에 대해 설명하며<br>
인증 방식, JWT, Refresh Token, Validation에 대한 이론적 내용을 정리합니다.

---
## 이론 설명
## 인증(Authentication)과 인가(Authorization)

- **인증 (Authentication)**  
  사용자가 권한을 부여받을 수 있는지를 확인하는 절차

- **인가 (Authorization)**  
  인증된 사용자에게 적절한 권한을 부여하는 절차

대부분의 서버 API는 인증된 사용자에게만 제공되어야 하며, 보안을 위해 인증 및 인가는 필수적인 요소임.

---

## 회원가입 방식

### 1. 세션 방식 (Session-based)

<img width="1600" height="1000" alt="image" src="https://github.com/user-attachments/assets/1941f0f9-abef-489e-954d-5b898e074af2" />


- 로그인 시 서버가 **입장권(세션 ID)** 를 발급하고 서버의 세션 저장소에 유지
- 이후 요청마다 세션 ID로 사용자를 식별
- 장점: 서버가 직접 유저 상태를 관리함  
- 단점: **대규모 사용자 동시 접속 시 부하가 큼**

### 2. 토큰 방식 (Token-based)

<img width="1600" height="1000" alt="스크린샷 2025-07-14 오후 3 39 05" src="https://github.com/user-attachments/assets/0b8a88c7-3c49-4317-9884-5bcb23a548bd" />


- 로그인 시 클라이언트에게 **토큰(JWT 등)** 을 발급하고 서버는 상태 저장 X (Stateless)
- 요청 시 토큰을 함께 보내며 인증 처리
- 토큰에는 유효시간, 사용자 정보 등이 포함됨
- **현대 웹 서비스에서 많이 사용됨 (ex. JWT)**

---

## JWT (JSON Web Token)

- **JWT 구조**: `Header`.`Payload`.`Signature`
- 서버는 JWT를 통해 사용자의 정보를 포함한 토큰을 발급
- 클라이언트는 이후 모든 요청에 JWT를 포함해 인증을 수행

### JWT 활용 예시

- 사용자 UUID를 Path로 노출하지 않고, **JWT 클레임**으로 포함하여 보안성 강화
- Spring Security에서 JWT를 추출하여 사용자 인증 처리 가능

---

## Access Token vs Refresh Token

### Access Token

- 사용자 정보를 담고 있으며 **유효시간이 짧음**
- 만료되면 API 요청 시 **403 Forbidden 오류** 발생

### Refresh Token

- Access Token의 만료를 대비하여 별도로 발급
- **Access Token을 재발급 받는 용도**
- 보안 및 사용 편의성을 위해 Redis에 저장하여 관리

---

## Redis의 활용 (추후 로직 추가예정)

- 세션/토큰 저장 시 **MySQL은 탐색 속도에 비효율적**
- Redis는 **인메모리 기반의 NoSQL**로 빠른 탐색 및 캐싱에 유리
- Refresh Token 저장소로 적합함

### Redis 설치

#### MacOS
```bash
brew install redis
brew services start redis
redis-cli -h localhost -p 6379
```

### 왜 Swagger를 사용하는가?
| 도구             | 장점                | 단점                   |
| -------------- | ----------------- | -------------------- |
| **POSTMAN**    | 손쉬운 요청 테스트        | API 명세 확인 어려움        |
| **Swagger UI** | API 명세 확인 + 요청 가능 | 실제 앱 호출에 비해 UX 한계 존재 |

Swagger는 API 설계를 문서화하고 클라이언트가 테스트할 수 있게 해주는 API 명세 기반의 문서화 툴임. <br>
개발/테스트 단계에서 매우 유용하며, 유효성 검증 피드백을 시각적으로 빠르게 확인할 수 있기 때문에 사용함.


---
## 📌 실습 환경

- **Swagger UI 주소**  
  http://localhost:8080/swagger-ui/index.html#/

- **실습 도구**  
  POSTMAN → Swagger로 대체 (API 명세 제공의 한계 때문)

<img width="1822" height="1180" alt="스크린샷 2025-07-14 오후 3 42 22" src="https://github.com/user-attachments/assets/5eec3917-3eb6-44e4-8b00-f9516e68f674" />


---

## 1. 회원가입 요청 (POST `/api/member/join`)

- **요청 방식**: `POST`
- **요청 Body**: `MemberRequestDto`  
- **DTO Validation 적용됨**
<img width="1600" height="1000" alt="스크린샷 2025-07-14 오후 3 45 33" src="https://github.com/user-attachments/assets/b5971b29-cd6f-4b6c-9e06-a06e541c7cb8" />



### ✅ DTO 구조
```kotlin
data class MemberRequestDto (
    @field:NotBlank(message = "이메일을 입력하세요!")
    @field:Email(message = "올바르지 못한 이메일 형식입니다!")
    @JsonProperty("email")
    private val _email : String?,

    @field:NotBlank(message = "비밀번호를 입력하세요!")
    @field:Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()])[a-zA-Z0-9!@#\$%^&*()]{8,20}\$",
        message = "올바르지 못한 비밀번호 형식입니다!"
    )
    @JsonProperty("password")
    private val _password : String?,

    @field:NotBlank(message = "이름을 입력하세요!")
    @JsonProperty("name")
    private val _name : String?,

    @field:NotBlank(message = "생년월일을 입력하세요!")
    @field:Pattern(
        regexp = "^([12]\\d{3})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])\$",
        message = "올바르지 못한 날짜 방식입니다"
    )
    @JsonProperty("birthday")
    private val _birthday : String?,

    @field:NotBlank(message = "성별을 입력하세요!")
    @field:ValidEnum(enumClass = Gender::class, message = "성별은 MAN 혹은 WOMAN입니다!")
    @JsonProperty("gender")
    private val _gender: String?
)
```
#### 회원가입 요청 성공 화면
<img width="3424" height="2174" alt="스크린샷 2025-07-14 오후 3 44 32" src="https://github.com/user-attachments/assets/cc13798d-158d-4499-b70e-3ae387859022" />


#### Validation 실패 예시
<img width="3424" height="2174" alt="스크린샷 2025-07-14 오후 3 47 37" src="https://github.com/user-attachments/assets/a56a4b56-e050-4db4-899c-c78462ea1e41" />



### 2. 로그인 요청 (POST 요청)
- 요청 방식: POST
- 요청 조건: 유효한 이메일 및 비밀번호
- 성공 시: Access Token 및 Refresh Token 반환

**로그인 성공 시**
<img width="3424" height="2174" alt="image" src="https://github.com/user-attachments/assets/36bcbaab-7fa1-443c-a0f5-25b200278843" />

**로그인 실패 시**
- 이메일 또는 비밀번호 불일치 시 커스텀 예외 처리 발생
<img width="3424" height="2174" alt="image" src="https://github.com/user-attachments/assets/84a087c4-c2d3-4b5f-89d1-f06a3fac0d3c" />

#### 응답 포맷 및 예외 처리 구조
- BaseResponse 구조
```kotlin
class BaseResponse<T>(
    var status: String = ResultStatus.SUCCESS.name,
    var data: T? = null,
    var resultMsg: String = ResultStatus.SUCCESS.msg
)
```

| 필드          | 설명                            |
| ----------- | ----------------------------- |
| `status`    | 응답 상태 코드 (예: SUCCESS / ERROR) |
| `data`      | 실질 응답 데이터 (nullable, 제네릭)     |
| `resultMsg` | 사용자 메시지 (요청 성공/실패 사유 등)       |

#### 예외 처리 흐름
- Enum 기반 상태 관리

```kotlin
enum class ResultStatus(val msg: String) {
    SUCCESS("요청이 성공했습니다!"),
    ERROR("에러가 발생하였습니다.")
}
```

- 회원 관련 예외 처리
```kotlin
@Order(1)
@RestControllerAdvice
class MemberExceptionHanderler {

    @ExceptionHandler(InvalidEmailException::class)
    fun handleInvalidEmail(ex: InvalidEmailException): ResponseEntity<BaseResponse<Map<String, String>>> {
        val error = mapOf(ex.fieldName to (ex.message ?: "예외 메시지 없음"))
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(BaseResponse(ResultStatus.ERROR.name, error, ResultStatus.ERROR.msg))
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf("로그인 실패" to "이메일 혹은 비밀번호를 확인하세요.")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(BaseResponse(ResultStatus.ERROR.name, errors, ResultStatus.ERROR.msg))
    }
}
```

- 공통 예외 처리
```kotlin
@Order(2)
@RestControllerAdvice
class CommonExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.allErrors.forEach {
            val field = (it as FieldError).field
            errors[field] = it.defaultMessage ?: "Validation 실패"
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(BaseResponse(ResultStatus.ERROR.name, errors, ResultStatus.ERROR.msg))
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNotFound(ex: NoHandlerFoundException): ResponseEntity<BaseResponse<Any>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(BaseResponse(ResultStatus.ERROR.name, resultMsg = "존재하지 않는 API URL입니다."))

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<BaseResponse<Any>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(BaseResponse(ResultStatus.ERROR.name, resultMsg = ResultStatus.ERROR.msg))
}
```

## 📌 참고 링크

- [인증 및 인가 (Spring Security, JWT)](https://velog.io/@stdiodh/Spring-Boot-%EC%9D%B8%EC%A6%9D-%EB%B0%8F-%EC%9D%B8%EA%B0%80-Spring-Security-JWT)
- [Spring Security 공식 문서](https://spring.io/projects/spring-security)


