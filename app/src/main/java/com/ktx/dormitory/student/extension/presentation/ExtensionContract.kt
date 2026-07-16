package com.ktx.dormitory.student.extension.presentation

import android.os.Parcelable
import com.ktx.dormitory.student.extension.domain.model.CheckEligibilityResult
import com.ktx.dormitory.student.extension.domain.model.StayExtensionResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExtensionUiState(
    val isLoading: Boolean = false,
    val isCheckingStatus: Boolean = false,
    val isLocked: Boolean = false,
    val lockMessage: String? = null,
    val extensionResponse: StayExtensionResponse? = null,
    val error: String? = null,
    val eligibilityResult: CheckEligibilityResult? = null,
    val isCheckingEligibility: Boolean = false
) : Parcelable

sealed interface ExtensionUiEvent {
    data class SubmitExtension(val reason: String, val description: String) : ExtensionUiEvent
    data object CheckStatus : ExtensionUiEvent
    data object ClearStatus : ExtensionUiEvent
    data class CheckEligibility(val cccd: String) : ExtensionUiEvent
}
