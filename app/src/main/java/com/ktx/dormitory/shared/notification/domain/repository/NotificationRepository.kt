package com.ktx.dormitory.shared.notification.domain.repository

import androidx.paging.PagingData
import com.ktx.dormitory.shared.notification.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun getUnreadCount(): Result<Long>
    fun getNotificationsPaging(): Flow<PagingData<Notification>>
    suspend fun markAsRead(id: Long): Result<Unit>
    suspend fun markAllRead(): Result<Unit>
}
