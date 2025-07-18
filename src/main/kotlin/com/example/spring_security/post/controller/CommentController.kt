package com.example.spring_security.post.controller

import com.example.spring_security.common.dto.BaseResponse
import com.example.spring_security.post.dto.CommentRequestDto
import com.example.spring_security.post.dto.CommentResponseDto
import com.example.spring_security.post.dto.PostResponseDto
import com.example.spring_security.post.service.CommentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@SecurityRequirement(name = "BearerAuth")
@Tag(name = "댓글 Api 컨트롤러", description = "댓글 등록, 조회 API 명세서 입니다.")

@RestController
@RequestMapping("/api/posts/comments")
class CommentController {

    @Autowired
    private lateinit var commentService: CommentService

    @Operation(summary = "댓글 조회", description = "모든 댓글 목록을 조회합니다.")
    @GetMapping
    fun getComments() : ResponseEntity<BaseResponse<List<CommentResponseDto>>> {
        val result = commentService.getComments()
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse(data = result))
    }

    @Operation(summary = "게시글 댓글 등록", description = "게시글 ID에 해당하는 게시글에 댓글을 등록합니다.")
    @PostMapping
    fun addComment(@RequestBody request: CommentRequestDto): ResponseEntity<PostResponseDto> {
        val response = commentService.createComment(request)
        return ResponseEntity.ok(response)
    }
}
