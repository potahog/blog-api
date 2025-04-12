package io.github.potahog.blog.integration

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.potahog.blog.domain.Post
import io.github.potahog.blog.dto.LoginRequest
import io.github.potahog.blog.dto.PostRequest
import io.github.potahog.blog.dto.SignUpRequest
import io.github.potahog.blog.repository.PostRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
class PostIntegrationTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper

    lateinit var token: String

    @BeforeEach
    fun setup() {
        val username = "potahog"
        val password = "1234"

        // 회원가입
        mockMvc.post("/api/auth/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(SignUpRequest(username, password))
        }

        // 로그인
        val result = mockMvc.post("/api/auth/login"){
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(LoginRequest(username, password))
        }.andReturn()

        val body = result.response.contentAsString
        token = objectMapper.readTree(body)["token"].asText()
    }

    @Test
    fun `게시글 작성, 조회, 수정, 삭제 통합테스트`() {
        // 1. 게시글 작성
        val createResult = mockMvc.post("/api/posts") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(PostRequest("제목", "내용"))
        }.andReturn()

        val createdId = objectMapper.readTree(createResult.response.contentAsString)["id"].asLong()

        // 2. 게시글 조회
        mockMvc.get("/api/posts/$createdId")
            .andExpect {
                status { isOk() }
                jsonPath("$.title") { value("제목") }
            }

        // 3. 게시글 수정
        mockMvc.put("/api/posts/$createdId") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(PostRequest("수정된 제목", "수정된 내용"))
        }.andExpect {
            status { isOk() }
            jsonPath("$.title") { value("수정된 제목") }
        }

        // 4. 게시글 삭제
        mockMvc.delete("/api/posts/$createdId") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isNoContent() }
        }
    }
}