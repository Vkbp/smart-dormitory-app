package com.ktx.dormitory.student.maintenance.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ktx.dormitory.core.util.DateTimeUtils
import com.ktx.dormitory.navigation.components.LoadingView
import com.ktx.dormitory.student.maintenance.domain.model.MaintenanceStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceDetailScreen(
    navController: NavController,
    requestId: String,
    viewModel: MaintenanceViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val request = remember(uiState.history, requestId) {
        uiState.history.find { it.id == requestId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết bảo trì", fontWeight = FontWeight.Bold) },
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
                    Text("Không tìm thấy thông tin yêu cầu", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Status Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Trạng thái hiện tại",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StatusTagLarge(status = request.status)
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Ngày gửi",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = DateTimeUtils.formatRelativeTime(request.createdAt),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Information Section
            Text(
                text = "Thông tin chi tiết",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            DetailField(label = "Mã yêu cầu", value = request.id ?: "N/A")
            DetailField(label = "Phòng", value = request.roomId ?: "N/A")
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Mô tả sự cố",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text(
                    text = request.description ?: "Không có mô tả chi tiết.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Image Section
            if (!request.imageUrl.isNullOrBlank()) {
                Text(
                    text = "Hình ảnh minh chứng",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                AsyncImage(
                    model = request.imageUrl,
                    contentDescription = "Maintenance Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 400.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = "Không có hình ảnh đính kèm",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun DetailField(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun StatusTagLarge(status: MaintenanceStatus) {
    val (color, text) = when (status) {
        MaintenanceStatus.PENDING -> Color(0xFFFF9800) to "Đang chờ xử lý"
        MaintenanceStatus.IN_PROGRESS -> Color(0xFF2196F3) to "Đang được sửa chữa"
        MaintenanceStatus.DONE -> Color(0xFF4CAF50) to "Đã hoàn thành"
        MaintenanceStatus.REJECTED -> Color(0xFFF44336) to "Đã từ chối"
        MaintenanceStatus.UNKNOWN -> Color.Gray to "Không xác định"
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text.uppercase(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = color,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
