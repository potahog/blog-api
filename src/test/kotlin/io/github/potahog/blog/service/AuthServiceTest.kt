package io.github.potahog.blog.service

import io.github.potahog.blog.domain.User
import io.github.potahog.blog.dto.LoginRequest
import io.github.potahog.blog.dto.SignUpRequest
import io.github.potahog.blog.repository.UserRepository
import io.github.potahog.blog.security.JwtUtil
import io.github.potahog.blog.service.AuthService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AuthServiceTest {

    private val userRepository: UserRepository = mock()
    private val jwtUtil: JwtUtil = mock()
    private val authService = AuthService(userRepository, jwtUtil)

    @Test
    fun `회원가입 성공`() {
        val request = SignUpRequest("potahog", "1234")

        whenever(userRepository.findByUsername("potahog")).thenReturn(null)

        val user = User(1L, "potahog", "encoded")
        whenever(userRepository.save(any<User>())).thenReturn(user)

        assertDoesNotThrow {
            authService.signUp(request)
        }
    }

    @Test
    fun `로그인 성공`(){
        val passwordEncoder = BCryptPasswordEncoder()
        val rawPassword = "1234"
        val encoded = passwordEncoder.encode(rawPassword)

        val user = User(1, "potahog", encoded)

        whenever(userRepository.findByUsername("potahog")).thenReturn(user)
        whenever(jwtUtil.generateToken("potahog")).thenReturn("mock-token")

        val response = authService.login(LoginRequest("potahog", "1234"))
        assertEquals("mock-token", response.token)
    }

    @Test
    fun `로그인 실패 - 비밀번호 틀림`() {
        val encoded = BCryptPasswordEncoder().encode("correct")
        val user = User(1, "potahog", encoded)

        whenever(userRepository.findByUsername("potahog")).thenReturn(user)

        val ex = assertThrows(IllegalArgumentException::class.java) {
            authService.login(LoginRequest("potahog", "wrong"))
        }
        assertTrue(ex.message!!.contains("비밀번호"))
    }
}