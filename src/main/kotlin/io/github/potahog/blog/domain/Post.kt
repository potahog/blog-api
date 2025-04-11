package io.github.potahog.blog.domain

import jakarta.persistence.*

@Entity
data class Post(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val title: String,
    val content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    val author: User,
)
