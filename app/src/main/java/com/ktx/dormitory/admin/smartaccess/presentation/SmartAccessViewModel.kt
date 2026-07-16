package com.ktx.dormitory.admin.smartaccess.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.admin.common.domain.usecase.EmergencyOverrideUseCase
import com.ktx.dormitory.admin.common.domain.usecase.GetBuildingsUseCase
import com.ktx.dormitory.admin.common.domain.usecase.GetGatesUseCase
import com.ktx.dormitory.admin.common.domain.usecase.RemoteUnlockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmartAccessViewModel @Inject constructor(
    private val remoteUnlockUseCase: RemoteUnlockUseCase,
    private val emergencyOverrideUseCase: EmergencyOverrideUseCase,
    private val getBuildingsUseCase: GetBuildingsUseCase,
    private val getGatesUseCase: GetGatesUseCase
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
            try {
                coroutineScope {
                    val buildingsDef = async { getBuildingsUseCase() }
                    val gatesDef = async { getGatesUseCase() }

                    val buildingsRes = buildingsDef.await()
                    val gatesRes = gatesDef.await()

                    if (buildingsRes.isSuccess && gatesRes.isSuccess) {
                        updateState { it.copy(
                            buildings = buildingsRes.getOrDefault(emptyList()),
                            gates = gatesRes.getOrDefault(emptyList()),
                            isLoading = false
                        ) }
                    } else {
                        updateState { it.copy(
                            errorMessage = "Không thể tải danh sách tài nguyên",
                            isLoading = false
                        ) }
                    }
                }
            } catch (e: Exception) {
                updateState { it.copy(errorMessage = e.message, isLoading = false) }
            }
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
