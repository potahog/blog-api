package io.github.potahog.blog.repository

import io.github.potahog.blog.domain.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface PostRepository : JpaRepository<Post, Long>{
    @Modifying
    @Query("DELETE FROM Post p WHERE p.deletedAt IS NOT NULL AND p.deletedAt < :cutoff")
    fun hardDeleteOlderThan(@Param("cutoff") cutoff: LocalDateTime): Int

    // 게시글 전체 중 살아있는 (삭제되지 않은) 글만 조회
    fun findAllByDeletedAtIsNull(): List<Post>

    // 특정 작성자의 게시글 중 살아있는 것만 조회
    fun findByAuthorIdAndDeletedAtIsNull(authorId: Long): List<Post>

    // 게시글 단건 조회 (soft delete 제외)
    fun findByIdAndDeletedAtIsNull(id: Long): Post?
}