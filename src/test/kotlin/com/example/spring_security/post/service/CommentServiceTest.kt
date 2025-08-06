package com.example.spring_security.post.service

import com.example.spring_security.post.dto.CommentRequestDto
import com.example.spring_security.post.entity.Comment
import com.example.spring_security.post.entity.Post
import com.example.spring_security.post.repository.CommentRepository
import com.example.spring_security.post.repository.PostRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.any
import java.util.*
import org.junit.jupiter.api.Assertions.*

@ExtendWith(MockitoExtension::class)
class CommentServiceTest {

    @Mock
    private lateinit var commentRepository: CommentRepository

    @Mock
    private lateinit var postRepository: PostRepository

    @InjectMocks
    private lateinit var commentService: CommentService

    @Test
    fun `댓글 목록 조회 성공`() {
        // given
        val post = Post(1L, "제목", "내용", 1L, true)
        val comments = listOf(
            Comment(1L, "첫 번째 댓글", post),
            Comment(2L, "두 번째 댓글", post)
        )
        given(commentRepository.findAll()).willReturn(comments)

        // when
        val result = commentService.getComments()

        // then
        // 댓글 개수와 각 댓글의 내용이 기대값과 같은지 검증
        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("첫 번째 댓글", result[0].content)
        assertEquals(2L, result[1].id)
        assertEquals("두 번째 댓글", result[1].content)
        verify(commentRepository).findAll()
    }

    @Test
    fun `댓글 목록 조회 - 빈 목록`() {
        // given
        given(commentRepository.findAll()).willReturn(emptyList())

        // when
        val result = commentService.getComments()

        // then
        // 빈 리스트가 반환되는지 확인
        assertEquals(0, result.size)
        assertTrue(result.isEmpty())
        verify(commentRepository).findAll()
    }

    @Test
    fun `댓글 작성 성공`() {
        // given
        val post = Post(1L, "제목", "내용", 1L, true)
        val request = CommentRequestDto(1L, "새로운 댓글")
        val savedComment = Comment(1L, "새로운 댓글", post)

        given(postRepository.findById(1L)).willReturn(Optional.of(post))
        given(commentRepository.findAllByPost(post)).willReturn(listOf(savedComment))

        // when
        val result = commentService.createComment(request)

        // then
        // 댓글과 게시글 정보가 올바르게 포함되어 있는지 확인
        assertEquals(1L, result.id)
        assertEquals("제목", result.title)
        assertEquals("내용", result.post)
        assertEquals(1L, result.userId)
        assertEquals(true, result.public)
        assertNotNull(result.comments)
        assertEquals(1, result.comments?.size)
        assertEquals("새로운 댓글", result.comments?.get(0)?.content)

        verify(postRepository).findById(1L)
        verify(commentRepository).save(any<Comment>())
        verify(commentRepository).findAllByPost(post)
    }

    @Test
    fun `댓글 작성 실패 - 존재하지 않는 게시글`() {
        // given
        val request = CommentRequestDto(999L, "댓글 내용")
        given(postRepository.findById(999L)).willReturn(Optional.empty())

        // when & then
        // 게시글이 존재하지 않을 경우 예외 발생 확인
        val exception = assertThrows(IllegalArgumentException::class.java) {
            commentService.createComment(request)
        }
        assertEquals("게시글을 찾을 수 없습니다.", exception.message)

        // save나 findAllByPost는 호출되지 않아야 함
        verify(postRepository).findById(999L)
        verify(commentRepository, org.mockito.kotlin.never()).save(any<Comment>())
        verify(commentRepository, org.mockito.kotlin.never()).findAllByPost(any())
    }

    @Test
    fun `댓글 작성 - 여러 댓글이 있는 게시글`() {
        // given
        val post = Post(1L, "제목", "내용", 1L, true)
        val request = CommentRequestDto(1L, "새로운 댓글")
        val existingComments = listOf(
            Comment(1L, "기존 댓글 1", post),
            Comment(2L, "기존 댓글 2", post),
            Comment(3L, "새로운 댓글", post)
        )

        given(postRepository.findById(1L)).willReturn(Optional.of(post))
        given(commentRepository.findAllByPost(post)).willReturn(existingComments)

        // when
        val result = commentService.createComment(request)

        // then
        // 댓글이 3개가 되어야 함
        assertEquals(1L, result.id)
        assertNotNull(result.comments)
        assertEquals(3, result.comments?.size)
        assertEquals("새로운 댓글", result.comments?.get(2)?.content)

        verify(postRepository).findById(1L)
        verify(commentRepository).save(any<Comment>())
        verify(commentRepository).findAllByPost(post)
    }

    @Test
    fun `댓글 작성 - 빈 내용으로 작성`() {
        // given
        val post = Post(1L, "제목", "내용", 1L, true)
        val request = CommentRequestDto(1L, "")
        val savedComment = Comment(1L, "", post)

        given(postRepository.findById(1L)).willReturn(Optional.of(post))
        given(commentRepository.findAllByPost(post)).willReturn(listOf(savedComment))

        // when
        val result = commentService.createComment(request)

        // then
        // 빈 내용이라도 저장은 가능해야 함
        assertEquals(1L, result.id)
        assertNotNull(result.comments)
        assertEquals(1, result.comments?.size)
        assertEquals("", result.comments?.get(0)?.content)

        verify(postRepository).findById(1L)
        verify(commentRepository).save(any<Comment>())
        verify(commentRepository).findAllByPost(post)
    }
}
