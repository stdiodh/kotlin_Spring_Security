# Kotlin Spring Security API 명세서

기준 컨트롤러:
- `member/controller/MemberController.kt`
- `post/controller/PostController.kt`
- `post/controller/CommentController.kt`

기준 DTO:
- `member/dto/*`
- `post/dto/*`
- `common/dto/BaseResponse.kt`

## 공통 응답 포맷

```json
{
  "status": "SUCCESS",
  "data": {},
  "resultMsg": "요청 성공"
}
```

## 공통 에러 포맷

```json
{
  "status": "ERROR",
  "data": {
    "email": "올바르지 못한 이메일 형식입니다!"
  },
  "resultMsg": "에러가 발생했습니다."
}
```

## 1. Member API (`/api/member`)

| Method | URI | 설명 | Auth |
|---|---|---|---|
| POST | `/join` | 회원가입 | None |
| POST | `/login` | 로그인 | None |
| GET | `/info` | 내 정보 조회 | Bearer |
| DELETE | `/logout` | 로그아웃 | Bearer |
| POST | `/reset-password-code` | 재설정 코드 발송 | None |
| POST | `/reset-password/request` | 비밀번호 재설정 | None |

### 1-1) POST `/api/member/join`

#### Request Body
| 필드 | 타입 | 필수 | 제약 |
|---|---|---|---|
| `email` | String | Y | `@Email`, `@NotBlank` |
| `password` | String | Y | 8~16자, 영문/숫자/특수문자 조합 |
| `name` | String | Y | `@NotBlank` |
| `birthday` | String | Y | `yyyy-MM-dd` |
| `gender` | String | Y | `MAN` 또는 `WOMAN` |

**요청 예시**
```json
{
  "email": "user@example.com",
  "password": "Abcd1234!",
  "name": "홍길동",
  "birthday": "2000-01-01",
  "gender": "MAN"
}
```

### 1-2) POST `/api/member/login`

#### Request Body
| 필드 | 타입 | 필수 | 제약 |
|---|---|---|---|
| `email` | String | Y | `@Email`, `@NotBlank` |
| `password` | String | Y | `@NotBlank` |

**응답 data 예시**
```json
{
  "grantType": "Bearer",
  "accessToken": "...",
  "refreshToken": "..."
}
```

### 1-3) GET `/api/member/info`
- 인증된 사용자 정보 조회
- `Authorization: Bearer <token>` 필요

### 1-4) DELETE `/api/member/logout`
- 서버 측 토큰/세션 정리

### 1-5) POST `/api/member/reset-password-code`

#### Query Parameter
| 이름 | 타입 | 필수 |
|---|---|---|
| `email` | String | Y |

### 1-6) POST `/api/member/reset-password/request`

#### Request Body
| 필드 | 타입 | 필수 | 제약 |
|---|---|---|---|
| `email` | String | Y | `@Email`, `@NotBlank` |
| `code` | String | Y | `@NotBlank` |
| `newPassword` | String | Y | 8~20자, 영문/숫자/특수문자 조합 |

## 2. Post API (`/api/post`)

| Method | URI | 설명 | Auth |
|---|---|---|---|
| GET | `` | 게시글 목록 조회 | Bearer |
| GET | `/{id}` | 게시글 단건 조회 | Bearer |
| POST | `/{id}` | 게시글 생성 | Bearer |
| PUT | `/{id}` | 게시글 수정 | Bearer |
| DELETE | `/{id}` | 게시글 삭제 | Bearer |

### PostRequestDto 필드
| 필드 | 타입 | 필수 | 제약 |
|---|---|---|---|
| `id` | Long | N | - |
| `title` | String | Y | `@NotBlank` |
| `post` | String | Y | `@NotBlank` |
| `userId` | Long | Y | `@Min(1)` |
| `public` | Boolean | Y | - |

## 3. Comment API (`/api/posts/comments`)

| Method | URI | 설명 | Auth |
|---|---|---|---|
| GET | `` | 댓글 목록 조회 | Bearer |
| POST | `` | 댓글 생성 | Bearer |

### CommentRequestDto 필드
| 필드 | 타입 | 필수 | 제약 |
|---|---|---|---|
| `postId` | Long | Y | - |
| `content` | String | Y | - |

## 4. 예외 응답 규칙
- 유효성 검증 실패: `400`, `data`에 필드별 오류 map
- 로그인 실패(BadCredentials): `400`, `data={"로그인 실패":"이메일 혹은 비밀번호를 확인하세요."}`
- 존재하지 않는 URL: `404`
- 기타 예외: `400` 또는 `500(AWS S3 예외)`
