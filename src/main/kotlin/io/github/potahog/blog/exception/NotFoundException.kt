package io.github.potahog.blog.exception

enum class ResourceType { POST, COMMENT, USER}

class NotFoundException(message: String) : RuntimeException(message)