package com.ktx.dormitory.student.face.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ktx.dormitory.core.util.DataFormatter
import com.ktx.dormitory.core.util.DateTimeUtils
import com.ktx.dormitory.navigation.components.LoadingView
import com.ktx.dormitory.student.face.data.dto.response.VerificationAttemptDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceVerificationDetailScreen(
    navController: NavController,
    attempt: VerificationAttemptDto?
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết xác thực", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (attempt == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                LoadingView()
            }
            return@Scaffold
        }

        val statusColor = when (attempt.status) {
            "SUCCESS" -> Color(0xFF4CAF50)
            "FAIL" -> MaterialTheme.colorScheme.error
            else -> Color(0xFFFFC107)
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
                        imageVector = when (attempt.status) {
                            "SUCCESS" -> Icons.Default.CheckCircle
                            "FAIL" -> Icons.Default.Error
                            else -> Icons.Default.Timer
                        },
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = DataFormatter.formatVerificationStatus(attempt.status).uppercase(),
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
                        value = DateTimeUtils.formatIsoDateTime(attempt.attemptedAt)
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                    
                    DetailRow(
                        label = "Thiết bị / Cổng",
                        value = attempt.gateDeviceId ?: "Thiết bị không xác định"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                    DetailRow(
                        label = "Độ tin cậy AI",
                        value = "${(attempt.confidenceScore * 100).toInt()}%"
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                    
                    DetailRow(
                        label = "ID Lần thử",
                        value = DataFormatter.formatId(attempt.attemptId.toString())
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (attempt.status == "FAIL") {
                Text(
                    text = "Lưu ý: Nếu bạn liên tục không thể xác thực khuôn mặt, hãy kiểm tra lại điều kiện ánh sáng hoặc liên hệ BQL để đăng ký lại bộ dữ liệu khuôn mặt.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
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
