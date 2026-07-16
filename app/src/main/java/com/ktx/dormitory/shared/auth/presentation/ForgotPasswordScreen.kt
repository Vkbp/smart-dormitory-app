package com.ktx.dormitory.shared.auth.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by accountViewModel.uiState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var resetToken by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var isTokenSent by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        accountViewModel.effect.collect { effect ->
            when (effect) {
                is AccountUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    if (effect.message.contains("gửi", ignoreCase = true)) {
                        isTokenSent = true
                    }
                }
                AccountUiEffect.NavigateBack -> navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quên mật khẩu") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isTokenSent) {
                Text(
                    text = "Nhập email của bạn để nhận mã khôi phục",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email sinh viên") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        accountViewModel.onEvent(AccountUiEvent.ForgotPassword(email))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading && email.isNotBlank()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("GỬI MÃ KHÔI PHỤC")
                    }
                }
            } else {
                Text(
                    text = "Đã gửi mã khôi phục tới $email",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = resetToken,
                    onValueChange = { resetToken = it },
                    label = { Text("Mã khôi phục (từ email)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Mật khẩu mới") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        accountViewModel.onEvent(AccountUiEvent.ResetPassword(resetToken, newPassword))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading && resetToken.isNotBlank() && newPassword.length >= 6
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("XÁC NHẬN ĐỔI MẬT KHẨU")
                    }
                }

                TextButton(onClick = { isTokenSent = false }) {
                    Text("Gửi lại mã khôi phục")
                }
            }
        }
    }
}
