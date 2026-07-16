package com.ktx.dormitory.admin.checkin.presentation

import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.admin.common.domain.model.CheckInStudent
import java.util.UUID

data class CheckInUiState(
    val isLoading: Boolean = false,
    val studentInfo: CheckInStudent? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
) : BaseContract.State

sealed class CheckInUiEvent : BaseContract.Event {
    data class SearchStudent(val cccd: String) : CheckInUiEvent()
    data class ConfirmCheckIn(val assignmentId: UUID) : CheckInUiEvent()
    data class AssignRfid(val studentId: UUID, val rfidCode: String) : CheckInUiEvent()
    data object ClearStatus : CheckInUiEvent()
}

sealed class CheckInUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : CheckInUiEffect()
}
