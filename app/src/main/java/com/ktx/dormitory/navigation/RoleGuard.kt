package com.ktx.dormitory.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ktx.dormitory.shared.auth.presentation.LoginViewModel

/**
 * High-level component to guard routes based on user role.
 */
@Composable
fun RoleGuard(
    loginViewModel: LoginViewModel,
    content: @Composable () -> Unit
) {
    val loginState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val userData = loginState.userData
    
    when {
        userData == null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Đang xác thực quyền...", style = MaterialTheme.typography.labelSmall)
                    
                    TextButton(onClick = { 
                        loginViewModel.logout { 
                            // Navigation handled by AuthEventBus in MainActivity
                        }
                    }) {
                        Text("Hủy và quay lại Đăng nhập", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        userData.role?.uppercase()?.let { role ->
            role.contains("STUDENT") || role.contains("USER") || 
            role.contains("ADMIN") || role.contains("STAFF")
        } ?: true -> {
            content()
        }
        else -> {
            LaunchedEffect(Unit) {
                loginViewModel.logout { }
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tài khoản không hợp lệ. Đang đăng xuất...", 
                     style = MaterialTheme.typography.bodyMedium,
                     color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
