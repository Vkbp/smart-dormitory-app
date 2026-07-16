package com.ktx.dormitory.admin.face.presentation

import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.admin.common.domain.model.FaceProfile
import java.util.UUID

data class FaceApprovalUiState(
    val isLoading: Boolean = false,
    val pendingProfiles: List<FaceProfile> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
) : BaseContract.State

sealed class FaceApprovalUiEvent : BaseContract.Event {
    data object LoadPendingProfiles : FaceApprovalUiEvent()
    data class ApproveProfile(val profileId: UUID) : FaceApprovalUiEvent()
    data class RejectProfile(val profileId: UUID, val reason: String) : FaceApprovalUiEvent()
}

sealed class FaceApprovalUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : FaceApprovalUiEffect()
}
