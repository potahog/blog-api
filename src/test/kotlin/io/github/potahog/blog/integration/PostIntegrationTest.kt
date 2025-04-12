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
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
            content = objectMapper.writeValueAsString(PostRequest("제목", "내용", true))
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
            content = objectMapper.writeValueAsString(PostRequest("수정된 제목", "수정된 내용", true))
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

    @Test
    fun `목록 조회시 공개글만 반환되고, 내 비공개글은 같이 보임`() {

        // 1. 공개 글 1개, 비공개 글 1개 작성
        mockMvc.post("/api/posts") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(PostRequest("공개글", "내용1", true))
        }

        mockMvc.post("/api/posts") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(PostRequest("비공개글", "내용2", false))
        }

        // 2. 목록 조회
        val response = mockMvc.get("/api/posts") {
            header("Authorization", "Bearer $token")
        } .andReturn()
        
        val titles = objectMapper.readTree(response.response.contentAsString)
            .map { it["title"].asText() }
        
        assertTrue(titles.contains("공개글"))
        assertTrue(titles.contains("비공개글"))
    }
    
    @Test
    fun `비로그인 사용자는 공개글만 볼 수 있음`() {
        val response = mockMvc.get("/api/posts").andReturn()
        val titles = objectMapper.readTree(response.response.contentAsString)
            .map { it["title"].asText() }
        
        assertTrue (titles.contains("공개글"))
        assertFalse (titles.contains("비공개글"))
    }
}