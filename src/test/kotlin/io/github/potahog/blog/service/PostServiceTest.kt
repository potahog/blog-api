package io.github.potahog.blog.service

import io.github.potahog.blog.domain.Post
import io.github.potahog.blog.domain.User
import io.github.potahog.blog.dto.PostRequest
import io.github.potahog.blog.repository.PostRepository
import io.github.potahog.blog.service.PostService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.util.*
import kotlin.test.assertEquals


class PostServiceTest {
    private val postRepository: PostRepository = mock()
    private val postService = PostService(postRepository)

    private val user = User(1L, "potahog", "encoded")
    private val request = PostRequest(title = "제목", content = "내용")

    @BeforeEach
    fun setup(){
        // 테스트마다 SecurityContextHolder 초기화 할 수 있도록 추가 기능
    }

    @Test
    fun `게시글 생성 성공`() {
        val saved = Post(1L, request.title, request.content,  user)
        whenever(postRepository.save(any<Post>())).thenReturn(saved)

        // getCurrentUser()는 실제 서비스 코드에서 호출되므로,
        // 여기에선 서비스 내부 구현을 살짝 바꾸거나,
        // 나중에 getCCurrentUser를 주입 방식으로 바꿔야 단위 테스트에 더 유리함

        // 지금은 간단하게 PostService에 `create(request, user)` 형태로 만들었다고 가정
        val response = postService.createWithUser(request, user)

        assertEquals("제목", response.title)
        assertEquals("내용", response.content)
    }

    @Test
    fun `게시글 수정 성공`(){
        val original = Post(1L, "old", "old",  user)
        whenever(postRepository.findById(1L)).thenReturn(java.util.Optional.of(original))
        whenever(postRepository.save(any<Post>())).thenReturn(original.apply{
            title = request.title
            content = request.content
        })

        val response = postService.updateWithUser(1L, request, user)
    }
}