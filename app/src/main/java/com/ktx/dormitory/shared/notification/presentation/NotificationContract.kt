package com.ktx.dormitory.shared.notification.presentation

import androidx.paging.PagingData
import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.shared.notification.domain.model.Notification
import com.ktx.dormitory.shared.notification.domain.model.NotificationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class NotificationUiState(
    val isLoading: Boolean = false,
    val pagingFlow: Flow<PagingData<Notification>> = emptyFlow(),
    val selectedType: NotificationType = NotificationType.ALL,
    val unreadCount: Int = 0,
    val error: String? = null,
    val selectedNotification: Notification? = null,
    val readIds: Set<Long> = emptySet(),
    val isAllReadMarked: Boolean = false
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
