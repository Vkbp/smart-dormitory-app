package com.ktx.dormitory.admin.extension.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val sheetState = rememberModalBottomSheetState()
    var showProfileSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is StayExtensionUiEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    LaunchedEffect(uiState.selectedStudentProfile) {
        if (uiState.selectedStudentProfile != null) {
            showProfileSheet = true
        }
    }

    if (showProfileSheet && uiState.selectedStudentProfile != null) {
        ModalBottomSheet(
            onDismissRequest = { 
                showProfileSheet = false
                viewModel.onEvent(StayExtensionUiEvent.ClearProfile)
            },
            sheetState = sheetState
        ) {
            StudentProfileContent(profile = uiState.selectedStudentProfile!!)
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
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(StayExtensionUiEvent.LoadExtensions(refresh = true)) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Tải lại")
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(uiState.extensions, key = { it.id ?: java.util.UUID.randomUUID() }) { extension ->
                        ExtensionRequestItem(
                            extension = extension,
                            isLoading = uiState.isLoading,
                            onApprove = { 
                                extension.id?.let { id ->
                                    viewModel.onEvent(StayExtensionUiEvent.ReviewExtension(id, "APPROVED", null)) 
                                }
                            },
                            onReject = { reason -> 
                                extension.id?.let { id ->
                                    viewModel.onEvent(StayExtensionUiEvent.ReviewExtension(id, "REJECTED", reason)) 
                                }
                            },
                            onViewProfile = {
                                extension.studentId?.let { id ->
                                    viewModel.onEvent(StayExtensionUiEvent.LoadStudentProfile(id))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExtensionRequestItem(
    extension: com.ktx.dormitory.admin.common.data.dto.response.StayExtensionResponseDto,
    isLoading: Boolean,
    onApprove: () -> Unit,
    onReject: (String) -> Unit,
    onViewProfile: () -> Unit
) {
    val context = LocalContext.current
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectReason by remember { mutableStateOf("") }
    var isReasonError by remember { mutableStateOf(false) }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { 
                showRejectDialog = false 
                isReasonError = false
            },
            title = { Text("Từ chối gia hạn", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Bạn đang từ chối đơn của ${extension.fullName}. Vui lòng nhập lý do cụ thể.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { 
                            rejectReason = it
                            if (it.isNotBlank()) isReasonError = false
                        },
                        label = { Text("Lý do từ chối") },
                        isError = isReasonError,
                        supportingText = {
                            if (isReasonError) {
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
                        if (rejectReason.isBlank()) {
                            isReasonError = true
                        } else {
                            onReject(rejectReason)
                            showRejectDialog = false 
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xác nhận từ chối")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showRejectDialog = false 
                    isReasonError = false
                }) {
                    Text("Hủy")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onViewProfile() }
                ) {
                    Text(
                        text = extension.fullName ?: "Sinh viên",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "MSSV: ${extension.studentCode ?: "N/A"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Surface(
                    color = when(extension.status.uppercase()) {
                        "PENDING" -> MaterialTheme.colorScheme.primaryContainer
                        "APPROVED" -> Color(0xFFE8F5E9)
                        "REJECTED" -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = extension.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = when(extension.status.uppercase()) {
                            "PENDING" -> MaterialTheme.colorScheme.onPrimaryContainer
                            "APPROVED" -> Color(0xFF2E7D32)
                            "REJECTED" -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Lý do gia hạn:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = extension.reason ?: "Lý do chưa xác định", // In text thẳng ra màn hình theo AGENT.md
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            if (extension.status.uppercase() == "REJECTED" && !extension.rejectReason.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lý do từ chối: ${extension.rejectReason}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoItem(
                    label = "Phòng hiện tại",
                    value = extension.roomCode ?: "N/A",
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    label = "Ngày gửi",
                    value = extension.createdAt?.take(10) ?: "N/A",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val isPending = extension.status.uppercase() == "PENDING"
            
            if (isPending) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { showRejectDialog = true },
                        enabled = !isLoading,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Từ chối")
                    }
                    
                    Button(
                        onClick = onApprove,
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Duyệt đơn")
                    }
                }
            } else {
                // Sau khi Duyệt xong (APPROVED): Hiện 2 nút xem PDF kết quả theo AGENT.md
                if (extension.status.uppercase() == "APPROVED") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        extension.contractPdfUrl?.let { url ->
                            AssistChip(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                },
                                label = { Text("Xem Hợp đồng") },
                                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }
                        extension.commitmentPdfUrl?.let { url ->
                            AssistChip(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                },
                                label = { Text("Xem Cam kết") },
                                leadingIcon = { Icon(Icons.Default.HistoryEdu, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentProfileContent(profile: com.ktx.dormitory.shared.profile.domain.model.UserProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Hồ sơ sinh viên",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        ProfileItem(label = "Họ tên", value = profile.fullName ?: "N/A")
        ProfileItem(label = "Khoa", value = profile.faculty ?: "N/A")
        ProfileItem(label = "Khóa", value = profile.academicYear ?: "N/A")
        ProfileItem(label = "Email", value = profile.email ?: "N/A")
        ProfileItem(label = "Số điện thoại", value = profile.phone ?: "N/A")
        ProfileItem(label = "Địa chỉ", value = profile.permanentAddress ?: "N/A")
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        
        Text(
            text = "Thông tin gia đình",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        ProfileItem(label = "Họ tên Cha", value = profile.fatherName ?: "N/A")
        ProfileItem(label = "SĐT Cha", value = profile.fatherPhone ?: "N/A")
        ProfileItem(label = "Họ tên Mẹ", value = profile.motherName ?: "N/A")
        ProfileItem(label = "SĐT Mẹ", value = profile.motherPhone ?: "N/A")
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun InfoItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
