package com.ktx.dormitory.shared.notification.presentation

import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.shared.notification.domain.model.Notification
import com.ktx.dormitory.shared.notification.domain.model.NotificationType

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val filteredNotifications: List<Notification> = emptyList(),
    val selectedType: NotificationType = NotificationType.ALL,
    val unreadCount: Int = 0,
    val error: String? = null,
    val selectedNotification: Notification? = null
) : BaseContract.State

sealed class NotificationUiEvent : BaseContract.Event {
    data object LoadNotifications : NotificationUiEvent()
    data class MarkAsRead(val notificationId: Long) : NotificationUiEvent()
    data object MarkAllAsRead : NotificationUiEvent()
    data object Refresh : NotificationUiEvent()
    data class FilterByType(val type: NotificationType) : NotificationUiEvent()
    data class SelectNotification(val notification: Notification?) : NotificationUiEvent()
}

sealed class NotificationUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : NotificationUiEffect()
}
