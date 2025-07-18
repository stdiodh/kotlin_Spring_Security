package com.example.spring_security.post.service

import com.example.spring_security.common.exception.post.PostException
import com.example.spring_security.post.dto.CommentRequestDto
import com.example.spring_security.post.dto.CommentResponseDto
import com.example.spring_security.post.dto.PostResponseDto
import com.example.spring_security.post.entity.Comment
import com.example.spring_security.post.entity.Post
import com.example.spring_security.post.repository.CommentRepository
import com.example.spring_security.post.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CommentService {
    @Autowired
    private lateinit var commentRepository: CommentRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    //댓글 가져오기 기능
    fun getComments() : List<CommentResponseDto> {
        val result = commentRepository.findAll()
        return result.map { it.toResponse()}
    }

    //댓글 달기 기능
    fun createComment(request: CommentRequestDto): PostResponseDto {
        val post = postRepository.findById(request.postId)
            .orElseThrow { IllegalArgumentException("게시글을 찾을 수 없습니다.") }

        val comment = Comment(
            id = null,
            content = request.content,
            post = post
        )

        commentRepository.save(comment)

        val comments = commentRepository.findAllByPost(post)
        return PostResponseDto(
            id = post.id,
            title = post.title,
            post = post.post,
            userId = post.userId,
            public = post.public,
            comments = comments.map { it.toResponse() }
        )
    }
}