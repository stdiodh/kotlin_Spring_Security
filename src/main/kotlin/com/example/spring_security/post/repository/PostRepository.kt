package com.example.spring_security.post.repository

import com.example.spring_security.post.entity.Post
import org.springframework.data.repository.CrudRepository

interface PostRepository : CrudRepository<Post, Long>{
    fun findByuserId(userId : Long) : List<Post>
}