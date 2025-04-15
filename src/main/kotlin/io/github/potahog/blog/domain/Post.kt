package io.github.potahog.blog.domain

import jakarta.persistence.*

@Entity
data class Post (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var title: String,
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    val author: User,

    @Column(nullable = false)
    var isPublic: Boolean = true,

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    val comments: MutableList<Comment> = mutableListOf(),
) : BaseTimeEntity()
