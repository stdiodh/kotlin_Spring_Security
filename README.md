# 🗂️ 4주차 - 1:N RDB Database
> 게시판 기능을 구현해보자!

본 문서는 Spring Data JPA를 이용하여 1:N 관계를 가진 게시판과 댓글 기능을 구현합니다.</br>
데이터베이스는 `MySQL`을 이용합니다.

---
## 이론 설명
## ERD란?

<img width="1600" height="1000" src="https://github.com/user-attachments/assets/dc6a52d9-f503-4f20-8125-9fdfd390494c" />

- 개체 관계 다이어그램으로써 데이터베이스 모델링에서 사용되는 시각적 도구
- **Entity(개체)**, **Attribute(속성)**, **Relationship(관계)** 을 통해 데이터 모델을 설계

 
## 📦 구성 요소

| 요소         | 설명 |
|--------------|------|
| **Entity**   | 테이블에 해당하는 개체. 사각형으로 표현 |
| **Attribute**| Entity의 속성. Entity에 연결된 타원형 또는 내부 속성으로 표시 |
| **Primary Key** | 기본 키(PK)는 속성명 앞에 🔑 또는 PK로 표시 |
| **Foreign Key** | 외래 키(FK)는 속성명 앞에 FK로 표시 |
| **Relationship** | Entity 간의 관계를 연결선으로 표현, 선 위에 관계명과 Cardinality를 표시 |


## ERD 속 표기법
## 🧾 IE 표기법이란?

IE(Information Engineering) 표기법은 ERD에서 **관계형 데이터베이스 설계 시 가장 널리 쓰이는 표기법** 중 하나입니다.  
**핵심 특징은 각 요소(Entity, 속성, 관계)를 명확하게 구분하여 설계할 수 있다는 점**입니다.

<img width="800" height="600" alt="image" src="https://github.com/user-attachments/assets/66fa022c-6caf-4511-8762-e330ec634c34" />

## 🔢 Cardinality (관계 수 표현)

| 표기 | 의미 | 설명 |
|------|------|------|
| 1:1  | 일대일 | 하나의 Entity가 다른 Entity와 정확히 하나만 관계 |
| 1:N  | 일대다 | 하나의 Entity가 여러 개의 다른 Entity와 관계 |
| M:N  | 다대다 | 여러 개의 Entity들이 서로 관계 (관계 테이블 필요) |

---
## 📌 실습 환경

- **Swagger UI 주소**  
  http://localhost:8080/swagger-ui/index.html#/

- **실습 도구**  
  POSTMAN → Swagger로 대체 (API 명세 제공의 한계 때문)

<img width="1822" height="1180" alt="스크린샷 2025-07-14 오후 3 42 22" src="https://github.com/user-attachments/assets/5eec3917-3eb6-44e4-8b00-f9516e68f674" />

---

### 4주차 실습 ERD
<img width="2276" height="1012" alt="image" src="https://github.com/user-attachments/assets/c7052d72-5af0-4582-9beb-3dacbf9af2ea" />

### Entity 관계
```kotlin
// Post.kt
@OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = [CascadeType.ALL])
var comments: List<Comment>? = null
```

```kotlin
// Comment.kt
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(foreignKey = ForeignKey(name = "fk_comment_post_id"))
val post: Post
```
| 로딩 방식   | 설명                         | 예시                                    |
| ------- | -------------------------- | ------------------------------------- |
| `EAGER` | 즉시 로딩 – 조회 시 연관 객체도 함께 가져옴 | `SELECT * FROM comment JOIN post ...` |
| `LAZY`  | 지연 로딩 – 실제 접근 시 로딩         | `SELECT * FROM post` → 이후에 comment 조회 |

<img width="1600" height="1000" alt="image" src="https://github.com/user-attachments/assets/7ba0b008-c922-44a3-bb24-018e4e6bc406" />

---

### 1. 게시글 작성 (POST `/api/post/{id}`)
- **요청 방식**: `POST`
- **요청 Body**: `PostRequestDto`
```kotlin
data class PostRequestDto (
    var id : Long?,
    @field:NotBlank(message = "제목을 반드시 입력되어야 합니다!")
    var title : String,
    @field:NotBlank(message = "내용은 반드시 입력되어야 합니다!")
    var post : String,
    @field:Min(value = 1, message = "유효하지 않은 사용자입니다!")
    var userId : Long,
    var public : Boolean,
)
```
- **DTO Validation 적용됨**

<img width="3424" height="2174" alt="스크린샷 2025-07-21 오후 2 26 15" src="https://github.com/user-attachments/assets/23f9f95c-5613-41ef-872b-74dcc40f317a" />

### 2. 댓글 작성 (POST `/api/posts/comments`)
- **요청 방식**: `POST`
- **요청 Body**: `CommentRequestDto`
```kotlin
data class CommentRequestDto(
    val postId: Long,
    val content: String
)
```

<img width="3424" height="2174" alt="스크린샷 2025-07-21 오후 2 26 59" src="https://github.com/user-attachments/assets/48327dc6-1980-40c7-80e6-70c29f0a085d" />


## 📌 참고 링크

- [1:N 관계 구축하기](https://velog.io/@qazws78941/Spring-Boot1N-%EA%B4%80%EA%B3%84-%EA%B5%AC%EC%B6%95%ED%95%98%EA%B8%B0)
- [1:N 관계에서의 N+1 문제 해결](https://velog.io/@qazws78941/Spring-Boot1N-%EA%B4%80%EA%B3%84%EC%97%90%EC%84%9C%EC%9D%98-N1-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0)
- [Spring Data JPA 공식 문서](https://spring.io/projects/spring-data-jpa)


