package com.ktx.dormitory.student.room.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.student.room.domain.usecase.CancelTransferRequestUseCase
import com.ktx.dormitory.student.room.domain.usecase.GetGroupedAvailableRoomsUseCase
import com.ktx.dormitory.student.room.domain.usecase.GetTransferHistoryUseCase
import com.ktx.dormitory.student.room.domain.usecase.SubmitTransferRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomTransferViewModel @Inject constructor(
    private val submitTransferRequestUseCase: SubmitTransferRequestUseCase,
    private val getTransferHistoryUseCase: GetTransferHistoryUseCase,
    private val getGroupedAvailableRoomsUseCase: GetGroupedAvailableRoomsUseCase,
    private val cancelTransferRequestUseCase: CancelTransferRequestUseCase
) : BaseViewModel<RoomTransferUiState, RoomTransferUiEvent, RoomTransferUiEffect>(
    RoomTransferUiState()
) {

    init {
        loadHistory()
        loadAvailableRooms()
    }

    override fun onEvent(event: RoomTransferUiEvent) {
        when (event) {
            is RoomTransferUiEvent.ReasonChanged -> {
                updateState { it.copy(reason = event.reason) }
            }
            is RoomTransferUiEvent.RoomSelected -> {
                updateState { it.copy(targetRoomId = event.roomId, targetRoomCode = event.roomCode) }
            }
            is RoomTransferUiEvent.TabSelected -> {
                updateState { it.copy(selectedTab = event.index) }
                if (event.index == 1) {
                    loadHistory()
                }
            }
            RoomTransferUiEvent.SubmitRequest -> submitRequest()
            is RoomTransferUiEvent.CancelRequest -> cancelRequest(event.id)
            RoomTransferUiEvent.LoadHistory -> loadHistory()
            RoomTransferUiEvent.LoadAvailableRooms -> loadAvailableRooms()
            RoomTransferUiEvent.ClearError -> updateState { it.copy(error = null) }
        }
    }

    private fun loadAvailableRooms() {
        viewModelScope.launch {
            getGroupedAvailableRoomsUseCase().fold(
                onSuccess = { result ->
                    updateState { it.copy(
                        availableRooms = result.allRooms,
                        groupedAvailableRooms = result.groupedByBuilding
                    ) }
                },
                onFailure = { /* Silent fail for available rooms */ }
            )
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

    private fun cancelRequest(id: Long) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            cancelTransferRequestUseCase(id).fold(
                onSuccess = {
                    updateState { it.copy(isLoading = false) }
                    sendEffect(RoomTransferUiEffect.ShowToast("Hủy yêu cầu thành công"))
                    sendEffect(RoomTransferUiEffect.CancelSuccess)
                    loadHistory()
                },
                onFailure = { error ->
                    updateState { it.copy(isLoading = false, error = error.message) }
                    sendEffect(RoomTransferUiEffect.ShowToast("Lỗi: ${error.message}"))
                }
            )
        }
    }

    private fun submitRequest() {
        viewModelScope.launch {
            val currentState = uiState.value
            
            // Client-side validation hardening (STEP 2)
            if (currentState.reason.isBlank()) {
                updateState { it.copy(reasonError = "Lý do không được để trống") }
                return@launch
            }
            if (currentState.reason.length < 10) {
                updateState { it.copy(reasonError = "Lý do phải có ít nhất 10 ký tự để Admin xem xét") }
                return@launch
            }
            updateState { it.copy(reasonError = null) }

            updateState { it.copy(isSubmitting = true) }
            submitTransferRequestUseCase(
                reason = currentState.reason,
                targetRoomId = currentState.targetRoomId.takeIf { it.isNotBlank() }
            ).fold(
                onSuccess = {
                    updateState { it.copy(isSubmitting = false, reason = "", targetRoomId = "", targetRoomCode = "") }
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
