package com.ktx.dormitory.shared.notification.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.shared.auth.domain.usecase.GetAuthStateUseCase
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
    private val getAuthStateUseCase: GetAuthStateUseCase
) : BaseViewModel<NotificationUiState, NotificationUiEvent, NotificationUiEffect>(NotificationUiState()) {

    init {
        loadNotifications()
    }

    override fun onEvent(event: NotificationUiEvent) {
        when (event) {
            NotificationUiEvent.LoadNotifications -> loadNotifications()
            NotificationUiEvent.Refresh -> loadNotifications()
            is NotificationUiEvent.MarkAsRead -> markAsRead(event.notificationId)
            NotificationUiEvent.MarkAllAsRead -> markAllAsRead()
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            getAuthStateUseCase().onSuccess { user ->
                val userId = user.id ?: ""
                val notificationsResult = getNotificationsUseCase(userId)
                val unreadResult = getUnreadCountUseCase(userId)

                if (notificationsResult.isSuccess && unreadResult.isSuccess) {
                    updateState { it.copy(
                        notifications = notificationsResult.getOrThrow(),
                        unreadCount = unreadResult.getOrThrow().toInt(),
                        isLoading = false
                    ) }
                } else {
                    updateState { it.copy(
                        error = notificationsResult.exceptionOrNull()?.message ?: "Lỗi tải thông báo",
                        isLoading = false
                    ) }
                }
            }.onFailure {
                updateState { it.copy(error = "Không tìm thấy người dùng", isLoading = false) }
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
            getAuthStateUseCase().onSuccess { user ->
                markAllReadUseCase(user.id ?: "").onSuccess {
                    loadNotifications()
                }
            }
        }
    }
}
