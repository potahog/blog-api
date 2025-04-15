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
}