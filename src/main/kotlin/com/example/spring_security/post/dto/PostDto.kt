package com.example.spring_security.post.dto

import com.example.spring_security.post.entity.Post
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class PostRequestDto (
    var id : Long?,
    @field:NotBlank(message = "제목을 반드시 입력되어야 합니다!")
    var title : String,
    @field:NotBlank(message = "내용은 반드시 입력되어야 합니다!")
    var post : String,
    @field:Min(value = 1, message = "유효하지 않은 사용자입니다!")
    var userId : Long,
    var public : Boolean,
){
    fun toEntity() : Post = Post(
        id = id,
        title = title,
        post = post,
        userId = userId,
        public = public
    )
}

data class PostResponseDto (
    var id : Long?,
    var title : String,
    var post : String,
    var userId : Long,
    var public : Boolean,
    var comments : List<CommentResponseDto>?
)