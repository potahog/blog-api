package io.github.potahog.blog.dto

data class PostRequest(
    val title: String,
    val content: String,
    val isPublic: Boolean = true,
)

data class PostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val isPublic: Boolean,
)