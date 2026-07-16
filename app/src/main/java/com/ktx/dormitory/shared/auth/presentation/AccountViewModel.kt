package com.ktx.dormitory.shared.auth.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.shared.auth.domain.usecase.ChangePasswordUseCase
import com.ktx.dormitory.shared.auth.domain.usecase.ForgotPasswordUseCase
import com.ktx.dormitory.shared.auth.domain.usecase.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : BaseViewModel<AccountUiState, AccountUiEvent, AccountUiEffect>(AccountUiState()) {

    override fun onEvent(event: AccountUiEvent) {
        when (event) {
            is AccountUiEvent.ChangePassword -> performChangePassword(event.oldPass, event.newPass)
            is AccountUiEvent.ForgotPassword -> performForgotPassword(event.email)
            is AccountUiEvent.ResetPassword -> performResetPassword(event.token, event.newPass)
            AccountUiEvent.ClearStatus -> updateState { it.copy(error = null, successMessage = null) }
        }
    }

    private fun performChangePassword(oldPass: String, newPass: String) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            changePasswordUseCase(oldPass, newPass)
                .onSuccess {
                    updateState { it.copy(successMessage = "Đổi mật khẩu thành công") }
                    sendEffect(AccountUiEffect.ShowToast("Đổi mật khẩu thành công"))
                    sendEffect(AccountUiEffect.NavigateBack)
                }
                .onFailure { e ->
                    updateState { it.copy(error = e.message ?: "Lỗi đổi mật khẩu") }
                }
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun performForgotPassword(email: String) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            forgotPasswordUseCase(email)
                .onSuccess {
                    updateState { it.copy(successMessage = "Yêu cầu đã được gửi. Vui lòng kiểm tra email.") }
                    sendEffect(AccountUiEffect.ShowToast("Yêu cầu đã được gửi"))
                }
                .onFailure { e ->
                    updateState { it.copy(error = e.message ?: "Lỗi gửi yêu cầu") }
                }
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun performResetPassword(token: String, newPass: String) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            resetPasswordUseCase(token, newPass)
                .onSuccess {
                    updateState { it.copy(successMessage = "Đặt lại mật khẩu thành công") }
                    sendEffect(AccountUiEffect.ShowToast("Đặt lại mật khẩu thành công"))
                    sendEffect(AccountUiEffect.NavigateBack)
                }
                .onFailure { e ->
                    updateState { it.copy(error = e.message ?: "Lỗi đặt lại mật khẩu") }
                }
            updateState { it.copy(isLoading = false) }
        }
    }
}
