package com.ktx.dormitory.student.extension.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.student.extension.domain.usecase.CheckEligibilityUseCase
import com.ktx.dormitory.student.extension.domain.usecase.CheckExtensionPeriodUseCase
import com.ktx.dormitory.student.extension.domain.usecase.RequestExtensionUseCase
import com.ktx.dormitory.core.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtensionViewModel @Inject constructor(
    private val requestExtensionUseCase: RequestExtensionUseCase,
    private val checkExtensionPeriodUseCase: CheckExtensionPeriodUseCase,
    private val checkEligibilityUseCase: CheckEligibilityUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val uiState: StateFlow<ExtensionUiState> = savedStateHandle.getStateFlow("uiState", ExtensionUiState())

    private fun updateUiState(reducer: (ExtensionUiState) -> ExtensionUiState) {
        savedStateHandle["uiState"] = reducer(uiState.value)
    }

    init {
        checkStatus()
        fetchMyExtension()
    }

    fun onEvent(event: ExtensionUiEvent) {
        when (event) {
            is ExtensionUiEvent.SubmitExtension -> submitExtension(event.reason, event.description)
            ExtensionUiEvent.CheckStatus -> checkStatus()
            ExtensionUiEvent.ClearStatus -> updateUiState { it.copy(extensionResponse = null, error = null, eligibilityResult = null) }
            is ExtensionUiEvent.CheckEligibility -> checkEligibility(event.cccd)
        }
    }

    fun fetchMyExtension() {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true, error = null) }
            requestExtensionUseCase.repository.getMyExtensionApplication()
                .onSuccess { response ->
                    updateUiState { it.copy(isLoading = false, extensionResponse = response) }
                }
                .onFailure { e ->
                    // 404 là bình thường nếu chưa nộp đơn, không coi là lỗi nghiêm trọng
                    updateUiState { it.copy(isLoading = false) }
                }
        }
    }

    private fun checkStatus() {
        viewModelScope.launch {
            updateUiState { it.copy(isCheckingStatus = true) }
            checkExtensionPeriodUseCase()
                .onSuccess { isActive ->
                    updateUiState { 
                        it.copy(
                            isCheckingStatus = false, 
                            isLocked = !isActive,
                            lockMessage = if (!isActive) "Hiện tại KTX không trong đợt tiếp nhận đơn gia hạn lưu trú." else null
                        ) 
                    }
                }
                .onFailure {
                    updateUiState { it.copy(isCheckingStatus = false) }
                }
        }
    }

    private fun checkEligibility(cccd: String) {
        if (!ValidationUtils.isValidCCCD(cccd)) {
            updateUiState { it.copy(error = "CCCD phải bao gồm 12 chữ số") }
            return
        }

        viewModelScope.launch {
            updateUiState { it.copy(isCheckingEligibility = true, error = null) }
            checkEligibilityUseCase(cccd).onSuccess { result ->
                updateUiState { it.copy(eligibilityResult = result, isCheckingEligibility = false) }
            }.onFailure { e ->
                updateUiState { it.copy(error = e.message, isCheckingEligibility = false) }
            }
        }
    }

    private fun submitExtension(reason: String, description: String) {
        if (reason.isBlank()) {
            updateUiState { it.copy(error = "Vui lòng chọn lý do gia hạn") }
            return
        }
        if (description.length < 10) {
            updateUiState { it.copy(error = "Vui lòng nhập mô tả chi tiết (tối thiểu 10 ký tự)") }
            return
        }

        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true, error = null) }
            try {
                requestExtensionUseCase(reason, description)
                    .onSuccess { response -> 
                        updateUiState { it.copy(extensionResponse = response) } 
                    }
                    .onFailure { e -> 
                        val errorMsg = e.message ?: "Lỗi nộp đơn"
                        updateUiState { it.copy(error = errorMsg) }
                        
                        // Nếu server trả về lỗi đợt gia hạn kết thúc, cập nhật trạng thái khóa
                        if (errorMsg.contains("đợt gia hạn", ignoreCase = true)) {
                            updateUiState { it.copy(isLocked = true, lockMessage = errorMsg) }
                        }
                    }
            } finally {
                updateUiState { it.copy(isLoading = false) }
            }
        }
    }
}
