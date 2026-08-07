package com.ktx.dormitory.shared.notification.presentation.violation

import androidx.paging.PagingData
import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.shared.notification.domain.model.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class ViolationHistoryUiState(
    val violations: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) : BaseContract.State

sealed class ViolationHistoryUiEvent : BaseContract.Event {
    data object LoadViolations : ViolationHistoryUiEvent()
    data object Refresh : ViolationHistoryUiEvent()
    data class MarkAsRead(val id: Long) : ViolationHistoryUiEvent()
}

sealed class ViolationHistoryUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : ViolationHistoryUiEffect()
    data class OpenUrl(val url: String) : ViolationHistoryUiEffect()
}
