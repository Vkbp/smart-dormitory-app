package com.ktx.dormitory.shared.notification.domain.repository

import com.ktx.dormitory.shared.notification.domain.model.Notification

interface NotificationRepository {
    suspend fun getUnreadCount(userId: String): Result<Long>
    suspend fun getNotifications(userId: String): Result<List<Notification>>
    suspend fun markAsRead(id: Long): Result<Unit>
    suspend fun markAllRead(userId: String): Result<Unit>
    suspend fun reportIssue(description: String, roomId: String, imageUrl: String?): Result<String>
    suspend fun getIssueHistory(): Result<List<com.ktx.dormitory.shared.notification.domain.model.IssueReport>>
}
