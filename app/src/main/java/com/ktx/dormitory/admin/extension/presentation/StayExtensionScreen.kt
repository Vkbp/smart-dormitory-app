package com.ktx.dormitory.admin.extension.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.ktx.dormitory.ui.components.LoadingView
import com.ktx.dormitory.ui.components.ErrorView
import com.ktx.dormitory.ui.components.EmptyView
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StayExtensionScreen(
    navController: NavController,
    viewModel: StayExtensionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is StayExtensionUiEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Duyệt gia hạn", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading && uiState.extensions.isEmpty()) {
                LoadingView()
            } else if (uiState.error != null && uiState.extensions.isEmpty()) {
                ErrorView(message = uiState.error, onRetry = { viewModel.onEvent(StayExtensionUiEvent.LoadExtensions(refresh = true)) })
            } else if (uiState.extensions.isEmpty()) {
                EmptyView(message = "Không có yêu cầu nào chờ duyệt")
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.extensions, key = { it.id }) { extension ->
                        ExtensionRequestItem(
                            extension = extension,
                            onApprove = { viewModel.onEvent(StayExtensionUiEvent.ReviewExtension(extension.id, "APPROVED", null)) },
                            onReject = { reason -> viewModel.onEvent(StayExtensionUiEvent.ReviewExtension(extension.id, "REJECTED", reason)) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun ExtensionRequestItem(
    extension: com.ktx.dormitory.admin.common.data.dto.response.StayExtensionResponseDto,
    onApprove: () -> Unit,
    onReject: (String) -> Unit
) {
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectReason by remember { mutableStateOf("") }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Từ chối gia hạn") },
            text = {
                OutlinedTextField(
                    value = rejectReason,
                    onValueChange = { rejectReason = it },
                    label = { Text("Lý do từ chối") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = { onReject(rejectReason); showRejectDialog = false }) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    ListItem(
        headlineContent = { Text(extension.studentName ?: "Sinh viên", fontWeight = FontWeight.Bold) },
        supportingContent = {
            Column {
                Text("MSSV: ${extension.studentId}")
                Text("Phòng: ${extension.roomCode ?: "N/A"}")
            }
        },
        trailingContent = {
            Row {
                IconButton(onClick = onApprove) {
                    Icon(Icons.Default.Check, contentDescription = "Approve", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { showRejectDialog = true }) {
                    Icon(Icons.Default.Close, contentDescription = "Reject", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}
