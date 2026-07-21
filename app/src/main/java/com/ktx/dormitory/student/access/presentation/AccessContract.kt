package com.ktx.dormitory.student.access.presentation

import android.os.Parcelable
import com.ktx.dormitory.student.access.domain.model.CurfewRequest
import com.ktx.dormitory.student.access.domain.model.CurfewRequestType
import com.ktx.dormitory.student.access.domain.model.UnifiedTimelineEvent
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccessUiState(
    val isLoading: Boolean = false,
    val logs: List<UnifiedTimelineEvent> = emptyList(),
    val curfewRequests: List<CurfewRequest> = emptyList(),
    val error: String? = null,
    val uiMessage: String? = null,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val submitSuccess: Boolean = false
) : Parcelable

sealed interface AccessUiEvent {
    data object FetchHistory : AccessUiEvent
    data object FetchCurfewRequests : AccessUiEvent
    data class SubmitCurfewRequest(
        val requestType: CurfewRequestType,
        val reason: String,
        val startDate: String?,
        val expectedArrivalTime: String,
        val note: String?
    ) : AccessUiEvent
    data object ClearMessage : AccessUiEvent
    data object ResetSubmitSuccess : AccessUiEvent
}

sealed interface AccessUiEffect {
    data object NavigateBack : AccessUiEffect
    data class ShowToast(val message: String) : AccessUiEffect
}
