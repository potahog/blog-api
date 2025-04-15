package io.github.potahog.blog.domain

import jakarta.persistence.*
import java.util.Date

@Entity
data class Comment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

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
)
