package io.github.potahog.blog.controller

import io.github.potahog.blog.dto.PostRequest
import io.github.potahog.blog.dto.PostResponse
import io.github.potahog.blog.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts")
class PostController(private val postService: PostService) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: PostRequest): PostResponse = postService.create(request)

    @GetMapping
    fun getAll() : List<PostResponse> = postService.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): PostResponse = postService.getById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: PostRequest): PostResponse = postService.update(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = postService.delete(id)
}