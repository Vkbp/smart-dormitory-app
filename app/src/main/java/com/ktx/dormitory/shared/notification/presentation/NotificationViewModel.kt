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
                val serverUnreadCount = unreadResult.getOrThrow().toInt()
                val listUnreadCount = notifications.count { !it.isRead }
                
                // Nếu số lượng chưa đọc trong list (vừa tải về) là 0, 
                // thì dù server trả về > 0 (do lag đồng bộ), ta vẫn nên ưu tiên hiển thị 0 
                // để tránh gây khó chịu cho người dùng (vừa đọc xong lại thấy hiện số).
                val displayUnreadCount = if (listUnreadCount == 0 && notifications.isNotEmpty()) 0 else serverUnreadCount

                updateState { it.copy(
                    notifications = notifications,
                    filteredNotifications = applyFilter(notifications, it.selectedType),
                    unreadCount = displayUnreadCount,
                    isLoading = false
                ) }
            }
else {
                updateState { it.copy(
                    error = notificationsResult.exceptionOrNull()?.message ?: "Lỗi tải thông báo",
                    isLoading = false
                ) }
            }
        }
    }

    private fun markAsRead(id: Long) {
        // Optimistic UI Update
        val updatedNotifications = currentState.notifications.map {
            if (it.id == id && !it.isRead) it.copy(isRead = true) else it
        }
        val unreadCountDelta = if (currentState.notifications.find { it.id == id }?.isRead == false) 1 else 0
        
        updateState { it.copy(
            notifications = updatedNotifications,
            filteredNotifications = applyFilter(updatedNotifications, it.selectedType),
            unreadCount = (it.unreadCount - unreadCountDelta).coerceAtLeast(0)
        ) }

        viewModelScope.launch {
            markReadUseCase(id).onFailure {
                // If failed, reload to sync with server state
                loadNotifications()
            }
        }
    }

    private fun markAllAsRead() {
        // Optimistic UI Update
        val updatedNotifications = currentState.notifications.map { it.copy(isRead = true) }
        updateState { it.copy(
            notifications = updatedNotifications,
            filteredNotifications = applyFilter(updatedNotifications, it.selectedType),
            unreadCount = 0
        ) }

        viewModelScope.launch {
            markAllReadUseCase().onFailure {
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
                    val typeUpper = it.type?.uppercase() ?: ""
                    val isPaymentType = typeUpper in listOf(
                        "PAYMENT", "ELECTRIC_FEE", "ACCOMMODATION_FEE",
                        "PENALTY_FEE", "BILL", "INVOICE", "PAYMENT_NOTICE"
                    )
                    val containsKeyword = it.title.contains("hóa đơn", ignoreCase = true) ||
                            it.title.contains("thanh toán", ignoreCase = true) ||
                            it.message.contains("tiền điện", ignoreCase = true)
                    
                    isPaymentType || containsKeyword
                }
            }
            else -> notifications.filter { it.type?.uppercase() == type.name }
        }
    }
}
