package com.example.spring_security.post.entity

import com.example.spring_security.post.dto.CommentResponseDto
import jakarta.persistence.*

@Entity
class Comment (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long?,

    @Column(nullable = false, length = 1000)
    var content : String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = ForeignKey(name = "fk_comment_post_id"))
    val post : Post,
){
    fun toResponse() : CommentResponseDto = CommentResponseDto(
        id = id,
        content = content
    )
}