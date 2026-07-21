package com.ktx.dormitory.admin.extension.presentation

import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.admin.common.data.dto.response.StayExtensionResponseDto
import java.util.UUID

data class StayExtensionUiState(
    val isLoading: Boolean = false,
    val extensions: List<StayExtensionResponseDto> = emptyList(),
    val error: String? = null,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val selectedStudentProfile: com.ktx.dormitory.shared.profile.domain.model.UserProfile? = null,
    val isLoadingProfile: Boolean = false
) : BaseContract.State

sealed class StayExtensionUiEvent : BaseContract.Event {
    data class LoadExtensions(val refresh: Boolean = false, val status: String? = "PENDING") : StayExtensionUiEvent()
    data class ReviewExtension(val id: UUID, val status: String, val reason: String?) : StayExtensionUiEvent()
    data class LoadStudentProfile(val studentId: UUID) : StayExtensionUiEvent()
    data object ClearProfile : StayExtensionUiEvent()
}

sealed class StayExtensionUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : StayExtensionUiEffect()
}
