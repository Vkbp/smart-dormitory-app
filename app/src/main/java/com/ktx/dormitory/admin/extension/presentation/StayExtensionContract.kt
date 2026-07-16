package com.ktx.dormitory.admin.extension.presentation

import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.admin.common.data.dto.response.StayExtensionResponseDto
import java.util.UUID

data class StayExtensionUiState(
    val isLoading: Boolean = false,
    val extensions: List<StayExtensionResponseDto> = emptyList(),
    val error: String? = null,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false
) : BaseContract.State

sealed class StayExtensionUiEvent : BaseContract.Event {
    data class LoadExtensions(val refresh: Boolean = false) : StayExtensionUiEvent()
    data class ReviewExtension(val id: UUID, val status: String, val reason: String?) : StayExtensionUiEvent()
}

sealed class StayExtensionUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : StayExtensionUiEffect()
}
