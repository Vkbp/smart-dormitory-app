package com.ktx.dormitory.shared.notification.presentation.components

import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.shared.notification.domain.model.IssueReport

data class IssueHistoryUiState(
    val isLoading: Boolean = false,
    val issues: List<IssueReport> = emptyList(),
    val error: String? = null
) : BaseContract.State

sealed class IssueHistoryUiEvent : BaseContract.Event {
    data object Refresh : IssueHistoryUiEvent()
}

sealed class IssueHistoryUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : IssueHistoryUiEffect()
}

