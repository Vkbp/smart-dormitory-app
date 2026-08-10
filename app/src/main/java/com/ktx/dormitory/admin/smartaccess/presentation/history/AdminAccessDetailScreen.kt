package com.ktx.dormitory.admin.smartaccess.presentation.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ktx.dormitory.core.util.DataFormatter
import com.ktx.dormitory.core.util.DateTimeUtils
import com.ktx.dormitory.student.access.domain.model.AccessLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAccessDetailScreen(
    navController: NavController,
    log: AccessLog?
) {
    if (log == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không tìm thấy dữ liệu")
        }
        return
    }

    val scrollState = rememberScrollState()
    val statusColor = if (log.decision == "GRANTED") Color(0xFF4CAF50) else MaterialTheme.colorScheme.error

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết hoạt động", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (log.decision == "GRANTED") Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (log.decision == "GRANTED") "MỞ CỬA THÀNH CÔNG" else "MỞ CỬA THẤT BẠI",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = statusColor,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ID Giao dịch: ${log.id}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Information Section
            DetailGroup(title = "Thông tin chung") {
                DetailItem("Tòa nhà", log.buildingName ?: DataFormatter.formatId(log.buildingId), Icons.Default.Business)
                DetailItem("Cổng/Phòng", log.gateName ?: DataFormatter.formatId(log.gateId), Icons.Default.DoorSliding)
                DetailItem("Thời gian", DateTimeUtils.formatIsoDateTime(log.eventTimestamp), Icons.Default.AccessTime)
                DetailItem("Phương thức", DataFormatter.formatAccessMethod(log.method), Icons.Default.SettingsRemote)
            }

            DetailGroup(title = "Đối tượng thực hiện") {
                val operatorDisplay = log.operatorName ?: DataFormatter.formatOperator(log.operatorId)
                DetailItem("Người thực hiện", operatorDisplay, Icons.Default.AdminPanelSettings)
                
                val studentDisplay = if (log.studentId != null) {
                    if (log.studentName != null) {
                        "${log.studentName} (${log.studentCode ?: DataFormatter.formatId(log.studentId)})"
                    } else {
                        "ID: ${DataFormatter.formatId(log.studentId)}"
                    }
                } else {
                    "Không chỉ định"
                }
                DetailItem("Sinh viên được mở", studentDisplay, Icons.Default.Person)
            }

            if (!log.denialReason.isNullOrBlank()) {
                DetailGroup(title = "Ghi chú & Lý do") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = log.denialReason,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun DetailGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
        )
        content()
    }
}

@Composable
fun DetailItem(label: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            }
        }
    }
}
