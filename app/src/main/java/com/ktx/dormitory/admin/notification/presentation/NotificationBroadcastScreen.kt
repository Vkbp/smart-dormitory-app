package com.ktx.dormitory.admin.notification.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationBroadcastScreen(
    navController: NavController,
    viewModel: NotificationBroadcastViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedTarget by remember { mutableStateOf("ALL") }
    val targets = listOf("ALL", "STUDENT", "STAFF", "ADMIN")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gửi thông báo toàn hệ thống") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tiêu đề thông báo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Nội dung thông báo") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text("Đối tượng nhận:")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                targets.forEach { target ->
                    FilterChip(
                        selected = selectedTarget == target,
                        onClick = { selectedTarget = target },
                        label = { Text(target) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            uiState.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            uiState.successMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
            }

            Button(
                onClick = { viewModel.onEvent(NotificationBroadcastUiEvent.Broadcast(title, message, selectedTarget)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && message.isNotBlank() && !uiState.isLoading
            ) {
                Text("Bắn thông báo")
            }
        }
    }
}
