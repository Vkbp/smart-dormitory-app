package com.ktx.dormitory.admin.checkout.presentation

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ktx.dormitory.admin.common.data.dto.response.CheckoutRequestResponseDto
import com.ktx.dormitory.ui.components.EmptyView
import com.ktx.dormitory.ui.components.ErrorView
import com.ktx.dormitory.ui.components.LoadingView
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutApprovalScreen(
    navController: NavController,
    viewModel: CheckoutApprovalViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedRequest by remember { mutableStateOf<CheckoutRequestResponseDto?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CheckoutApprovalUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Duyệt trả phòng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(CheckoutApprovalUiEvent.LoadRequests(refresh = true)) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Tải lại")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val statuses = listOf("PENDING", "APPROVED", "REJECTED", "COMPLETED")
            ScrollableTabRow(
                selectedTabIndex = statuses.indexOf(uiState.selectedStatus).coerceAtLeast(0),
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                statuses.forEach { status ->
                    Tab(
                        selected = uiState.selectedStatus == status,
                        onClick = { viewModel.onEvent(CheckoutApprovalUiEvent.ChangeStatus(status)) },
                        text = {
                            Text(
                                text = when (status) {
                                    "PENDING" -> "Chờ duyệt"
                                    "APPROVED" -> "Đã duyệt"
                                    "REJECTED" -> "Từ chối"
                                    "COMPLETED" -> "Hoàn tất"
                                    else -> status
                                },
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (uiState.isLoading && uiState.requests.isEmpty()) {
                    LoadingView()
                } else if (uiState.error != null && uiState.requests.isEmpty()) {
                    ErrorView(
                        message = uiState.error ?: "Đã có lỗi xảy ra",
                        onRetry = { viewModel.onEvent(CheckoutApprovalUiEvent.LoadRequests(refresh = true)) }
                    )
                } else if (uiState.requests.isEmpty()) {
                    EmptyView(message = "Không có yêu cầu nào trong danh sách này")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.requests, key = { it.id ?: UUID.randomUUID() }) { request ->
                            CheckoutRequestCard(
                                request = request,
                                onClick = { selectedRequest = request }
                            )
                        }
                        if (uiState.isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedRequest != null) {
        CheckoutDetailDialog(
            request = selectedRequest!!,
            onDismiss = { selectedRequest = null },
            onApprove = {
                selectedRequest?.id?.let { id ->
                    val nextStatus = if (selectedRequest?.status == "APPROVED") "COMPLETED" else "APPROVED"
                    viewModel.onEvent(
                        CheckoutApprovalUiEvent.ReviewRequest(
                            id,
                            nextStatus,
                            null
                        )
                    )
                }
                selectedRequest = null
            },
            onReject = { reason ->
                selectedRequest?.id?.let { id ->
                    viewModel.onEvent(
                        CheckoutApprovalUiEvent.ReviewRequest(
                            id,
                            "REJECTED",
                            reason
                        )
                    )
                }
                selectedRequest = null
            }
        )
    }
}

@Composable
fun CheckoutRequestCard(
    request: CheckoutRequestResponseDto,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.fullName ?: "Sinh viên",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = request.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "MSSV: ${request.studentCode}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Phòng: ${request.roomCode} - Giường: ${request.bedCode}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Ngày dự kiến: ${request.intendedCheckoutDate?.split("T")?.get(0)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CheckoutDetailDialog(
    request: CheckoutRequestResponseDto,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: (String) -> Unit
) {
    var showApproveConfirm by remember { mutableStateOf(false) }
    var showRejectInput by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chi tiết đơn trả phòng",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Đóng")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    DetailSection(title = "Thông tin sinh viên") {
                        DetailItem(label = "Họ tên", value = request.fullName ?: "N/A")
                        DetailItem(label = "MSSV", value = request.studentCode ?: "N/A")
                        DetailItem(label = "Vị trí", value = "${request.roomCode} / ${request.bedCode}")
                    }

                    DetailSection(title = "Nội dung yêu cầu") {
                        DetailItem(label = "Ngày dự kiến", value = request.intendedCheckoutDate?.split("T")?.get(0) ?: "N/A")
                        DetailItem(label = "Lý do trả phòng", value = request.reason ?: "Không có lý do")
                    }

                    DetailSection(title = "Thông tin hoàn tiền") {
                        DetailItem(label = "Ngân hàng", value = request.bankName ?: "N/A")
                        DetailItem(label = "Số tài khoản", value = request.bankAccountNumber ?: "N/A")
                        
                        Surface(
                            color = MaterialTheme.colorScheme.infoContainer(),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.infoColor(),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Thông tin này dùng để Ban quản lý đối chiếu và Web Admin sẽ xuất file gửi Phòng Tài vụ để hoàn tiền/cọc",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.infoColor()
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (request.status == "PENDING") {
                        Button(
                            onClick = { showRejectInput = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("TỪ CHỐI", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { showApproveConfirm = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), // Dark Green
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                "PHÊ DUYỆT & CHỐT CÔNG NỢ",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        }
                    } else if (request.status == "APPROVED") {
                        Button(
                            onClick = onApprove,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("HOÀN TẤT HỒ SƠ", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // REJECTED or COMPLETED - Close only
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("ĐÓNG", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showApproveConfirm) {
        AlertDialog(
            onDismissRequest = { showApproveConfirm = false },
            title = { Text("Xác nhận duyệt trả phòng?") },
            text = {
                Text(
                    text = "CẢNH BÁO: Thao tác này sẽ ngay lập tức thu hồi giường và tước quyền ra vào KTX (RFID/FaceID) của sinh viên. Hồ sơ sẽ được chốt công nợ để chờ giải ngân. Bạn có chắc chắn sinh viên đã dọn đồ xong?",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showApproveConfirm = false
                        onApprove()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Đồng ý duyệt")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApproveConfirm = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    if (showRejectInput) {
        var rejectReason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showRejectInput = false },
            title = { Text("Lý do từ chối") },
            text = {
                OutlinedTextField(
                    value = rejectReason,
                    onValueChange = { rejectReason = it },
                    label = { Text("Nhập lý do từ chối...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rejectReason.isNotBlank()) {
                            showRejectInput = false
                            onReject(rejectReason)
                        }
                    },
                    enabled = rejectReason.isNotBlank()
                ) {
                    Text("Gửi từ chối")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectInput = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        content()
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

// Extension functions for colors to keep it clean or use direct values if not available
@Composable
fun ColorScheme.infoContainer() = secondaryContainer

@Composable
fun ColorScheme.infoColor() = onSecondaryContainer
