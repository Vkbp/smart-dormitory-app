package com.ktx.dormitory.admin.face.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ktx.dormitory.admin.common.domain.model.FaceProfile
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceApprovalScreen(
    navController: NavController,
    viewModel: FaceApprovalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showRejectDialog by remember { mutableStateOf<UUID?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Hiển thị Snackbar khi có thông báo
    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it, actionLabel = "Thử lại")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Duyệt khuôn mặt sinh viên") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(FaceApprovalUiEvent.LoadPendingProfiles) }) {
                        Text("Tải lại")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.pendingProfiles.isEmpty() && !uiState.isLoading) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Không có hồ sơ chờ duyệt", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                items(uiState.pendingProfiles, key = { it.profileId }) { profile ->
                    FaceProfileCard(
                        profile = profile,
                        onApprove = { viewModel.onEvent(FaceApprovalUiEvent.ApproveProfile(profile.profileId)) },
                        onReject = { showRejectDialog = profile.profileId }
                    )
                }
            }

            if (uiState.isLoading) {
                Surface(
                    color = Color.Black.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Đang xử lý...", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { viewModel.onEvent(FaceApprovalUiEvent.LoadPendingProfiles) },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Text("Hủy / Tải lại")
                        }
                    }
                }
            }
        }
    }

    showRejectDialog?.let { profileId ->
        var reason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showRejectDialog = null },
            title = { Text("Từ chối khuôn mặt") },
            text = {
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Lý do từ chối") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onEvent(FaceApprovalUiEvent.RejectProfile(profileId, reason))
                        showRejectDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Xác nhận từ chối") }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = null }) { Text("Hủy") }
            }
        )
    }
}

@Composable
fun FaceProfileCard(
    profile: FaceProfile,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = profile.faceImageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Student ID: ${profile.studentId}", style = MaterialTheme.typography.titleSmall)
                    Text("Ngày gửi: ${profile.createdAt ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onApprove, modifier = Modifier.weight(1f)) {
                    Text("Duyệt")
                }
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Từ chối")
                }
            }
        }
    }
}
