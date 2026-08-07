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
import com.ktx.dormitory.shared.auth.domain.repository.AuthRepository
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
    private val authRepository: AuthRepository
) : BaseViewModel<NotificationUiState, NotificationUiEvent, NotificationUiEffect>(NotificationUiState()) {

    init {
        initUserRole()
        loadNotifications()
        fetchUnreadCount()
    }

    private fun initUserRole() {
        val role = authRepository.getRoleSync() ?: "STUDENT"
        updateState { it.copy(userRole = role) }
    }

    fun refresh() {
        // Chỉ cập nhật số lượng, không xóa trạng thái đã đọc tạm thời để tránh nhảy UI khi chuyển trang
        fetchUnreadCount()
    }

    override fun onEvent(event: NotificationUiEvent) {
        when (event) {
            NotificationUiEvent.LoadNotifications -> { /* Paging 3 handles initial load */ }
            NotificationUiEvent.Refresh -> {
                // Khi chủ động Refresh danh sách, ta mới đồng bộ lại từ đầu với Server
                updateState { it.copy(isAllReadMarked = false, readIds = emptySet()) }
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
            .cachedIn(viewModelScope)

        updateState { it.copy(pagingFlow = pagingFlow) }
    }

    private fun fetchUnreadCount() {
        viewModelScope.launch {
            getUnreadCountUseCase().onSuccess { count ->
                val serverCount = count.toInt()
                val localReadCount = currentState.readIds.size
                val finalCount = if (currentState.isAllReadMarked) 0 
                                else (serverCount - localReadCount).coerceAtLeast(0)
                updateState { it.copy(unreadCount = finalCount) }
            }
        }
    }

    private fun markAsRead(id: Long) {
        if (currentState.readIds.contains(id)) return

        // Update Local State for instant UI feedback without breaking Paging flow
        val newReadIds = currentState.readIds + id
        updateState { it.copy(
            readIds = newReadIds,
            unreadCount = (it.unreadCount - 1).coerceAtLeast(0)
        ) }

        viewModelScope.launch {
            markReadUseCase(id).onFailure {
                // Optional: handle revert if critical, but usually server eventually syncs
            }
        }
    }

    private fun markAllAsRead() {
        updateState { it.copy(isAllReadMarked = true, unreadCount = 0) }
        viewModelScope.launch {
            markAllReadUseCase().onFailure {
                updateState { it.copy(isAllReadMarked = false) }
                fetchUnreadCount()
            }
        }
    }

    private fun applyFilterLogic(notification: Notification, type: NotificationType): Boolean {
        if (type == NotificationType.ALL) return true
        
        val typeUpper = notification.type?.uppercase() ?: ""
        
        return when (type) {
            NotificationType.PAYMENT -> {
                val isPaymentType = typeUpper in listOf(
                    "PAYMENT", "ELECTRIC_FEE", "ACCOMMODATION_FEE",
                    "PENALTY_FEE", "BILL", "INVOICE", "PAYMENT_NOTICE"
                )
                val containsKeyword = notification.title.contains("hóa đơn", ignoreCase = true) ||
                        notification.title.contains("thanh toán", ignoreCase = true) ||
                        notification.message.contains("tiền điện", ignoreCase = true)
                isPaymentType || containsKeyword
            }
            NotificationType.VIOLATION -> {
                typeUpper == "VIOLATION"
            }
            else -> {
                typeUpper == type.name
            }
        }
    }
}
