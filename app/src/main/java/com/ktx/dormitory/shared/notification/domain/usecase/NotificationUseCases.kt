package com.ktx.dormitory.shared.notification.domain.usecase

import com.ktx.dormitory.shared.notification.domain.model.Notification
import com.ktx.dormitory.shared.notification.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(userId: String): Result<List<Notification>> = repository.getNotifications(userId)
}

class GetUnreadCountUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(userId: String): Result<Long> = repository.getUnreadCount(userId)
}

class MarkNotificationReadUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> = repository.markAsRead(id)
}

class MarkAllNotificationsReadUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(userId: String): Result<Unit> = repository.markAllRead(userId)
}

class GetIssueHistoryUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke() = repository.getIssueHistory()
}
