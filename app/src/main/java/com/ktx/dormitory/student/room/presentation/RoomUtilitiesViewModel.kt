package com.ktx.dormitory.student.room.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.student.room.domain.usecase.GetRoomUtilitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomUtilitiesViewModel @Inject constructor(
    private val getRoomUtilitiesUseCase: GetRoomUtilitiesUseCase
) : BaseViewModel<RoomUtilitiesUiState, RoomUtilitiesUiEvent, RoomUtilitiesUiEffect>(RoomUtilitiesUiState()) {

    init {
        loadUtilities()
    }

    override fun onEvent(event: RoomUtilitiesUiEvent) {
        when (event) {
            RoomUtilitiesUiEvent.LoadUtilities -> loadUtilities()
            RoomUtilitiesUiEvent.Refresh -> loadUtilities()
        }
    }

    private fun loadUtilities() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            getRoomUtilitiesUseCase()
                .onSuccess { utilities ->
                    updateState { it.copy(isLoading = false, utilities = utilities) }
                }
                .onFailure { e ->
                    updateState { it.copy(isLoading = false, error = e.message ?: "Lỗi tải chỉ số điện nước") }
                }
        }
    }
}
