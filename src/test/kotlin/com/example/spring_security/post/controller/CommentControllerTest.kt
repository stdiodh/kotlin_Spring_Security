package com.example.spring_security.post.controller

import com.example.spring_security.post.dto.CommentRequestDto
import com.example.spring_security.post.dto.CommentResponseDto
import com.example.spring_security.post.dto.PostResponseDto
import com.example.spring_security.post.service.CommentService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(CommentController::class)
class CommentControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var commentService: CommentService

    @Test
    @WithMockUser
    fun `댓글 전체 조회 성공`() {
        // given: 댓글 목록 3개를 미리 반환하도록 설정
        val comments = listOf(
            CommentResponseDto(1L, "첫 번째 댓글"),
            CommentResponseDto(2L, "두 번째 댓글"),
            CommentResponseDto(3L, "세 번째 댓글")
        )
        given(commentService.getComments()).willReturn(comments)

        // when & then: GET /api/posts/comments 요청 후 BaseResponse 구조의 JSON 응답을 검증
        mockMvc.perform(get("/api/posts/comments"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(3))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].content").value("첫 번째 댓글"))
            .andExpect(jsonPath("$.data[1].id").value(2))
            .andExpect(jsonPath("$.data[1].content").value("두 번째 댓글"))
            .andExpect(jsonPath("$.data[2].id").value(3))
            .andExpect(jsonPath("$.data[2].content").value("세 번째 댓글"))

        // verify: service 호출 여부 검증
        verify(commentService).getComments()
    }

    @Test
    @WithMockUser
    fun `댓글 전체 조회 - 빈 목록`() {
        // given: 댓글이 없을 때 빈 리스트 반환
        given(commentService.getComments()).willReturn(emptyList())

        // when & then: 빈 배열이 포함된 BaseResponse 구조 확인
        mockMvc.perform(get("/api/posts/comments"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(0))

        verify(commentService).getComments()
    }

    @Test
    @WithMockUser
    fun `댓글 작성 성공`() {
        // given: 댓글 작성 요청과 그에 대한 응답(Post 포함)을 설정
        val request = CommentRequestDto(1L, "새로운 댓글")
        val commentResponse = CommentResponseDto(1L, "새로운 댓글")
        val postResponse = PostResponseDto(
            1L, "게시글 제목", "게시글 내용", 1L, true,
            listOf(commentResponse)
        )
        given(commentService.createComment(any<CommentRequestDto>())).willReturn(postResponse)

        // when & then: POST /api/posts/comments 요청 후, 직접 PostResponseDto 형태로 응답하는지 확인
        mockMvc.perform(post("/api/posts/comments")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("게시글 제목"))
            .andExpect(jsonPath("$.post").value("게시글 내용"))
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.public").value(true))
            .andExpect(jsonPath("$.comments").isArray)
            .andExpect(jsonPath("$.comments.length()").value(1))
            .andExpect(jsonPath("$.comments[0].id").value(1))
            .andExpect(jsonPath("$.comments[0].content").value("새로운 댓글"))

        verify(commentService).createComment(any<CommentRequestDto>())
    }

    @Test
    @WithMockUser
    fun `댓글 작성 실패 - 존재하지 않는 게시글`() {
        // given: 존재하지 않는 게시글 ID로 요청 시 예외 발생
        val request = CommentRequestDto(999L, "댓글 내용")
        given(commentService.createComment(any<CommentRequestDto>()))
            .willThrow(IllegalArgumentException("게시글을 찾을 수 없습니다."))

        // when & then: 400 Bad Request 응답 확인
        mockMvc.perform(post("/api/posts/comments")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest)

        verify(commentService).createComment(any<CommentRequestDto>())
    }

    @Test
    @WithMockUser
    fun `댓글 작성 - 빈 내용`() {
        // given: 빈 문자열로 댓글 작성 요청 → 허용된다고 가정
        val request = CommentRequestDto(1L, "")
        val commentResponse = CommentResponseDto(1L, "")
        val postResponse = PostResponseDto(
            1L, "게시글 제목", "게시글 내용", 1L, true,
            listOf(commentResponse)
        )
        given(commentService.createComment(any<CommentRequestDto>())).willReturn(postResponse)

        // when & then: 빈 내용 댓글도 정상 응답인지 확인
        mockMvc.perform(post("/api/posts/comments")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.comments[0].content").value(""))

        verify(commentService).createComment(any<CommentRequestDto>())
    }

    @Test
    @WithMockUser
    fun `댓글 작성 - 매우 긴 내용`() {
        // given: 매우 긴 문자열로 댓글 작성 요청
        val longContent = "a".repeat(1000)
        val request = CommentRequestDto(1L, longContent)
        val commentResponse = CommentResponseDto(1L, longContent)
        val postResponse = PostResponseDto(
            1L, "게시글 제목", "게시글 내용", 1L, true,
            listOf(commentResponse)
        )
        given(commentService.createComment(any<CommentRequestDto>())).willReturn(postResponse)

        // when & then: 길이가 긴 댓글도 정상 저장되는지 확인
        mockMvc.perform(post("/api/posts/comments")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.comments[0].content").value(longContent))

        verify(commentService).createComment(any<CommentRequestDto>())
    }

    @Test
    @WithMockUser
    fun `유효하지 않은 JSON 형식 요청`() {
        // when & then: 잘못된 JSON 형식 요청 시 400 Bad Request 응답
        mockMvc.perform(post("/api/posts/comments")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ invalid json }"))
            .andExpect(status().isBadRequest)
    }
}
