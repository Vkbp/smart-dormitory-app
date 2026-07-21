package com.ktx.dormitory.shared.notification.domain.usecase

import com.ktx.dormitory.shared.notification.domain.model.Notification
import com.ktx.dormitory.shared.notification.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(): Result<List<Notification>> = repository.getNotifications()
}

class GetUnreadCountUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(): Result<Long> = repository.getUnreadCount()
}

class MarkNotificationReadUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> = repository.markAsRead(id)
}

class MarkAllNotificationsReadUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.markAllRead()
}
