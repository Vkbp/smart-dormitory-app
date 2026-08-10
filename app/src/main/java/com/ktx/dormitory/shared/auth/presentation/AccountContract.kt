package com.ktx.dormitory.shared.auth.presentation

import com.ktx.dormitory.core.base.BaseContract

data class AccountUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
) : BaseContract.State

sealed class AccountUiEvent : BaseContract.Event {
    data class ChangePassword(val oldPass: String, val newPass: String) : AccountUiEvent()
    data class ForgotPassword(val email: String) : AccountUiEvent()
    data class ResetPassword(val email: String, val otp: String, val newPass: String) : AccountUiEvent()
    data object ClearStatus : AccountUiEvent()
}

sealed class AccountUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : AccountUiEffect()
    data object NavigateBack : AccountUiEffect()
}
