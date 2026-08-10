package com.ktx.dormitory.shared.auth.presentation

import android.os.Parcelable
import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.shared.auth.domain.model.UserData
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginUiState(
    val isLoading: Boolean = false,
    val userData: UserData? = null,
    val mssvError: String? = null,
    val passwordError: String? = null,
    val error: String? = null
) : Parcelable, BaseContract.State

sealed class LoginUiEvent : BaseContract.Event {
    data class LoginClicked(val mssv: String, val pass: String) : LoginUiEvent()
    data object BiometricClicked : LoginUiEvent()
    data object LogoutClicked : LoginUiEvent()
    data class ForgotPasswordClicked(val email: String) : LoginUiEvent()
    data object CheckAuthStatus : LoginUiEvent()
}

sealed class LoginUiEffect : BaseContract.Effect {
    data class NavigateToHome(val role: String) : LoginUiEffect()
    data class ShowError(val message: String) : LoginUiEffect()
    data object NavigateToLogin : LoginUiEffect()
    data class AuthStatusChecked(val role: String?) : LoginUiEffect()
}
