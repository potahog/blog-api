package io.github.potahog.blog.repository

import io.github.potahog.blog.domain.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long>