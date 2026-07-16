package com.ktx.dormitory.shared.auth.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ktx.dormitory.navigation.Screen
import com.ktx.dormitory.core.security.checkBiometricSupport
import com.ktx.dormitory.core.security.ShowBiometricPrompt
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(navController: NavController,
                loginViewModel: LoginViewModel
                ) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()

    var mssv by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isBiometricAvailable by remember { mutableStateOf(false) }
    var showBiometricDialog by remember { mutableStateOf(false) }
    val hasSession = remember { loginViewModel.hasLocalSession() }

    LaunchedEffect(Unit) {
        isBiometricAvailable = checkBiometricSupport(context)
    }

    LaunchedEffect(Unit) {
        loginViewModel.effect.collectLatest { effect ->
            when (effect) {
                is LoginUiEffect.NavigateToHome -> {
                    val roleUpper = effect.role.uppercase()
                    val destination = when {
                        roleUpper.contains("ADMIN") || roleUpper.contains("STAFF") -> Screen.AdminDashboard.route
                        else -> Screen.StudentHome.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                is LoginUiEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                else -> {}
            }
        }
    }

    if (showBiometricDialog) {
        ShowBiometricPrompt(
            title = "Đăng nhập vân tay",
            subtitle = "Xác thực để truy cập hệ thống",
            onSuccess = { result ->
                showBiometricDialog = false
                loginViewModel.onEvent(LoginUiEvent.BiometricClicked)
            },
            onError = { showBiometricDialog = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Smart Dormitory",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = mssv,
                onValueChange = { mssv = it },
                label = { Text("MSSV hoặc Email") },
                modifier = Modifier.fillMaxWidth().testTag("login_mssv_field"),
                isError = uiState.mssvError != null,
                supportingText = {
                    uiState.mssvError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().testTag("login_password_field"),
                isError = uiState.passwordError != null,
                supportingText = {
                    uiState.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    loginViewModel.onEvent(LoginUiEvent.LoginClicked(mssv, password))
                },
                modifier = Modifier.fillMaxWidth().testTag("login_button"),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp).testTag("login_loading"),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("ĐĂNG NHẬP")
                }
            }

            TextButton(onClick = { navController.navigate(Screen.ForgotPassword.route) }) {
                Text("Quên mật khẩu?")
            }

            if (isBiometricAvailable && hasSession) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { showBiometricDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔐 Đăng nhập bằng Vân tay")
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Ứng dụng dành cho sinh viên đã được duyệt nội trú",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
