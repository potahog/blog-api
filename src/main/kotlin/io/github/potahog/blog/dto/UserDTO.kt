package io.github.potahog.blog.dto

data class SignUpRequest(val username: String, val password: String)
data class LoginRequest(val username: String, val password: String)
data class TokenResponse(val token: String)
