package com.example.spring_security.post.controller

import com.example.spring_security.common.dto.BaseResponse
import com.example.spring_security.post.dto.PostRequestDto
import com.example.spring_security.post.dto.PostResponseDto
import com.example.spring_security.post.repository.PostRepository
import com.example.spring_security.post.service.PostService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@SecurityRequirement(name = "BearerAuth")
@Tag(name = "게시판 Api 컨트롤러", description = "게시판 전체 조회, id별 조회, 수정, 삭제 Api 명세서 입니다.")
@RestController
@RequestMapping("/api/post")
class PostController (
    private val postService: PostService, private val postRepository: PostRepository
){

    @Operation(summary = "게시글 전체 조회", description = "모든 게시글 목록을 조회합니다.")
    @GetMapping
    fun getPostList() : List<PostResponseDto>{
        val result = postRepository.findAllByFetchJoin()
        return result.map {it.toResponse()}
    }

    @Operation(summary = "게시글 ID로 조회", description = "게시글 ID를 통해 특정 게시글을 조회합니다.")
    @GetMapping("/{id}")
    fun getPostById(@PathVariable id: Long) : ResponseEntity<BaseResponse<PostResponseDto>> {
        val result = postService.getPostById(id)
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse(data = result))
    }

    @Operation(summary = "게시글 작성", description = "게시글을 작성합니다.")
    @PostMapping("/{id}")
    fun postPosts(@Valid @RequestBody postRequestDto: PostRequestDto) : ResponseEntity<BaseResponse<PostResponseDto>> {
        val result = postService.postPost(postRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse(data = result))
    }

    @Operation(summary = "게시글 수정", description = "게시글 ID를 통해 게시글 내용을 수정합니다.")
    @PutMapping("/{id}")
    fun putPost(@Valid @PathVariable id: Long, @RequestBody postRequestDto: PostRequestDto) : ResponseEntity<BaseResponse<PostResponseDto>>{
        val result = postService.putPost(id, postRequestDto)
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse(data = result))
    }

    @Operation(summary = "게시글 삭제", description = "게시글 ID를 통해 게시글을 삭제합니다.")
    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable id: Long) : ResponseEntity<BaseResponse<String>> {
        val result = postService.deletePostById(id)
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse(data = "게시글이 삭제되었습니다."))
    }
}
