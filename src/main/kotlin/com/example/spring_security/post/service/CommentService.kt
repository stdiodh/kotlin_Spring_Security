package com.example.spring_security.post.service

import com.example.spring_security.post.dto.CommentRequestDto
import com.example.spring_security.post.dto.CommentResponseDto
import com.example.spring_security.post.dto.PostResponseDto
import com.example.spring_security.post.entity.Comment
import com.example.spring_security.post.repository.CommentRepository
import com.example.spring_security.post.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommentService {
    //@Autowired 는 스프링에서 의존성을 자동으로 주입할 때 사용하는 어노테이션이다.
    //객체 간의 의존 관계를 코드 내부에서 직접 설정하는 대신, 외부(스프링 컨테이너)에서 주입해 주는 방식
    //스프링 컨테이너가 관리하는 객체를 말합니다.
    //해당 어노테이션이 붙은 필드나 매개변수와 타입이 일치하는 빈을 스프링 컨테이너에서 찾아 주입

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