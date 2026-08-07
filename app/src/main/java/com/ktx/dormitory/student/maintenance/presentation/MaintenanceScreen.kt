package com.ktx.dormitory.student.maintenance.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ktx.dormitory.core.util.DateTimeUtils
import com.ktx.dormitory.navigation.Screen
import com.ktx.dormitory.student.maintenance.domain.model.MaintenanceRequest
import com.ktx.dormitory.student.maintenance.domain.model.MaintenanceStatus
import com.ktx.dormitory.navigation.components.ErrorView
import com.ktx.dormitory.navigation.components.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen(
    navController: NavController,
    viewModel: MaintenanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử bảo trì", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(MaintenanceUiEvent.LoadHistory) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Tải lại")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateMaintenance.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Báo sự cố")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoading -> LoadingView()
                uiState.error != null -> ErrorView(
                    message = uiState.error!!,
                    onRetry = { viewModel.onEvent(MaintenanceUiEvent.LoadHistory) }
                )
                uiState.history.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Chưa có báo cáo sự cố nào", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.history) { request ->
                            MaintenanceItem(request) {
                                navController.navigate(Screen.MaintenanceDetail.createRoute(request.id ?: ""))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MaintenanceItem(request: MaintenanceRequest, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusTag(status = request.status)
                Text(
                    text = DateTimeUtils.formatRelativeTime(request.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = request.description ?: "Không có mô tả",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun StatusTag(status: MaintenanceStatus) {
    val (color, text) = when (status) {
        MaintenanceStatus.PENDING -> Color(0xFFFF9800) to "Đang chờ"
        MaintenanceStatus.IN_PROGRESS -> Color(0xFF2196F3) to "Đang xử lý"
        MaintenanceStatus.DONE -> Color(0xFF4CAF50) to "Hoàn thành"
        MaintenanceStatus.REJECTED -> Color(0xFFF44336) to "Từ chối"
        MaintenanceStatus.UNKNOWN -> Color.Gray to "Không xác định"
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
