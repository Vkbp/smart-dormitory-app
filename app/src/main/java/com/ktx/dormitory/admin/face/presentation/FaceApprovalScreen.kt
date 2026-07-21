package com.ktx.dormitory.admin.face.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ktx.dormitory.admin.common.domain.model.FaceProfile
import com.ktx.dormitory.ui.components.EmptyView
import com.ktx.dormitory.ui.components.LoadingView
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceApprovalScreen(
    navController: NavController,
    viewModel: FaceApprovalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showRejectDialog by remember { mutableStateOf<UUID?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(FaceApprovalUiEvent.ClearMessages)
        }
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it, actionLabel = "Thử lại")
            viewModel.onEvent(FaceApprovalUiEvent.ClearMessages)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Duyệt khuôn mặt", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(FaceApprovalUiEvent.LoadPendingProfiles) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Tải lại")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (uiState.isLoading && uiState.pendingProfiles.isEmpty()) {
                LoadingView(message = "Đang tải danh sách chờ...")
            } else if (uiState.pendingProfiles.isEmpty()) {
                EmptyView(message = "Không có hồ sơ nào đang chờ duyệt")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.pendingProfiles, key = { it.profileId }) { profile ->
                        FaceProfileCard(
                            profile = profile,
                            onApprove = { viewModel.onEvent(FaceApprovalUiEvent.ApproveProfile(profile.profileId)) },
                            onReject = { showRejectDialog = profile.profileId }
                        )
                    }
                }
            }

            if (uiState.isLoading && uiState.pendingProfiles.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }

    showRejectDialog?.let { profileId ->
        var reason by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showRejectDialog = null },
            title = { Text("Từ chối khuôn mặt", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Vui lòng nhập lý do từ chối để sinh viên biết và thực hiện đăng ký lại.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { 
                            reason = it
                            if (it.isNotBlank()) isError = false
                        },
                        label = { Text("Lý do từ chối") },
                        isError = isError,
                        supportingText = {
                            if (isError) {
                                Text("Lý do không được để trống", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reason.isBlank()) {
                            isError = true
                        } else {
                            viewModel.onEvent(FaceApprovalUiEvent.RejectProfile(profileId, reason))
                            showRejectDialog = null
                        }
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = profile.faceImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile.fullName ?: "Sinh viên ẩn danh",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "MSSV: ${profile.studentCode ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = "Ngày gửi: ${profile.createdAt?.take(10) ?: "N/A"}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Từ chối")
                }
                
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Duyệt hồ sơ")
                }
            }
        }
    }
}
