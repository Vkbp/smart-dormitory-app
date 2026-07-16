package com.ktx.dormitory.shared.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ktx.dormitory.R
import com.ktx.dormitory.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    var showBiometricPrompt by remember { mutableStateOf(false) }
    var hasNavigated by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        loginViewModel.effect.collectLatest { effect ->
            if (hasNavigated) return@collectLatest
            
            when (effect) {
                is LoginUiEffect.AuthStatusChecked -> {
                    hasNavigated = true
                    val roleUpper = effect.role?.uppercase() ?: ""
                    val destination = when {
                        roleUpper.contains("ADMIN") || roleUpper.contains("STAFF") -> Screen.AdminDashboard.route
                        effect.role != null -> Screen.StudentHome.route
                        else -> Screen.Login.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
                is LoginUiEffect.NavigateToHome -> {
                    hasNavigated = true
                    val roleUpper = effect.role.uppercase()
                    val destination = when {
                        roleUpper.contains("ADMIN") || roleUpper.contains("STAFF") -> Screen.AdminDashboard.route
                        else -> Screen.StudentHome.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
                is LoginUiEffect.ShowError -> {
                    hasNavigated = true
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(1000)
        if (loginViewModel.shouldShowBiometricAtStartup() && com.ktx.dormitory.core.security.checkBiometricSupport(context)) {
            showBiometricPrompt = true
        } else {
            loginViewModel.onEvent(LoginUiEvent.CheckAuthStatus)
        }
    }

    if (showBiometricPrompt) {
        com.ktx.dormitory.core.security.ShowBiometricPrompt(
            title = "Xác thực truy cập",
            subtitle = "Sử dụng vân tay để tiếp tục vào ứng dụng",
            onSuccess = { result ->
                showBiometricPrompt = false
                loginViewModel.onEvent(LoginUiEvent.BiometricClicked)
            },
            onError = {
                showBiometricPrompt = false
                if (!hasNavigated) {
                    hasNavigated = true
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo_stu),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "SMART DORMITORY",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
