package com.ktx.dormitory.shared.notification.presentation.components

import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.student.room.domain.model.RoomInfo

data class IssueReportUiState(
    val isLoading: Boolean = false,
    val roomInfo: RoomInfo? = null,
    val description: String = "",
    val successMessage: String? = null,
    val errorMessage: String? = null
) : BaseContract.State

sealed class IssueReportUiEvent : BaseContract.Event {
    data class DescriptionChanged(val description: String) : IssueReportUiEvent()
    data object SubmitReport : IssueReportUiEvent()
    data object ClearStatus : IssueReportUiEvent()
}

sealed class IssueReportUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : IssueReportUiEffect()
    data object DismissSheet : IssueReportUiEffect()
}
