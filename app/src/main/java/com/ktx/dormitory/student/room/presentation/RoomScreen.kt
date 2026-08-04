package com.ktx.dormitory.student.room.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ktx.dormitory.core.util.DateTimeUtils
import com.ktx.dormitory.navigation.Screen
import com.ktx.dormitory.ui.components.LoadingView
import com.ktx.dormitory.ui.components.ErrorView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomScreen(navController: NavController, viewModel: RoomViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin chỗ ở", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            val room = uiState.roomInfo
            val error = uiState.error
            when {
                uiState.isLoading -> LoadingView()
                error != null -> ErrorView(
                    message = error,
                    onRetry = { viewModel.loadRoomInfo() }
                )
                room != null -> {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(androidx.compose.foundation.rememberScrollState())
                    ) {
                        StayProgressSection(room.expectedCheckOutAt)
                        
                        uiState.latestUtility?.let { utility ->
                            Spacer(modifier = Modifier.height(16.dp))
                            UtilitySummaryCard(utility) {
                                navController.navigate(Screen.RoomUtilities.route)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        RoomDetailCard("Tòa nhà", room.building, Icons.Default.Business)
                        RoomDetailCard("Tầng", room.floor?.toString(), Icons.Default.Layers)
                        RoomDetailCard("Số phòng", room.roomCode, Icons.Default.MeetingRoom)
                        RoomDetailCard("Vị trí giường", room.bedCode, Icons.Default.Bed)
                        RoomDetailCard("Trạng thái", room.status, Icons.Default.Info)

                        Spacer(modifier = Modifier.weight(1f))

                        QuickActionSection(navController)
                    }
                }
                else -> {
                    if (!uiState.isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Chưa có thông tin phòng")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StayProgressSection(expectedCheckOutAt: String?) {
    val daysRemaining = DateTimeUtils.calculateDaysRemaining(expectedCheckOutAt)
    
    // Chỉ ẩn khi ngày không hợp lệ (nhỏ hơn 0)
    if (daysRemaining < 0) return

    val totalDays = 180f // Giả định một học kỳ khoảng 180 ngày để tính progress
    val progress = (daysRemaining.toFloat() / totalDays).coerceIn(0f, 1f)
    val color = if (daysRemaining < 15) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tiến độ lưu trú",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$daysRemaining ngày còn lại",
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(MaterialTheme.shapes.medium),
                color = color,
                trackColor = color.copy(alpha = 0.2f),
            )
            if (daysRemaining < 15) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sắp hết hạn lưu trú! Vui lòng làm đơn gia hạn sớm.",
                    style = MaterialTheme.typography.bodySmall,
                    color = color
                )
            }
        }
    }
}

@Composable
fun UtilitySummaryCard(utility: com.ktx.dormitory.student.room.domain.model.UtilityReading, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = Color(0xFFFFC107))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Chỉ số điện (Tháng này)", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = "${utility.oldReading} -> ${utility.newReading ?: "Đang sử dụng..."}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
fun RoomDetailCard(label: String, value: String?, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium)
                Text(
                    text = value ?: "Không xác định",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuickActionSection(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tiện ích nhanh",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionButton(
                "Điện nước",
                Icons.Default.ElectricBolt,
                Modifier.weight(1f)
            ) { navController.navigate(Screen.RoomUtilities.route) }

            QuickActionButton(
                "Đổi phòng",
                Icons.Default.SwapHoriz,
                Modifier.weight(1f)
            ) { navController.navigate(Screen.RoomTransfer.route) }
            
            QuickActionButton(
                "Trả phòng",
                Icons.Default.ExitToApp,
                Modifier.weight(1f)
            ) { navController.navigate(Screen.Checkout.route) }
        }
    }
}

@Composable
fun QuickActionButton(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
        }
    }
}
