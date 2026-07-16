package com.ktx.dormitory.student.room.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.student.room.domain.usecase.GetTransferHistoryUseCase
import com.ktx.dormitory.student.room.domain.usecase.SubmitTransferRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomTransferViewModel @Inject constructor(
    private val submitTransferRequestUseCase: SubmitTransferRequestUseCase,
    private val getTransferHistoryUseCase: GetTransferHistoryUseCase
) : BaseViewModel<RoomTransferUiState, RoomTransferUiEvent, RoomTransferUiEffect>(
    RoomTransferUiState()
) {

    init {
        loadHistory()
    }

    override fun onEvent(event: RoomTransferUiEvent) {
        when (event) {
            is RoomTransferUiEvent.ReasonChanged -> {
                updateState { it.copy(reason = event.reason) }
            }
            is RoomTransferUiEvent.TargetRoomChanged -> {
                updateState { it.copy(targetRoomId = event.targetRoomId) }
            }
            is RoomTransferUiEvent.TabSelected -> {
                updateState { it.copy(selectedTab = event.index) }
                if (event.index == 1) {
                    loadHistory()
                }
            }
            RoomTransferUiEvent.SubmitRequest -> submitRequest()
            RoomTransferUiEvent.LoadHistory -> loadHistory()
            RoomTransferUiEvent.ClearError -> updateState { it.copy(error = null) }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            getTransferHistoryUseCase().fold(
                onSuccess = { history ->
                    updateState { it.copy(isLoading = false, history = history) }
                },
                onFailure = { error ->
                    updateState { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    private fun submitRequest() {
        viewModelScope.launch {
            val currentState = uiState.value
            if (currentState.reason.isBlank()) {
                updateState { it.copy(error = "Lý do không được để trống") }
                return@launch
            }

            updateState { it.copy(isSubmitting = true) }
            submitTransferRequestUseCase(
                reason = currentState.reason,
                targetRoomId = currentState.targetRoomId.takeIf { it.isNotBlank() }
            ).fold(
                onSuccess = {
                    updateState { it.copy(isSubmitting = false, reason = "", targetRoomId = "") }
                    sendEffect(RoomTransferUiEffect.ShowToast("Gửi yêu cầu thành công"))
                    sendEffect(RoomTransferUiEffect.SubmissionSuccess)
                    // Chuyển sang tab lịch sử để xem kết quả
                    onEvent(RoomTransferUiEvent.TabSelected(1))
                },
                onFailure = { error ->
                    updateState { it.copy(isSubmitting = false, error = error.message) }
                    sendEffect(RoomTransferUiEffect.ShowToast("Lỗi: ${error.message}"))
                }
            )
        }
    }
}
