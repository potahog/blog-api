package io.github.potahog.blog.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ApiError> =
        ResponseEntity(ApiError("NOT_FOUND", ex.message ?: "Not found"), HttpStatus.NOT_FOUND)

    @ExceptionHandler(Exception::class)
    fun hendleDefeault(ex: Exception): ResponseEntity<ApiError> =
        ResponseEntity(ApiError("INTERNAL_SERVER_ERROR", ex.message ?: "Unexpected error"), HttpStatus.INTERNAL_SERVER_ERROR)
}

data class ApiError(
    val code: String,
    val message: String
)