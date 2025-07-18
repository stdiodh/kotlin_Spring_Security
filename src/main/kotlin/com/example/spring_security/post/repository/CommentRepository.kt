package com.example.spring_security.post.repository

import com.example.spring_security.post.entity.Comment
import com.example.spring_security.post.entity.Post
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long?>{
    fun findAllByPost(post: Post): List<Comment>
}