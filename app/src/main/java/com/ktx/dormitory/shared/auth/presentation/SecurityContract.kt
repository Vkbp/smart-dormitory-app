package com.ktx.dormitory.shared.auth.presentation

import com.ktx.dormitory.core.base.BaseContract

data class SecurityUiState(
    val isBiometricEnabled: Boolean = false,
    val isLoading: Boolean = false
) : BaseContract.State

sealed class SecurityUiEvent : BaseContract.Event {
    data class ToggleBiometric(val enabled: Boolean) : SecurityUiEvent()
    data object LoadStatus : SecurityUiEvent()
}

sealed class SecurityUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : SecurityUiEffect()
}
