package io.github.potahog.blog

import io.github.potahog.blog.repository.CommentRepository
import io.github.potahog.blog.repository.PostRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class HardDeleteScheduler (
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    @Transactional
    fun hardDeleteOldSoftDeleted() {
        val cutoff = LocalDateTime.now().minusDays(30)

        val deletedComments = commentRepository.hardDeleteOlderThan(cutoff)
        val deletedPosts = postRepository.hardDeleteOlderThan(cutoff)

        log.info("🧹 30일 지난 댓글 ${deletedComments}개 하드 삭제 완료")
        log.info("🧹 30일 지난 게시글 ${deletedPosts}개 하드 삭제 완료")
    }
}