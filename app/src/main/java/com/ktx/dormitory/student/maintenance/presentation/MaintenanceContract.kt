package com.ktx.dormitory.student.maintenance.presentation

import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.student.maintenance.domain.model.MaintenanceRequest

data class MaintenanceUiState(
    val isLoading: Boolean = false,
    val history: List<MaintenanceRequest> = emptyList(),
    val error: String? = null,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val capturedImagePath: String? = null,
    val isUploading: Boolean = false
) : BaseContract.State

sealed class MaintenanceUiEvent : BaseContract.Event {
    data object LoadHistory : MaintenanceUiEvent()
    data class SubmitRequest(val description: String) : MaintenanceUiEvent()
    data object ResetSubmitState : MaintenanceUiEvent()
    data class OnImageCaptured(val path: String) : MaintenanceUiEvent()
    data object ClearImage : MaintenanceUiEvent()
}

sealed class MaintenanceUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : MaintenanceUiEffect()
    data object NavigateBack : MaintenanceUiEffect()
}
