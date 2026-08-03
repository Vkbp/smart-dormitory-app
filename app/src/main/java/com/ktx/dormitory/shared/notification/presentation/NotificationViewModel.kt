package com.ktx.dormitory.shared.notification.presentation

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.shared.notification.domain.model.Notification
import com.ktx.dormitory.shared.notification.domain.model.NotificationType
import com.ktx.dormitory.shared.notification.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val getUnreadCountUseCase: GetUnreadCountUseCase,
    private val markReadUseCase: MarkNotificationReadUseCase,
    private val markAllReadUseCase: MarkAllNotificationsReadUseCase,
) : BaseViewModel<NotificationUiState, NotificationUiEvent, NotificationUiEffect>(NotificationUiState()) {

    init {
        loadNotifications()
        fetchUnreadCount()
    }

    fun refresh() {
        fetchUnreadCount()
        // Paging 3 manages its own refresh, but we can re-trigger the flow if needed
    }

    override fun onEvent(event: NotificationUiEvent) {
        when (event) {
            NotificationUiEvent.LoadNotifications -> loadNotifications()
            NotificationUiEvent.Refresh -> {
                loadNotifications()
                fetchUnreadCount()
            }
            is NotificationUiEvent.MarkAsRead -> markAsRead(event.notificationId)
            NotificationUiEvent.MarkAllAsRead -> markAllAsRead()
            is NotificationUiEvent.FilterByType -> {
                updateState { it.copy(selectedType = event.type) }
            }
            is NotificationUiEvent.SelectNotification -> {
                updateState { it.copy(selectedNotification = event.notification) }
            }
        }
    }

    private fun loadNotifications() {
        val pagingFlow = uiState.map { it.selectedType }
            .flatMapLatest { type ->
                getNotificationsUseCase()
                    .map { pagingData ->
                        pagingData.filter { notification ->
                            applyFilterLogic(notification, type)
                        }
                    }
            }
            .cachedIn(viewModelScope)

        updateState { it.copy(pagingFlow = pagingFlow) }
    }

    private fun fetchUnreadCount() {
        viewModelScope.launch {
            getUnreadCountUseCase().onSuccess { count ->
                updateState { it.copy(unreadCount = count.toInt()) }
            }
        }
    }

    private fun markAsRead(id: Long) {
        // Optimistic UI Update (limited in Paging 3, usually we wait for refresh or use a local state wrapper)
        // For simplicity with Paging 3, we'll decrease count and wait for list refresh if necessary
        updateState { it.copy(unreadCount = (it.unreadCount - 1).coerceAtLeast(0)) }

        viewModelScope.launch {
            markReadUseCase(id).onFailure {
                fetchUnreadCount()
            }
        }
    }

    private fun markAllAsRead() {
        updateState { it.copy(unreadCount = 0) }
        viewModelScope.launch {
            markAllReadUseCase().onFailure {
                fetchUnreadCount()
            }
        }
    }

    private fun applyFilterLogic(notification: Notification, type: NotificationType): Boolean {
        if (type == NotificationType.ALL) return true
        
        val typeUpper = notification.type?.uppercase() ?: ""
        
        return if (type == NotificationType.PAYMENT) {
            val isPaymentType = typeUpper in listOf(
                "PAYMENT", "ELECTRIC_FEE", "ACCOMMODATION_FEE",
                "PENALTY_FEE", "BILL", "INVOICE", "PAYMENT_NOTICE"
            )
            val containsKeyword = notification.title.contains("hóa đơn", ignoreCase = true) ||
                    notification.title.contains("thanh toán", ignoreCase = true) ||
                    notification.message.contains("tiền điện", ignoreCase = true)
            isPaymentType || containsKeyword
        } else {
            typeUpper == type.name
        }
    }
}
