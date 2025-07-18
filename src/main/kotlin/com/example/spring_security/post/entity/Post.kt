package com.example.spring_security.post.entity

import com.example.spring_security.post.dto.PostResponseDto
import jakarta.persistence.*

@Entity
class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long?,

    @Column(nullable = false, length = 100)
    var title: String,

    @Column(nullable = false, length = 2000)
    var post: String,

    @Column(nullable = false, length = 50)
    val userId: Long,

    @Column(nullable = false, length = 10)
    var public: Boolean

){
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = [CascadeType.ALL])
    var comments : List<Comment>? = null

    fun toResponse() : PostResponseDto = PostResponseDto(
        id = id,
        title = title,
        post = post,
        userId = userId,
        public = public,
        comments = comments?.map { it.toResponse() }
    )
}