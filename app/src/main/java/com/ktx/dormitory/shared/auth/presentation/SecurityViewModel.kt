package com.ktx.dormitory.shared.auth.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.shared.auth.domain.usecase.GetBiometricStatusUseCase
import com.ktx.dormitory.shared.auth.domain.usecase.SetBiometricStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val getBiometricStatusUseCase: GetBiometricStatusUseCase,
    private val setBiometricStatusUseCase: SetBiometricStatusUseCase
) : BaseViewModel<SecurityUiState, SecurityUiEvent, SecurityUiEffect>(SecurityUiState()) {

    init {
        loadStatus()
    }

    override fun onEvent(event: SecurityUiEvent) {
        when (event) {
            is SecurityUiEvent.ToggleBiometric -> toggleBiometric(event.enabled)
            SecurityUiEvent.LoadStatus -> loadStatus()
        }
    }

    private fun loadStatus() {
        viewModelScope.launch {
            val enabled = getBiometricStatusUseCase.isEnabledSync()
            updateState { it.copy(isBiometricEnabled = enabled) }
        }
    }

    private fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            setBiometricStatusUseCase(enabled)
            updateState { it.copy(isBiometricEnabled = enabled) }
            sendEffect(SecurityUiEffect.ShowToast(if (enabled) "Đã bật xác thực vân tay" else "Đã tắt xác thực vân tay"))
        }
    }
}
