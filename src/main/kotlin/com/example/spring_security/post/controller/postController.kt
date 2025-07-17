package com.example.spring_security.post.controller

import com.example.spring_security.common.dto.BaseResponse
import com.example.spring_security.post.dto.PostRequestDto
import com.example.spring_security.post.dto.PostResponseDto
import com.example.spring_security.post.repository.PostRepository
import com.example.spring_security.post.service.PostService
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
class postController (
    private val postService: PostService, private val postRepository: PostRepository
){
    @GetMapping
    fun getPosts() : ResponseEntity<BaseResponse<List<PostResponseDto>>>{
        val result = postService.getPostList()
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse(data = result))
    }

    @GetMapping("/{id}")
    fun getPostById(@PathVariable id: Long) : ResponseEntity<BaseResponse<PostResponseDto>> {
        val result = postService.getPostById(id)
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse(data = result))
    }

    @GetMapping("/{userId}/posts")
    fun getPostsByUserId(@PathVariable userId: Long) : ResponseEntity<BaseResponse<List<PostResponseDto>>>{
        val result = postService.getPostByUserId(userId)
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse(data = result))
    }

    @PostMapping("/{id}")
    fun postPosts(@Valid @RequestBody postRequestDto: PostRequestDto) : ResponseEntity<BaseResponse<PostResponseDto>> {
        val result = postService.postPost(postRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse(data = result))
    }

    @PutMapping("/{id}")
    fun putPost(@Valid @PathVariable id: Long, @RequestBody postRequestDto: PostRequestDto) : ResponseEntity<BaseResponse<PostResponseDto>>{
        val result = postService.putPost(id, postRequestDto)
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse(data = result))
    }

    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable id: Long) : ResponseEntity<BaseResponse<String>> {
        val result = postService.deletePostById(id)
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse(data = "게시글이 삭제되었습니다."))
    }
}