package com.example.spring_security.post.service

import com.example.spring_security.common.exception.post.PostException
import com.example.spring_security.post.dto.PostRequestDto
import com.example.spring_security.post.entity.Post
import com.example.spring_security.post.repository.PostRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.any
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import java.util.Optional

//Mockito를 JUnit5 테스트에서 원활히 사용하기 위한 필수 설정
@ExtendWith(MockitoExtension::class)
class PostServiceTest {

    @Mock
    private lateinit var postRepository: PostRepository

    @InjectMocks
    private lateinit var postService: PostService

    @Test
    fun `게시글 ID로 조회 성공`() {
        val id = 1L
        val post = Post(id, "제목", "내용", 1L, true).apply { comments = emptyList() }

        given(postRepository.findById(id)).willReturn(Optional.of(post))

        val result = postService.getPostById(id)

        assertEquals(id, result.id)
        assertEquals("제목", result.title)
        assertEquals("내용", result.post)
        assertEquals(1L, result.userId)
        assertEquals(true, result.public)

        verify(postRepository).findById(id)
    }

    @Test
    fun `게시글 ID로 조회 실패`() {
        val id = 999L

        given(postRepository.findById(id)).willReturn(Optional.empty())

        val exception = assertThrows<ResponseStatusException> {
            postService.getPostById(id)
        }
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)

        verify(postRepository).findById(id)
    }


    @Test
    fun `게시글 작성 성공`() {
        // given: 게시글 작성 요청과 저장 결과를 설정
        val request = PostRequestDto(null, "새 제목", "새 내용", 1L, true)
        val entity = request.toEntity()
        val savedPost = Post(1L, "새 제목", "새 내용", 1L, true)
        given(postRepository.save(any<Post>())).willReturn(savedPost)

        // when: 게시글 작성 요청 실행
        val result = postService.postPost(request)

        // then: 작성된 게시글 결과가 기대와 일치하는지 확인
        assertEquals(1L, result.id)
        assertEquals("새 제목", result.title)
        assertEquals("새 내용", result.post)
        assertEquals(1L, result.userId)
        assertEquals(true, result.public)
        verify(postRepository).save(any<Post>())
    }

    @Test
    fun `게시글 수정 성공`() {
        // given: 기존 게시글과 수정 요청, 수정 결과 설정
        val id = 1L
        val existingPost = Post(id, "기존 제목", "기존 내용", 1L, true)
        val updateRequest = PostRequestDto(null, "수정된 제목", "수정된 내용", 1L, false)
        val updatedPost = Post(id, "수정된 제목", "수정된 내용", 1L, false)

        given(postRepository.findById(id)).willReturn(Optional.of(existingPost))
        given(postRepository.save(any<Post>())).willReturn(updatedPost)

        // when: 수정 메서드 호출
        val result = postService.putPost(id, updateRequest)

        // then: 수정 결과 검증
        assertEquals(id, result.id)
        assertEquals("수정된 제목", result.title)
        assertEquals("수정된 내용", result.post)
        assertEquals(false, result.public)
        verify(postRepository).findById(id)
        verify(postRepository).save(any<Post>())
    }

    @Test
    fun `게시글 수정 실패 - 존재하지 않는 게시글`() {
        // given: 수정 대상 게시글이 존재하지 않는 상황
        val id = 999L
        val updateRequest = PostRequestDto(null, "수정 제목", "수정 내용", 1L, true)
        given(postRepository.findById(id)).willReturn(Optional.empty())

        // when & then: PostException 발생 여부 확인
        val exception = assertThrows(PostException::class.java) {
            postService.putPost(id, updateRequest)
        }
        assertEquals("존재하지 않는 게시글 ID 입니다.", exception.message)
        verify(postRepository).findById(id)
    }


    @Test
    fun `게시글 삭제 성공`() {
        // given: 게시글 ID 설정
        val id = 1L

        // when: 삭제 메서드 호출
        postService.deletePostById(id)

        // then: 리포지토리의 deleteById가 호출되었는지 확인
        verify(postRepository).deleteById(id)
    }

    @Test
    fun `빈 제목으로 게시글 작성 시 유효성 검증`() {
        // given: 제목이 빈 문자열인 요청 생성
        val request = PostRequestDto(null, "", "내용", 1L, true)
        val entity = request.toEntity()
        val savedPost = Post(1L, "", "내용", 1L, true)
        given(postRepository.save(any<Post>())).willReturn(savedPost)

        // when: 게시글 작성 시도
        val result = postService.postPost(request)

        // then: 서비스 단에서는 통과하므로 그대로 저장됨 (Controller에서 검증 필요)
        assertEquals("", result.title)
        verify(postRepository).save(any<Post>())
    }

    @Test
    fun `잘못된 사용자 ID로 게시글 작성`() {
        // given: userId가 0인 잘못된 요청
        val request = PostRequestDto(null, "제목", "내용", 0L, true)
        val entity = request.toEntity()
        val savedPost = Post(1L, "제목", "내용", 0L, true)
        given(postRepository.save(any<Post>())).willReturn(savedPost)

        // when: 게시글 작성 시도
        val result = postService.postPost(request)

        // then: Service 레벨에서는 그대로 저장됨 (Controller에서 검증 필요)
        assertEquals(0L, result.userId)
        verify(postRepository).save(any<Post>())
    }
}
