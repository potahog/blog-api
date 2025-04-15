package io.github.potahog.blog.domain

import jakarta.persistence.*

@Entity
data class Comment(

    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    var author: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    val post: Post,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    val parent: Comment? = null,

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
    val children: MutableList<Comment> = mutableListOf(),
) : BaseEntity()
