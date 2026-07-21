package com.ktx.dormitory.admin.smartaccess.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.admin.common.domain.usecase.EmergencyOverrideUseCase
import com.ktx.dormitory.admin.common.domain.usecase.GetSmartAccessResourcesUseCase
import com.ktx.dormitory.admin.common.domain.usecase.RemoteUnlockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmartAccessViewModel @Inject constructor(
    private val remoteUnlockUseCase: RemoteUnlockUseCase,
    private val emergencyOverrideUseCase: EmergencyOverrideUseCase,
    private val getSmartAccessResourcesUseCase: GetSmartAccessResourcesUseCase
) : BaseViewModel<SmartAccessUiState, SmartAccessUiEvent, SmartAccessUiEffect>(SmartAccessUiState()) {

    init {
        onEvent(SmartAccessUiEvent.LoadResources)
    }

    override fun onEvent(event: SmartAccessUiEvent) {
        when (event) {
            SmartAccessUiEvent.LoadResources -> loadResources()
            is SmartAccessUiEvent.RemoteUnlock -> unlock(event)
            is SmartAccessUiEvent.EmergencyOverride -> override(event)
            SmartAccessUiEvent.ClearStatus -> updateState { it.copy(successMessage = null, errorMessage = null) }
        }
    }

    private fun loadResources() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            getSmartAccessResourcesUseCase().fold(
                onSuccess = { resources ->
                    updateState { it.copy(
                        buildings = resources.buildings,
                        gates = resources.gates,
                        isLoading = false
                    ) }
                },
                onFailure = { error ->
                    updateState { it.copy(
                        errorMessage = error.message ?: "Không thể tải danh sách tài nguyên",
                        isLoading = false
                    ) }
                }
            )
        }
    }

    /**
     * Remote Unlock Logic (IoT Integration).
     * Rationale: Provides a secondary access method for students while maintaining central control.
     * Uses a strict 10s timeout at the API level to ensure real-time responsiveness.
     */
    private fun unlock(event: SmartAccessUiEvent.RemoteUnlock) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                remoteUnlockUseCase(event.gateId, event.buildingId)
                    .onSuccess {
                        updateState { it.copy(successMessage = "Cửa đã được mở thành công") }
                        sendEffect(SmartAccessUiEffect.ShowToast("Cửa đã được mở"))
                    }
                    .onFailure { error ->
                        updateState { it.copy(errorMessage = error.message ?: "Mở cửa thất bại") }
                    }
            } catch (e: Exception) {
                updateState { it.copy(errorMessage = "Lỗi kết nối: ${e.message}") }
            } finally {
                updateState { it.copy(isLoading = false) }
            }
        }
    }

    private fun override(event: SmartAccessUiEvent.EmergencyOverride) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                emergencyOverrideUseCase(event.actionType, event.reason, event.buildingId)
                    .onSuccess {
                        updateState { it.copy(successMessage = "Kích hoạt khẩn cấp thành công") }
                        sendEffect(SmartAccessUiEffect.ShowToast("Kích hoạt khẩn cấp thành công"))
                    }
                    .onFailure { error ->
                        updateState { it.copy(errorMessage = error.message ?: "Kích hoạt thất bại") }
                    }
            } catch (e: Exception) {
                updateState { it.copy(errorMessage = "Lỗi hệ thống: ${e.message}") }
            } finally {
                updateState { it.copy(isLoading = false) }
            }
        }
    }
}
