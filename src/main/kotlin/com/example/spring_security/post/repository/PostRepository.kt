package com.example.spring_security.post.repository

import com.example.spring_security.post.entity.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PostRepository : JpaRepository<Post, Long>{
    @Query(value = "SELECT p FROM Post p LEFT JOIN FETCH p.comments")
    fun findAllByFetchJoin() : List<Post>
}