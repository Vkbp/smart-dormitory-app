package com.ktx.dormitory.student.room.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.student.room.domain.usecase.GetRoomInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val getRoomInfoUseCase: GetRoomInfoUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val uiState: StateFlow<RoomUiState> = savedStateHandle.getStateFlow("uiState", RoomUiState())

    private fun updateUiState(reducer: (RoomUiState) -> RoomUiState) {
        savedStateHandle["uiState"] = reducer(uiState.value)
    }

    init {
        if (uiState.value.roomInfo == null) {
            loadRoomInfo()
        }
    }

    fun loadRoomInfo() {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true, error = null) }
            getRoomInfoUseCase()
                .onSuccess { info ->
                    updateUiState { it.copy(isLoading = false, roomInfo = info) }
                }
                .onFailure { e ->
                    updateUiState { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
