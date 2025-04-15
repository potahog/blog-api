package io.github.potahog.blog.repository

import io.github.potahog.blog.domain.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface CommentRepository: JpaRepository<Comment, Long> {
    fun findByPostId(postId: Long): List<Comment>
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.deletedAt IS NOT NULL AND c.deletedAt < :cutoff")
    fun hardDeleteOlderThan(@Param("cutoff") cutoff: LocalDateTime): Int
    fun findByPostIdAndDeletedAtIsNull(postId: Long): List<Comment>
    fun findByPostIdAndParentIsNullAndDeletedAtIsNull(postId: Long): List<Comment>
}