package com.ktx.dormitory.student.room.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomTransferDetailScreen(
    navController: NavController,
    requestId: Long,
    viewModel: RoomTransferViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val request = remember(state.history, requestId) {
        state.history.find { it.id == requestId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết đổi phòng", fontWeight = FontWeight.Bold) },
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
                Text("Không tìm thấy thông tin yêu cầu")
            }
            return@Scaffold
        }

        val statusColor = when (request.status) {
            "APPROVED" -> Color(0xFF4CAF50)
            "REJECTED" -> MaterialTheme.colorScheme.error
            else -> Color(0xFFFF9800)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.05f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = when(request.status) {
                            "APPROVED" -> "Yêu cầu đã được chấp thuận"
                            "REJECTED" -> "Yêu cầu bị từ chối"
                            else -> "Đang chờ xử lý"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ngày gửi: ${DateTimeUtils.formatIsoDateTime(request.createdAt)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Thông tin yêu cầu",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            DetailItem(label = "Phòng hiện tại", value = request.currentRoomName ?: "N/A")
            DetailItem(label = "Phòng mong muốn", value = request.targetRoomName ?: "Admin sắp xếp")
            DetailItem(label = "Mã yêu cầu", value = "#${request.id}")

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Lý do đổi phòng",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = request.reason,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            if (!request.adminNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Phản hồi từ Ban Quản Lý",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = request.adminNote,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cập nhật lúc: ${DateTimeUtils.formatIsoDateTime(request.updatedAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.SemiBold)
    }
}
