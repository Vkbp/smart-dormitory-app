package com.ktx.dormitory.student.maintenance.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.student.maintenance.domain.usecase.GetMaintenanceHistoryUseCase
import com.ktx.dormitory.student.maintenance.domain.usecase.SubmitMaintenanceRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaintenanceViewModel @Inject constructor(
    private val getMaintenanceHistoryUseCase: GetMaintenanceHistoryUseCase,
    private val submitMaintenanceRequestUseCase: SubmitMaintenanceRequestUseCase,
    private val uploadMaintenanceImageUseCase: com.ktx.dormitory.student.maintenance.domain.usecase.UploadMaintenanceImageUseCase
) : BaseViewModel<MaintenanceUiState, MaintenanceUiEvent, MaintenanceUiEffect>(MaintenanceUiState()) {

    init {
        loadHistory()
    }

    override fun onEvent(event: MaintenanceUiEvent) {
        when (event) {
            is MaintenanceUiEvent.LoadHistory -> loadHistory()
            is MaintenanceUiEvent.SubmitRequest -> submitRequest(event.description)
            is MaintenanceUiEvent.ResetSubmitState -> updateState { it.copy(submitSuccess = false, error = null, capturedImagePath = null) }
            is MaintenanceUiEvent.OnImageCaptured -> updateState { it.copy(capturedImagePath = event.path) }
            is MaintenanceUiEvent.ClearImage -> updateState { it.copy(capturedImagePath = null) }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            getMaintenanceHistoryUseCase()
                .onSuccess { history ->
                    updateState { it.copy(isLoading = false, history = history) }
                }
                .onFailure { e ->
                    updateState { it.copy(isLoading = false, error = e.message ?: "Lỗi tải lịch sử bảo trì") }
                }
        }
    }

    private fun submitRequest(description: String) {
        viewModelScope.launch {
            updateState { it.copy(isSubmitting = true, error = null) }
            
            val imageUrl = if (uiState.value.capturedImagePath != null) {
                updateState { it.copy(isUploading = true) }
                val uploadResult = uploadMaintenanceImageUseCase(uiState.value.capturedImagePath!!)
                updateState { it.copy(isUploading = false) }
                
                if (uploadResult.isSuccess) {
                    uploadResult.getOrNull()
                } else {
                    updateState { it.copy(isSubmitting = false, error = "Tải ảnh thất bại: ${uploadResult.exceptionOrNull()?.message}") }
                    return@launch
                }
            } else null

            submitMaintenanceRequestUseCase(description, imageUrl)
                .onSuccess {
                    updateState { it.copy(isSubmitting = false, submitSuccess = true) }
                    sendEffect(MaintenanceUiEffect.ShowToast("Gửi yêu cầu bảo trì thành công"))
                    loadHistory()
                }
                .onFailure { e ->
                    updateState { it.copy(isSubmitting = false, error = e.message ?: "Gửi yêu cầu thất bại") }
                }
        }
    }
}
