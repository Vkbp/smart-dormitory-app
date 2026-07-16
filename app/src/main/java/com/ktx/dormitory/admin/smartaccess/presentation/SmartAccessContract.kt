package com.ktx.dormitory.admin.smartaccess.presentation

import com.ktx.dormitory.admin.common.data.dto.response.BuildingResponseDto
import com.ktx.dormitory.admin.common.data.dto.response.GateResponseDto
import com.ktx.dormitory.core.base.BaseContract
import java.util.UUID

data class SmartAccessUiState(
    val isLoading: Boolean = false,
    val buildings: List<BuildingResponseDto> = emptyList(),
    val gates: List<GateResponseDto> = emptyList(),
    val successMessage: String? = null,
    val errorMessage: String? = null
) : BaseContract.State

sealed class SmartAccessUiEvent : BaseContract.Event {
    data object LoadResources : SmartAccessUiEvent()
    data class RemoteUnlock(val gateId: UUID, val buildingId: UUID) : SmartAccessUiEvent()
    data class EmergencyOverride(val actionType: String, val reason: String, val buildingId: UUID?) : SmartAccessUiEvent()
    data object ClearStatus : SmartAccessUiEvent()
}

sealed class SmartAccessUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : SmartAccessUiEffect()
}
