package io.github.potahog.blog.controller

import io.github.potahog.blog.domain.Post
import io.github.potahog.blog.dto.CommentRequest
import io.github.potahog.blog.dto.CommentResponse
import io.github.potahog.blog.repository.CommentRepository
import io.github.potahog.blog.service.CommentService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts/{postId}/comments")
class CommentController(
    private val commentService: CommentService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable postId: Long,
        @RequestBody request: CommentRequest
    ) : CommentResponse = commentService.create(postId, request)

    @GetMapping
    fun getComments(@PathVariable postId: Long) : List<CommentResponse> = commentService.getComments(postId)
}

@RestController
@RequestMapping("/api/comments")
class SingleCommentController(
    private val commentService: CommentService
){
    @PutMapping("/{commentId}")
    fun update(
        @PathVariable commentId: Long,
        @RequestBody request: CommentRequest
    ):CommentResponse = commentService.update(commentId, request)

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable commentId: Long) {
        commentService.delete(commentId)
    }
}