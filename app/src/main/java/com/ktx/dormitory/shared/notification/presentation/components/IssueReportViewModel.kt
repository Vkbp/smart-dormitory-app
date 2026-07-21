package com.ktx.dormitory.shared.notification.presentation.components

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.shared.notification.domain.usecase.ReportIssueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IssueReportViewModel @Inject constructor(
    private val reportIssueUseCase: ReportIssueUseCase
) : BaseViewModel<IssueReportUiState, IssueReportUiEvent, IssueReportUiEffect>(IssueReportUiState()) {

    override fun onEvent(event: IssueReportUiEvent) {
        when (event) {
            is IssueReportUiEvent.DescriptionChanged -> updateState { it.copy(description = event.description) }
            is IssueReportUiEvent.IsCommonAreaChanged -> updateState { it.copy(isCommonArea = event.isCommonArea) }
            IssueReportUiEvent.SubmitReport -> submitReport()
            IssueReportUiEvent.ClearStatus -> updateState { it.copy(successMessage = null, errorMessage = null) }
        }
    }

    private fun submitReport() {
        val description = currentState.description
        val isCommonArea = currentState.isCommonArea

        if (description.isBlank()) {
            updateState { it.copy(errorMessage = "Vui lòng nhập mô tả lỗi") }
            return
        }

        if (description.length < 10) {
            updateState { it.copy(errorMessage = "Mô tả lỗi phải có ít nhất 10 ký tự") }
            return
        }

        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            try {
                reportIssueUseCase(description, isCommonArea)
                    .onSuccess { message ->
                        updateState { it.copy(successMessage = message, description = "", isCommonArea = false) }
                        sendEffect(IssueReportUiEffect.ShowToast(message))
                        sendEffect(IssueReportUiEffect.DismissSheet)
                    }
                    .onFailure { error ->
                        updateState { it.copy(errorMessage = error.message ?: "Gửi báo cáo thất bại") }
                    }
            } catch (e: Exception) {
                updateState { it.copy(errorMessage = "Lỗi hệ thống: ${e.message}") }
            } finally {
                updateState { it.copy(isLoading = false) }
            }
        }
    }
}
