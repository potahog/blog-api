package io.github.potahog.blog.service

import io.github.potahog.blog.domain.Comment
import io.github.potahog.blog.dto.CommentRequest
import io.github.potahog.blog.dto.CommentResponse
import io.github.potahog.blog.repository.CommentRepository
import io.github.potahog.blog.repository.PostRepository
import io.github.potahog.blog.util.getCurrentUser
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CommentService (
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
) {
    fun create (postId: Long, request: CommentRequest): CommentResponse {
        val user = getCurrentUser()
        val post = postRepository.findById(postId).orElseThrow{ IllegalArgumentException("Post not found") }

        val parent = request.parentId?.let {
            commentRepository.findById(it).orElseThrow{ IllegalArgumentException("Parent comment not found") }
        }

        val comment = Comment(
            content = request.content,
            author = user,
            post = post,
            parent = parent,
        )
        return commentRepository.save(comment).toResponse()
    }

    fun getComments(postId: Long): List<CommentResponse> {
        return commentRepository.findByPostIdAndDeletedAtIsNull(postId)
            .sortedBy { it.createdAt }
            .map{ it.toResponse() }
    }
    
    fun update(commentId: Long, request: CommentRequest): CommentResponse {
        val comment = commentRepository.findById(commentId).orElseThrow()
        val user = getCurrentUser()
        
        if(comment.author.id != user.id) throw IllegalArgumentException("댓글 수정 권한 없음")

        comment.content = request.content
        return commentRepository.save(comment).toResponse()
    }
    
    fun delete(commentId: Long) {
        val comment  = commentRepository.findById(commentId).orElseThrow()
        val user = getCurrentUser()
        
        if(comment.author.id != user.id) throw IllegalArgumentException("댓글 삭제 권한 없음")

//        commentRepository.delete(comment)
        comment.deletedAt = LocalDateTime.now()
        commentRepository.save(comment)
    }

    private fun Comment.toResponse() : CommentResponse {
        return CommentResponse(
            id = id,
            content = content,
            author = author.username,
            children = children.map { it.toResponse() }
        )
    }
}