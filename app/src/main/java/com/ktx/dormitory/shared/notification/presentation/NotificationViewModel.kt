package com.ktx.dormitory.shared.notification.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.shared.notification.domain.model.Notification
import com.ktx.dormitory.shared.notification.domain.model.NotificationType
import com.ktx.dormitory.shared.notification.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val getUnreadCountUseCase: GetUnreadCountUseCase,
    private val markReadUseCase: MarkNotificationReadUseCase,
    private val markAllReadUseCase: MarkAllNotificationsReadUseCase,
) : BaseViewModel<NotificationUiState, NotificationUiEvent, NotificationUiEffect>(NotificationUiState()) {

    init {
        loadNotifications()
    }

    fun refresh() {
        loadNotifications()
    }

    override fun onEvent(event: NotificationUiEvent) {
        when (event) {
            NotificationUiEvent.LoadNotifications -> loadNotifications()
            NotificationUiEvent.Refresh -> loadNotifications()
            is NotificationUiEvent.MarkAsRead -> markAsRead(event.notificationId)
            NotificationUiEvent.MarkAllAsRead -> markAllAsRead()
            is NotificationUiEvent.FilterByType -> filterNotifications(event.type)
            is NotificationUiEvent.SelectNotification -> {
                updateState { it.copy(selectedNotification = event.notification) }
            }
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            val notificationsResult = getNotificationsUseCase()
            val unreadResult = getUnreadCountUseCase()

            if (notificationsResult.isSuccess && unreadResult.isSuccess) {
                val notifications = notificationsResult.getOrThrow()
                updateState { it.copy(
                    notifications = notifications,
                    filteredNotifications = applyFilter(notifications, it.selectedType),
                    unreadCount = unreadResult.getOrThrow().toInt(),
                    isLoading = false
                ) }
            } else {
                updateState { it.copy(
                    error = notificationsResult.exceptionOrNull()?.message ?: "Lỗi tải thông báo",
                    isLoading = false
                ) }
            }
        }
    }

    private fun markAsRead(id: Long) {
        viewModelScope.launch {
            markReadUseCase(id).onSuccess {
                loadNotifications()
            }
        }
    }

    private fun markAllAsRead() {
        viewModelScope.launch {
            markAllReadUseCase().onSuccess {
                loadNotifications()
            }
        }
    }

    private fun filterNotifications(type: NotificationType) {
        updateState { it.copy(
            selectedType = type,
            filteredNotifications = applyFilter(it.notifications, type)
        ) }
    }

    private fun applyFilter(notifications: List<Notification>, type: NotificationType): List<Notification> {
        return when (type) {
            NotificationType.ALL -> notifications
            NotificationType.PAYMENT -> {
                notifications.filter {
                    it.type?.uppercase() in listOf(
                        "PAYMENT", "ELECTRIC_FEE",
                        "ACCOMMODATION_FEE", "APPLICATION_FEE",
                        "PENALTY_FEE", "DEPOSIT_FEE"
                    )
                }
            }
            else -> notifications.filter { it.type?.uppercase() == type.name }
        }
    }
}
