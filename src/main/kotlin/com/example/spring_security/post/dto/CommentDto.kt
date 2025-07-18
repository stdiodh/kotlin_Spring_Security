package com.example.spring_security.post.dto

data class CommentRequestDto(
    val postId: Long,
    val content: String
)

data class CommentResponseDto(
    val id : Long?,
    val content : String
)