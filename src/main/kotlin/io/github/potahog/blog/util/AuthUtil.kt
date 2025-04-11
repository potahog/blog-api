package io.github.potahog.blog.util

import io.github.potahog.blog.domain.User
import org.springframework.security.core.context.SecurityContextHolder

fun getCurrentUser(): User {
    val auth = SecurityContextHolder.getContext().authentication
    return auth?.principal as? User ?: throw IllegalStateException("인증되지 않은 사용자입니다.")
}