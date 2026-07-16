package com.ktx.dormitory.shared.notification.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ktx.dormitory.shared.notification.domain.model.IssueReport
import com.ktx.dormitory.ui.components.EmptyView
import com.ktx.dormitory.ui.components.ErrorView
import com.ktx.dormitory.ui.components.LoadingView
import com.ktx.dormitory.core.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueHistoryScreen(
    navController: NavController,
    viewModel: IssueHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử báo hỏng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(IssueHistoryUiEvent.Refresh) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Tải lại")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoading && uiState.issues.isEmpty() -> LoadingView()
                uiState.error != null && uiState.issues.isEmpty() -> ErrorView(
                    message = uiState.error ?: "Không thể tải lịch sử",
                    onRetry = { viewModel.onEvent(IssueHistoryUiEvent.Refresh) }
                )
                uiState.issues.isEmpty() -> EmptyView(
                    message = "Chưa có báo cáo nào",
                    icon = Icons.Default.History
                )
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.issues, key = { it.id }) { issue ->
                            IssueItem(issue)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IssueItem(issue: IssueReport) {
    val statusColor = when (issue.status.uppercase()) {
        "PENDING" -> Color(0xFFFF9800)
        "IN_PROGRESS" -> Color(0xFF2196F3)
        "RESOLVED" -> Color(0xFF4CAF50)
        "REJECTED" -> Color(0xFFF44336)
        else -> Color.Gray
    }

    val statusText = when (issue.status.uppercase()) {
        "PENDING" -> "Đang chờ"
        "IN_PROGRESS" -> "Đang xử lý"
        "RESOLVED" -> "Đã xong"
        "REJECTED" -> "Từ chối"
        else -> issue.status
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = DateTimeUtils.formatIsoDate(issue.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = issue.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
