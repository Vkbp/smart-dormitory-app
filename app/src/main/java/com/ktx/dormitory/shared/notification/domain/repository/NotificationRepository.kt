package com.ktx.dormitory.shared.notification.domain.repository

import com.ktx.dormitory.shared.notification.domain.model.Notification

interface NotificationRepository {
    suspend fun getUnreadCount(): Result<Long>
    suspend fun getNotifications(): Result<List<Notification>>
    suspend fun markAsRead(id: Long): Result<Unit>
    suspend fun markAllRead(): Result<Unit>
    suspend fun reportIssue(description: String, isCommonArea: Boolean): Result<String>
}
