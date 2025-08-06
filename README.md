# 🗂️ 5주차 - RefreshToken, SMTP
> 로그인 토큰 중 RefreshToken을 생성해보고, 비밀번호를 잊어버렸을 시 메일로 인증번호가 전송되어 비밀번호를 재설정할 수 있는 로직을 만들어보자!

본 문서는 Spring Boot를 활용하여 **인증 토큰 관리(Access/Refresh Token)** 와 **이메일 전송 기능(SMTP)** 을 구현하는 방법을 설명합니다.  
캐시 저장소로는 `Redis`, 이메일 템플릿 처리는 `Thymeleaf`를 사용합니다.

---

## 이론 설명

### ✅ Access Token이란?
- 로그인 후 인증된 사용자임을 증명하는 **짧은 수명의 토큰**
- 사용자가 서버에 리소스를 요청할 때 **인증 정보로 사용**
- 민감한 정보를 담고 있어 유효 시간이 짧게 설정되는 것이 일반적

---

### 🔒 Access Token의 한계
- **짧은 유효 시간**으로 인해 자주 재로그인이 필요
- 일정 시간 경과 후 **403 오류 발생**
- 사용자 경험 저하

---

### 🔁 Refresh Token이란?
- Access Token의 단점을 보완하기 위해 도입된 **장기 토큰**
- 사용자가 로그인 상태를 유지할 수 있도록 **Access Token을 갱신하는 용도**
- Refresh Token은 서버에 안전하게 저장되어야 하며, **로그아웃 시 제거 필요**

---

## 🧰 Redis 도입 이유

| 항목 | 설명 |
|------|------|
| RDB (MySQL) | 데이터가 많아질수록 탐색 시간이 증가함 |
| Redis | 메모리 기반 캐시 시스템, 빠른 조회 가능 |
| 특징 | NoSQL 구조로 설정이 간단하며, Token 저장에 적합 |

### 🖥 Redis 설치 명령어

#### ▸ MacOS
```bash
brew install redis
brew services start redis
redis-cli -h localhost -p 6379
```

#### ▸ Windows
- [`Redis for Windows`](https://github.com/microsoftarchive/redis/releases) 에서 `.msi` 파일 설치
- 설치 경로에서 `redis-cli.exe` 실행

---

## 🔓 로그아웃 구현

- 로그아웃 시 Redis에 저장된 **Refresh Token 제거 필수**
- 제거하지 않으면 발생할 수 있는 문제:
  1. 공격자가 Redis에서 모든 토큰을 탈취할 수 있음
  2. 사용자가 많을수록 **리소스 낭비** 심화
  3. 세션이 **무한 유지**될 수 있음 (분실/탈퇴 시 위험)

---

## 📧 이메일 전송 (SMTP)

### ✅ SMTP란?
- 메일 전송 프로토콜 (Simple Mail Transfer Protocol)
- Spring Boot에서 **메일 발송 기능을 구현**할 수 있음
- 비밀번호 재설정, 인증번호 발송 등에 활용 가능

---

### ✅ Thymeleaf란?
- **HTML 템플릿 엔진**으로 변수 바인딩 가능
- 이메일 본문을 동적으로 생성할 수 있도록 도와줌

```html
<p>안녕하세요 <span th:text="${username}"></span> 님!</p>
```

---

## 📌 실습 환경

- **Swagger UI 주소**  
  http://localhost:8080/swagger-ui/index.html#/

- **실습 도구**  
  POSTMAN → Swagger로 대체 (API 명세 제공의 한계 때문)

<img width="1822" height="1180" alt="스크린샷 2025-07-14 오후 3 42 22" src="https://github.com/user-attachments/assets/5eec3917-3eb6-44e4-8b00-f9516e68f674" />

### 1. 리프레시 토큰 발급 (POST /api/member/login)
- **요청 방식**: `POST`
- **요청 Body**: `LoginRequestDto`
```kotlin
data class LoginDto (
    @field:NotBlank(message = "이메일을 입력하세요.")
    @field:Email(message = "이메일 형식이 올바르지 않습니다!")
    @JsonProperty("email")
    private val _email : String?,

    @field:NotBlank(message = "비밀번호를 입력하세요.")
    @JsonProperty("password")
    private val _password : String?
)
```
- **DTO Validation 적용됨**
<img width="857" height="602" alt="image" src="https://github.com/user-attachments/assets/ae1e15f0-a1da-48ea-af10-e6cab4c6d768" />

로그아웃을 하지 않으면 RefreshToken이 남아 있을 수 있으니 null로 표기 시 redis에 들어가 `flashall` 명령어를 사용할 것

<img width="3424" height="2174" alt="image" src="https://github.com/user-attachments/assets/384df843-068e-4204-a228-e65901315757" />


### 2. 비밀번호 재설정 코드 전송 (POST /api/member/reset-password-code)
- **요청 방식**: `POST`
- **요청 Param**: `email`

<img width="3424" height="2174" alt="image" src="https://github.com/user-attachments/assets/75b05f3b-50fb-4e34-9dfc-fa6d11568ea8" />
<img width="3424" height="2174" alt="image" src="https://github.com/user-attachments/assets/344ff534-59fb-434c-a151-945675b3bc14" />
유효한 이메일에만 전송되니 유의할 것

### 3. 비밀번호 재설정 코드 확인 후 비밀번호 재설정 (POST /api/member/reset-password/request)
- **요청 방식**: `POST`
- **요청 Body**: `PasswordResetRequestDto`
```kotlin
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
)
```
- **DTO Validation 적용됨**

<img width="3424" height="2174" alt="스크린샷 2025-08-06 오후 5 27 07" src="https://github.com/user-attachments/assets/c57a612a-4683-4f6b-8023-d49d86b8a649" />
<img width="857" height="602" alt="스크린샷 2025-08-06 오후 5 27 44" src="https://github.com/user-attachments/assets/e82e8d2d-267c-444a-a545-8fd6bb934be8" />

유효한 인증코드에 대해 비밀번호가 잘 바뀐다.


---



## 📌 참고 링크

- [인증 및 인가 (Spring Security, JWT)](https://velog.io/@stdiodh/Spring-Boot-%EC%9D%B8%EC%A6%9D-%EB%B0%8F-%EC%9D%B8%EA%B0%80-Spring-Security-JWT)
- [Redis 공식 문서](https://redis.io/)
- [Spring Mail 공식 문서](https://docs.spring.io/spring-framework/reference/integration/email.html)
- [Thymeleaf 공식 문서](https://www.thymeleaf.org/)
