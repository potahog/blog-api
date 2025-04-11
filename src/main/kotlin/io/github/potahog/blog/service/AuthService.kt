package io.github.potahog.blog.service

import io.github.potahog.blog.domain.User
import io.github.potahog.blog.dto.*
import io.github.potahog.blog.repository.UserRepository
import io.github.potahog.blog.security.JwtUtil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService (
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
){
    private val passwordEncoder = BCryptPasswordEncoder()

    fun signUp(request: SignUpRequest) {
        if(userRepository.findByUsername(request.username) != null) {
            throw IllegalArgumentException("Username ${request.username} already exists")
        }

        val user = User(
            username = request.username,
            password = passwordEncoder.encode(request.password),
        )
        userRepository.save(user)
    }

    fun login(request: LoginRequest): TokenResponse {
        val user = userRepository.findByUsername(request.username) ?: throw IllegalArgumentException("Username ${request.username} not found")

        if(!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Password ${request.password} is not valid")
        }

        val token = jwtUtil.generateToken(user.username)
        return TokenResponse(token)
    }
}