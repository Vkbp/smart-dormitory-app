package com.ktx.dormitory.shared.notification.presentation

import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.shared.notification.domain.model.Notification

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val error: String? = null
) : BaseContract.State

sealed class NotificationUiEvent : BaseContract.Event {
    data object LoadNotifications : NotificationUiEvent()
    data class MarkAsRead(val notificationId: Long) : NotificationUiEvent()
    data object MarkAllAsRead : NotificationUiEvent()
    data object Refresh : NotificationUiEvent()
}

sealed class NotificationUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : NotificationUiEffect()
}
