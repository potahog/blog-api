package io.github.potahog.blog.service

import io.github.potahog.blog.domain.Post
import io.github.potahog.blog.domain.User
import io.github.potahog.blog.dto.PostRequest
import io.github.potahog.blog.dto.PostResponse
import io.github.potahog.blog.exception.NotFoundException
import io.github.potahog.blog.repository.PostRepository
import io.github.potahog.blog.util.*
import org.springframework.stereotype.Service

@Service
class PostService (private val postRepository: PostRepository) {
    fun create(request: PostRequest): PostResponse {
        val currentUser = getCurrentUser()

        return createWithUser(request, currentUser)
//        val post = Post(title = request.title, content = request.content, author = currentUser)
//        return postRepository.save(post).toResponse()
    }

    fun getAll(): List<PostResponse> {
        val currentUser = getCurrentUserOrNull()
        val posts= postRepository.findAll().filter { post ->
            post.isPublic || (currentUser != null && post.author.id == currentUser.id)
        }

        return posts.map { it.toResponse() }
    }

    fun getById(id: Long): PostResponse {
        val post = postRepository.findById(id).orElseThrow { NotFoundException("Post with id $id not found") }
        val currentUser = getCurrentUserOrNull()

        if(!post.isPublic && (currentUser == null || post.author.id != currentUser.id)) {
            throw IllegalAccessException("Post is private")
        }

        return post.toResponse()

    }

    fun update(id: Long, request: PostRequest): PostResponse {
        val post = postRepository.findById(id).orElseThrow{ NotFoundException("Cannot update: Post with id $id does not exist") }

        val currentUser = getCurrentUser()

        return updateWithUser(id, request, currentUser)
//        if(post.author.id != currentUser.id){
//            throw IllegalStateException("해당 게시글을 수정할 권한이 없습니다.")
//        }
//
//        post.title = request.title
//        post.content = request.content
//        return postRepository.save(post).toResponse()
    }

    fun delete(id: Long) {
        val post = postRepository.findById(id).orElseThrow{ NotFoundException("Cannot delete: Post with id $id does not exist") }

        val currentUser = getCurrentUser()

        if(post.author.id != currentUser.id){
            throw IllegalStateException("해당 게시글을 삭제할 권한이 없습니다.")
        }

        postRepository.delete(post)
    }

    private fun Post.toResponse(): PostResponse =
        PostResponse(
            id = id,
            title = title,
            content = content,
            isPublic = isPublic
        )

    fun createWithUser(request: PostRequest, user: User): PostResponse {
        val post = Post(
            title = request.title,
            content = request.content,
            author = user,
            isPublic = request.isPublic
        )
        return postRepository.save(post).toResponse()
    }

    fun updateWithUser(id: Long, request: PostRequest, user: User): PostResponse {
        val post = postRepository.findById(id).orElseThrow{ NotFoundException("Cannot update: Post with id $id does not exist") }

        if(post.author.id != user.id){
            throw IllegalStateException("해당 게시글을 수정할 권한이 없습니다.")
        }

        post.title = request.title
        post.content = request.content
        post.isPublic = request.isPublic
        return postRepository.save(post).toResponse()
    }

    fun delete(id: Long, user: User){
        val post = postRepository.findById(id).orElseThrow{ NotFoundException("Cannot delete: Post with id $id does not exist") }

        if(post.author.id != user.id){
            throw IllegalStateException("해당 게시글을 삭제할 권한이 없습니다.")
        }

        postRepository.delete(post)
    }
}