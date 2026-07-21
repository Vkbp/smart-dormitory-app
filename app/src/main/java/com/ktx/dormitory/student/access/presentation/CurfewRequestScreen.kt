package com.ktx.dormitory.student.access.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ktx.dormitory.navigation.Screen
import com.ktx.dormitory.student.access.domain.model.CurfewRequest
import com.ktx.dormitory.student.access.domain.model.CurfewRequestType
import com.ktx.dormitory.student.access.domain.model.CurfewStatus
import com.ktx.dormitory.ui.components.EmptyView
import com.ktx.dormitory.ui.components.ErrorView
import com.ktx.dormitory.ui.components.LoadingView
import com.ktx.dormitory.core.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurfewRequestScreen(
    navController: NavController,
    viewModel: AccessViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đơn về trễ / Vắng mặt", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(AccessUiEvent.FetchCurfewRequests) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Tải lại")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateCurfewRequest.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tạo đơn mới")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoading && uiState.curfewRequests.isEmpty() -> LoadingView()
                uiState.error != null && uiState.curfewRequests.isEmpty() -> ErrorView(
                    message = uiState.error ?: "Không thể tải dữ liệu",
                    onRetry = { viewModel.onEvent(AccessUiEvent.FetchCurfewRequests) }
                )
                uiState.curfewRequests.isEmpty() -> EmptyView(
                    message = "Bạn chưa nộp đơn nào",
                    icon = Icons.Default.Description
                )
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.curfewRequests, key = { it.id }) { request ->
                            CurfewRequestItem(request)
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun CurfewRequestItem(request: CurfewRequest) {
    val statusColor = when (request.status) {
        CurfewStatus.PENDING -> Color(0xFFFFA000)
        CurfewStatus.APPROVED -> Color(0xFF4CAF50)
        CurfewStatus.REJECTED -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (request.requestType == CurfewRequestType.LATE_RETURN) 
                            Icons.Default.AccessTime else Icons.Default.FlightTakeoff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (request.requestType == CurfewRequestType.LATE_RETURN) "Về trễ" else "Vắng mặt",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = when(request.status) {
                            CurfewStatus.PENDING -> "CHỜ DUYỆT"
                            CurfewStatus.APPROVED -> "ĐÃ DUYỆT"
                            CurfewStatus.REJECTED -> "TỪ CHỐI"
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = request.reason,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            if (request.requestType == CurfewRequestType.ABSENCE && request.startDate != null) {
                Text(
                    text = "Từ ngày: ${DateTimeUtils.formatIsoDateTime(request.startDate)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = "Giờ về: ${DateTimeUtils.formatIsoDateTime(request.expectedArrivalTime)}",
                style = MaterialTheme.typography.bodySmall
            )

            if (!request.note.isNullOrBlank()) {
                Text(
                    text = "Ghi chú: ${request.note}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Text(
                text = "Nộp lúc: ${DateTimeUtils.formatIsoDateTime(request.createdAt)}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
            )
        }
    }
}
