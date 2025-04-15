package io.github.potahog.blog.dto

data class CommentResponse(
    val id: Long,
    val content: String,
    val author: String,
    val children: List<CommentResponse> = emptyList(),
)
