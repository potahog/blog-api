package io.github.potahog.blog.service

import io.github.potahog.blog.domain.Post
import io.github.potahog.blog.dto.PostRequest
import io.github.potahog.blog.dto.PostResponse
import io.github.potahog.blog.repository.PostRepository
import org.springframework.stereotype.Service

@Service
class PostService (private val postRepository: PostRepository) {
    fun create(request: PostRequest): PostResponse {
        val post = Post(title = request.title, content = request.content)
        return postRepository.save(post).toResponse()
    }

    fun getAll(): List<PostResponse> = postRepository.findAll().map{ it.toResponse() }

    fun getById(id: Long): PostResponse =
        postRepository.findById(id).orElseThrow().toResponse()

    fun update(id: Long, request: PostRequest): PostResponse {
        val post = postRepository.findById(id).orElseThrow()
        post.title = request.title
        post.content = request.content
        return postRepository.save(post).toResponse()
    }

    fun delete(id: Long) {
        postRepository.deleteById(id)
    }

    private fun Post.toResponse(): PostResponse = PostResponse(id = id, title = title, content = content)
}