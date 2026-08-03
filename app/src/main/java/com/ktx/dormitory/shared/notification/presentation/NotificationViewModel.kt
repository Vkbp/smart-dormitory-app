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
import androidx.paging.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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

    private val readIdsFlow = MutableStateFlow<Set<Long>>(emptySet())
    private val isAllReadFlow = MutableStateFlow(false)

    init {
        loadNotifications()
        fetchUnreadCount()
    }

    fun refresh() {
        fetchUnreadCount()
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
            .distinctUntilChanged()
            .flatMapLatest { type ->
                getNotificationsUseCase()
                    .map { pagingData ->
                        pagingData.filter { applyFilterLogic(it, type) }
                    }
            }
            .combine(readIdsFlow) { pagingData, readIds ->
                pagingData.map { it.copy(isRead = it.isRead || readIds.contains(it.id)) }
            }
            .combine(isAllReadFlow) { pagingData, isAllRead ->
                if (isAllRead) pagingData.map { it.copy(isRead = true) } else pagingData
            }
            .cachedIn(viewModelScope)

        updateState { it.copy(pagingFlow = pagingFlow) }
    }

    private fun fetchUnreadCount() {
        viewModelScope.launch {
            getUnreadCountUseCase().onSuccess { count ->
                val localReadCount = readIdsFlow.value.size
                val finalCount = if (isAllReadFlow.value) 0 else (count.toInt() - localReadCount).coerceAtLeast(0)
                updateState { it.copy(unreadCount = finalCount) }
            }
        }
    }

    private fun markAsRead(id: Long) {
        if (readIdsFlow.value.contains(id)) return

        // Update Local State for instant UI feedback
        readIdsFlow.value = readIdsFlow.value + id
        updateState { it.copy(
            readIds = readIdsFlow.value,
            unreadCount = (it.unreadCount - 1).coerceAtLeast(0)
        ) }

        viewModelScope.launch {
            markReadUseCase(id).onFailure {
                // On failure, we could potentially remove it from readIds, 
                // but usually, we just let the next real refresh handle it.
            }
        }
    }

    private fun markAllAsRead() {
        isAllReadFlow.value = true
        updateState { it.copy(isAllReadMarked = true, unreadCount = 0) }
        
        viewModelScope.launch {
            markAllReadUseCase().onFailure {
                isAllReadFlow.value = false
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
