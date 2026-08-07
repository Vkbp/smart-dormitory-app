package com.ktx.dormitory.student.access.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ktx.dormitory.core.util.DateTimeUtils
import com.ktx.dormitory.navigation.components.LoadingView
import com.ktx.dormitory.student.access.domain.model.CurfewRequestType
import com.ktx.dormitory.student.access.domain.model.CurfewStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurfewDetailScreen(
    navController: NavController,
    requestId: String,
    viewModel: AccessViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val request = remember(uiState.curfewRequests, requestId) {
        uiState.curfewRequests.find { it.id == requestId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết yêu cầu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (request == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                if (uiState.isLoading) {
                    LoadingView()
                } else {
                    Text("Không tìm thấy thông tin đơn", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                }
            }
            return@Scaffold
        }

        val statusColor = when (request.status) {
            CurfewStatus.PENDING -> Color(0xFFFFA000)
            CurfewStatus.APPROVED -> Color(0xFF4CAF50)
            CurfewStatus.REJECTED -> MaterialTheme.colorScheme.error
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Status Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.05f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when(request.status) {
                            CurfewStatus.PENDING -> Icons.Default.Pending
                            CurfewStatus.APPROVED -> Icons.Default.CheckCircle
                            CurfewStatus.REJECTED -> Icons.Default.Error
                        },
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = when(request.status) {
                                CurfewStatus.PENDING -> "Đang chờ duyệt"
                                CurfewStatus.APPROVED -> "Yêu cầu đã được chấp nhận"
                                CurfewStatus.REJECTED -> "Yêu cầu bị từ chối"
                            },
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                        Text(
                            text = "Ngày gửi: ${DateTimeUtils.formatIsoDateTime(request.createdAt ?: "")}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Thông tin đơn",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            DetailItem(label = "Loại yêu cầu", value = if (request.requestType == CurfewRequestType.LATE_RETURN) "Về trễ" else "Vắng mặt")
            DetailItem(label = "Mã đơn", value = request.id)
            
            if (request.requestType == CurfewRequestType.ABSENCE && request.startDate != null) {
                DetailItem(label = "Bắt đầu từ", value = DateTimeUtils.formatIsoDateTime(request.startDate))
            }
            
            DetailItem(label = "Dự kiến có mặt", value = DateTimeUtils.formatIsoDateTime(request.expectedArrivalTime))

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Lý do",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = request.reason,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            if (!request.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Ghi chú thêm",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = request.note,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (request.status != CurfewStatus.PENDING) {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Thông tin xét duyệt",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                DetailItem(label = "Người duyệt", value = request.approvedBy ?: "Hệ thống")
                DetailItem(label = "Thời gian duyệt", value = DateTimeUtils.formatIsoDateTime(request.approvedAt ?: ""))
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.SemiBold)
    }
}
