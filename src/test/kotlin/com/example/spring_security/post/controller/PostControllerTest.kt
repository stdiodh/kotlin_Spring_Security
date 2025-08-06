package com.example.spring_security.post.controller

import com.example.spring_security.post.dto.CommentResponseDto
import com.example.spring_security.post.dto.PostRequestDto
import com.example.spring_security.post.dto.PostResponseDto
import com.example.spring_security.post.entity.Post
import com.example.spring_security.post.repository.PostRepository
import com.example.spring_security.post.service.PostService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PostController::class)
class PostControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var postService: PostService

    @MockBean
    private lateinit var postRepository: PostRepository

    @Test
    @WithMockUser
    fun `게시글 전체 조회 성공`() {
        // given: 게시글 목록 반환 설정
        val posts : List<Post> = listOf(
            Post(1L, "첫 번째 게시글", "첫 번째 내용", 1L, true),
            Post(2L, "두 번째 게시글", "두 번째 내용", 2L, false)
        )
        given(postRepository.findAllByFetchJoin()).willReturn(posts)

        // when & then: GET /api/post 요청 후 JSON 응답 검증
        mockMvc.perform(get("/api/post"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("첫 번째 게시글"))
            .andExpect(jsonPath("$[0].post").value("첫 번째 내용"))
            .andExpect(jsonPath("$[0].userId").value(1))
            .andExpect(jsonPath("$[0].public").value(true))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].title").value("두 번째 게시글"))
            .andExpect(jsonPath("$[1].post").value("두 번째 내용"))
            .andExpect(jsonPath("$[1].userId").value(2))
            .andExpect(jsonPath("$[1].public").value(false))

        verify(postRepository).findAllByFetchJoin()
    }

    @Test
    @WithMockUser
    fun `게시글 전체 조회 - 빈 목록`() {
        // given: 빈 게시글 목록 반환
        given(postRepository.findAllByFetchJoin()).willReturn(emptyList())

        // when & then: 빈 배열 응답 확인
        mockMvc.perform(get("/api/post"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(0))

        verify(postRepository).findAllByFetchJoin()
    }

    @Test
    @WithMockUser
    fun `게시글 ID로 조회 성공`() {
        // given: 특정 게시글 반환 설정
        val postResponse = PostResponseDto(
            id = 1L,
            title = "테스트 게시글",
            post = "테스트 내용",
            userId = 1L,
            public = true,
            comments = listOf(CommentResponseDto(1L, "테스트 댓글"))
        )
        given(postService.getPostById(1L)).willReturn(postResponse)

        // when & then: GET /api/post/1 요청 후 BaseResponse 구조의 JSON 응답 검증
        mockMvc.perform(get("/api/post/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").value("테스트 게시글"))
            .andExpect(jsonPath("$.data.post").value("테스트 내용"))
            .andExpect(jsonPath("$.data.userId").value(1))
            .andExpect(jsonPath("$.data.public").value(true))
            .andExpect(jsonPath("$.data.comments").isArray)
            .andExpect(jsonPath("$.data.comments.length()").value(1))
            .andExpect(jsonPath("$.data.comments[0].id").value(1))
            .andExpect(jsonPath("$.data.comments[0].content").value("테스트 댓글"))

        verify(postService).getPostById(1L)
    }

    @Test
    @WithMockUser
    fun `게시글 작성 성공`() {
        // given: 게시글 작성 요청과 응답 설정
        val requestDto = PostRequestDto(
            id = null,
            title = "새로운 게시글",
            post = "새로운 내용",
            userId = 1L,
            public = true
        )
        val responseDto = PostResponseDto(
            id = 1L,
            title = "새로운 게시글",
            post = "새로운 내용",
            userId = 1L,
            public = true,
            comments = emptyList()
        )
        given(postService.postPost(any<PostRequestDto>())).willReturn(responseDto)

        // when & then: POST /api/post/1 요청 후 CREATED 상태와 BaseResponse 구조 검증
        mockMvc.perform(post("/api/post/1")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").value("새로운 게시글"))
            .andExpect(jsonPath("$.data.post").value("새로운 내용"))
            .andExpect(jsonPath("$.data.userId").value(1))
            .andExpect(jsonPath("$.data.public").value(true))
            .andExpect(jsonPath("$.data.comments").isArray)
            .andExpect(jsonPath("$.data.comments.length()").value(0))

        verify(postService).postPost(any<PostRequestDto>())
    }

    @Test
    @WithMockUser
    fun `게시글 작성 실패 - 제목 누락`() {
        // given: 제목이 빈 문자열인 요청
        val requestDto = PostRequestDto(
            id = null,
            title = "",
            post = "내용",
            userId = 1L,
            public = true
        )

        // when & then: 유효성 검사 실패로 400 Bad Request 응답
        mockMvc.perform(post("/api/post/1")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser
    fun `게시글 작성 실패 - 내용 누락`() {
        // given: 내용이 빈 문자열인 요청
        val requestDto = PostRequestDto(
            id = null,
            title = "제목",
            post = "",
            userId = 1L,
            public = true
        )

        // when & then: 유효성 검사 실패로 400 Bad Request 응답
        mockMvc.perform(post("/api/post/1")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser
    fun `게시글 수정 성공`() {
        // given: 게시글 수정 요청과 응답 설정
        val requestDto = PostRequestDto(
            id = 1L,
            title = "수정된 게시글",
            post = "수정된 내용",
            userId = 1L,
            public = false
        )
        val responseDto = PostResponseDto(
            id = 1L,
            title = "수정된 게시글",
            post = "수정된 내용",
            userId = 1L,
            public = false,
            comments = emptyList()
        )
        //첫 번째 파라미터는 1L로 매칭하겠다는 뜻
        given(postService.putPost(eq(1L), any<PostRequestDto>())).willReturn(responseDto)

        // when & then: PUT /api/post/1 요청 후 OK 상태와 수정된 내용 검증
        mockMvc.perform(put("/api/post/1")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").value("수정된 게시글"))
            .andExpect(jsonPath("$.data.post").value("수정된 내용"))
            .andExpect(jsonPath("$.data.public").value(false))

        verify(postService).putPost(eq(1L), any<PostRequestDto>())
    }

    @Test
    @WithMockUser
    fun `게시글 삭제 성공`() {
        // given: 게시글 삭제 서비스 설정 (junit의 willReturn이 의미가 없어 void 전용 문법)
        doNothing().`when`(postService).deletePostById(1L)

        // when & then: DELETE /api/post/1 요청 후 OK 상태와 삭제 메시지 검증
        mockMvc.perform(delete("/api/post/1")
            .with(csrf()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.data").value("게시글이 삭제되었습니다."))

        verify(postService).deletePostById(1L)
    }

    @Test
    @WithMockUser
    fun `게시글 조회 실패 - 존재하지 않는 게시글`() {
        // given: 존재하지 않는 게시글 조회 시 예외 발생
        given(postService.getPostById(999L))
            .willThrow(IllegalArgumentException("게시글을 찾을 수 없습니다."))

        // when & then: 예외로 인한 에러 응답 확인
        mockMvc.perform(get("/api/post/999"))
            .andExpect(status().isBadRequest)

        verify(postService).getPostById(999L)
    }

    // Mock Post 객체를 생성하는 헬퍼 메서드
    private fun createMockPost(
        id: Long,
        title: String,
        content: String,
        userId: Long,
        isPublic: Boolean
    ): Any {
        // Post 엔티티의 toResponse() 메서드를 호출한 결과를 모킹
        return object {
            fun toResponse() = PostResponseDto(
                id = id,
                title = title,
                post = content,
                userId = userId,
                public = isPublic,
                comments = emptyList()
            )
        }
    }
}
