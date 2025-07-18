package com.example.spring_security.post.service

import com.example.spring_security.common.exception.post.PostException
import com.example.spring_security.post.dto.PostRequestDto
import com.example.spring_security.post.dto.PostResponseDto
import com.example.spring_security.post.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PostService (
    @Autowired
    private val postRepository : PostRepository,
){
    //게시물 ID 개별 조회
    fun getPostById(id: Long) : PostResponseDto {
        val result = postRepository.findByIdOrNull(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return result.toResponse()
    }

    //게시판 작성
    fun postPost(postRequestDto : PostRequestDto) : PostResponseDto {
        val result = postRepository.save(postRequestDto.toEntity())
        return result.toResponse()
    }

    //게시판 수정
    fun putPost(id: Long, postRequestDto: PostRequestDto) : PostResponseDto {
        val post = postRepository.findByIdOrNull(id) ?: throw PostException("존재하지 않는 게시글 ID 입니다.")

        val newPost = PostRequestDto(
            id = post.id,
            title = postRequestDto.title,
            post = postRequestDto.post,
            userId = postRequestDto.userId,
            public = postRequestDto.public
        )

        val result = postRepository.save(newPost.toEntity())

        return result.toResponse()
    }

    //게시글 삭제
    fun deletePostById(id: Long) : Unit {
        postRepository.deleteById(id)
    }
}