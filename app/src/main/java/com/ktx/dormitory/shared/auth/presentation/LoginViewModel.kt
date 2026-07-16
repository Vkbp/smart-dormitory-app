package com.ktx.dormitory.shared.auth.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.core.util.AuthEvent
import com.ktx.dormitory.core.util.AuthEventBus
import com.ktx.dormitory.shared.auth.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val getBiometricStatusUseCase: GetBiometricStatusUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<LoginUiState, LoginUiEvent, LoginUiEffect>(
    savedStateHandle.get<LoginUiState>("uiState") ?: LoginUiState()
) {

    init {
        // Đồng bộ SavedStateHandle với uiState của BaseViewModel
        viewModelScope.launch {
            uiState.collect {
                savedStateHandle["uiState"] = it
            }
        }
        
        if (currentState.userData == null) {
            fetchCurrentUser()
        }
    }

    override fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.LoginClicked -> performLogin(event.mssv, event.pass)
            is LoginUiEvent.BiometricClicked -> loginWithBiometric()
            is LoginUiEvent.LogoutClicked -> logout {}
            is LoginUiEvent.CheckAuthStatus -> checkAuthStatus()
            else -> { /* Account events handled by AccountViewModel */ }
        }
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                kotlinx.coroutines.withTimeout(3000) {
                    getAuthStateUseCase().onSuccess { data ->
                        updateState { it.copy(userData = data) }
                        sendEffect(LoginUiEffect.AuthStatusChecked(data.role))
                    }.onFailure { 
                        sendEffect(LoginUiEffect.AuthStatusChecked(null))
                    }
                }
            } catch (e: Exception) {
                sendEffect(LoginUiEffect.AuthStatusChecked(null))
            }
        }
    }

    private fun fetchCurrentUser() {
        viewModelScope.launch {
            getAuthStateUseCase().onSuccess { data ->
                updateState { it.copy(userData = data) }
            }
        }
    }

    private fun performLogin(usernameOrEmail: String, password: String) {
        updateState { it.copy(mssvError = null, passwordError = null, error = null) }

        if (usernameOrEmail.isBlank()) {
            updateState { it.copy(mssvError = "MSSV hoặc Email không được để trống") }
            return
        }
        if (password.isBlank()) {
            updateState { it.copy(passwordError = "Mật khẩu không được để trống") }
            return
        }

        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                loginUseCase(usernameOrEmail, password)
                    .onSuccess { data ->
                        val role = data.role?.uppercase() ?: ""
                        if (role.contains("STUDENT") || role.contains("ADMIN") || role.contains("STAFF") || role.contains("USER")) {
                            updateState { it.copy(userData = data) }
                            sendEffect(LoginUiEffect.NavigateToHome(data.role ?: "STUDENT"))
                        } else {
                            logout {}
                            val errorMsg = "Tài khoản không có quyền truy cập hệ thống"
                            updateState { it.copy(error = errorMsg) }
                            sendEffect(LoginUiEffect.ShowError(errorMsg))
                        }
                    }
                    .onFailure { e ->
                        val errorMsg = e.message ?: "Đăng nhập thất bại"
                        updateState { it.copy(error = errorMsg) }
                        sendEffect(LoginUiEffect.ShowError(errorMsg))
                    }
            } finally {
                updateState { it.copy(isLoading = false) }
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            updateState { it.copy(userData = null) }
            AuthEventBus.emit(AuthEvent.LOGOUT)
            onComplete()
        }
    }

    fun loginWithBiometric() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                refreshTokenUseCase().onSuccess { data ->
                    val role = data.role?.uppercase() ?: ""
                    if (role.contains("STUDENT") || role.contains("ADMIN") || role.contains("STAFF") || role.contains("USER")) {
                        updateState { it.copy(userData = data) }
                        sendEffect(LoginUiEffect.NavigateToHome(data.role ?: "STUDENT"))
                    } else {
                        logout {}
                        sendEffect(LoginUiEffect.ShowError("Tài khoản không có quyền truy cập ứng dụng này"))
                    }
                }.onFailure { e ->
                    val errorMsg = e.message ?: "Phiên đăng nhập hết hạn"
                    if (errorMsg.contains("refresh token", ignoreCase = true)) {
                        sendEffect(LoginUiEffect.ShowError("Phiên đăng nhập hết hạn, vui lòng đăng nhập lại"))
                        logout {}
                    } else {
                        sendEffect(LoginUiEffect.ShowError(errorMsg))
                    }
                }
            } catch (e: Exception) {
                sendEffect(LoginUiEffect.ShowError("Lỗi hệ thống: ${e.message}"))
            } finally {
                updateState { it.copy(isLoading = false) }
            }
        }
    }

    fun shouldShowBiometricAtStartup(): Boolean {
        val hasSession = checkSessionUseCase.hasLocalSession()
        val biometricEnabled = getBiometricStatusUseCase.isEnabledSync()
        return hasSession && biometricEnabled
    }

    fun hasLocalSession(): Boolean = checkSessionUseCase.hasLocalSession()
}
