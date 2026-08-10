package com.ktx.dormitory.student.room.presentation

import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.student.room.domain.model.UtilityReading

data class RoomUtilitiesUiState(
    val isLoading: Boolean = false,
    val utilities: List<UtilityReading> = emptyList(),
    val error: String? = null
) : BaseContract.State

sealed class RoomUtilitiesUiEvent : BaseContract.Event {
    data object LoadUtilities : RoomUtilitiesUiEvent()
    data object Refresh : RoomUtilitiesUiEvent()
}

sealed class RoomUtilitiesUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : RoomUtilitiesUiEffect()
}
