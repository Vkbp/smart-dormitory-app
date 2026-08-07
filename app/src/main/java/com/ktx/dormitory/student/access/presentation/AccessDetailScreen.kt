package com.ktx.dormitory.student.access.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ktx.dormitory.core.util.DateTimeUtils
import com.ktx.dormitory.navigation.components.LoadingView
import com.ktx.dormitory.student.access.domain.model.UnifiedTimelineEvent
import com.ktx.dormitory.student.access.domain.model.UnifiedEventType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessDetailScreen(
    navController: NavController,
    event: UnifiedTimelineEvent?
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết ra vào", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (event == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                LoadingView()
            }
            return@Scaffold
        }

        val statusColor = when (event.type) {
            UnifiedEventType.SUCCESS -> Color(0xFF4CAF50)
            UnifiedEventType.ACCESS_DENIED -> MaterialTheme.colorScheme.error
            UnifiedEventType.VERIFY_FAIL -> Color(0xFFFFC107)
            UnifiedEventType.UNKNOWN -> Color.Gray
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon & Status Header
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = statusColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (event.type) {
                            UnifiedEventType.SUCCESS -> Icons.Default.CheckCircle
                            UnifiedEventType.ACCESS_DENIED -> Icons.Default.Cancel
                            UnifiedEventType.VERIFY_FAIL -> Icons.Default.Warning
                            else -> Icons.Default.Help
                        },
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when (event.type) {
                    UnifiedEventType.SUCCESS -> "THÀNH CÔNG"
                    UnifiedEventType.ACCESS_DENIED -> "BỊ TỪ CHỐI"
                    UnifiedEventType.VERIFY_FAIL -> "LỖI XÁC THỰC"
                    UnifiedEventType.UNKNOWN -> "KHÔNG XÁC ĐỊNH"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = statusColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    DetailRow(
                        label = "Thời gian",
                        value = DateTimeUtils.formatIsoDateTime(event.timestamp ?: "")
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                    
                    DetailRow(
                        label = "Địa điểm",
                        value = event.gateId ?: event.buildingId ?: "Cổng KTX"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                    DetailRow(
                        label = "Phương thức",
                        value = event.method ?: "Thẻ RFID"
                    )
                    
                    if (event.confidenceScore != null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                        DetailRow(
                            label = "Độ tin cậy AI",
                            value = "${(event.confidenceScore * 100).toInt()}%"
                        )
                    }

                    if (!event.denialReason.isNullOrBlank()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                        Text(
                            text = "Lý do từ chối",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = event.denialReason,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (event.type == UnifiedEventType.ACCESS_DENIED && event.denialReason?.contains("giới nghiêm") == true) {
                OutlinedButton(
                    onClick = { navController.navigate("curfew_request") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.AddAlert, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("TẠO ĐƠN VÀO TRỄ KHẨN CẤP")
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
