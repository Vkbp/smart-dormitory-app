package com.ktx.dormitory.admin.notification.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.admin.notification.domain.usecase.BroadcastNotificationUseCase
import com.ktx.dormitory.core.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationBroadcastViewModel @Inject constructor(
    private val broadcastNotificationUseCase: BroadcastNotificationUseCase
) : BaseViewModel<NotificationBroadcastUiState, NotificationBroadcastUiEvent, NotificationBroadcastUiEffect>(NotificationBroadcastUiState()) {

    override fun onEvent(event: NotificationBroadcastUiEvent) {
        when (event) {
            is NotificationBroadcastUiEvent.Broadcast -> broadcast(event.title, event.message, event.target)
            NotificationBroadcastUiEvent.ClearStatus -> updateState { it.copy(successMessage = null, errorMessage = null) }
        }
    }

    private fun broadcast(title: String, message: String, target: String) {
        if (title.isBlank()) {
            updateState { it.copy(errorMessage = "Tiêu đề không được để trống") }
            return
        }
        if (message.isBlank()) {
            updateState { it.copy(errorMessage = "Nội dung thông báo không được để trống") }
            return
        }

        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            broadcastNotificationUseCase(title, message, target)
                .onSuccess { response ->
                    val successMsg = "Đã gửi thông báo: ${response.title} tới ${response.recipientCount} người"
                    updateState { it.copy(isLoading = false, successMessage = successMsg) }
                    sendEffect(NotificationBroadcastUiEffect.ShowToast(successMsg))
                }
                .onFailure { error ->
                    updateState { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }
}
