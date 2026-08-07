package com.ktx.dormitory.admin.smartaccess.presentation.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ktx.dormitory.core.util.DataFormatter
import com.ktx.dormitory.core.util.DateTimeUtils
import com.ktx.dormitory.navigation.Screen
import com.ktx.dormitory.navigation.components.EmptyView
import com.ktx.dormitory.navigation.components.ErrorView
import com.ktx.dormitory.navigation.components.LoadingView
import com.ktx.dormitory.student.access.domain.model.AccessLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAccessHistoryScreen(
    navController: NavController,
    viewModel: AdminAccessHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagingItems = uiState.pagingFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử hoạt động Admin", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { pagingItems.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Tải lại")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                pagingItems.loadState.refresh is LoadState.Loading -> LoadingView()
                pagingItems.loadState.refresh is LoadState.Error -> ErrorView(
                    message = "Không thể tải lịch sử",
                    onRetry = { pagingItems.refresh() }
                )
                pagingItems.itemCount == 0 && pagingItems.loadState.refresh is LoadState.NotLoading -> {
                    EmptyHistoryState()
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            count = pagingItems.itemCount,
                            key = pagingItems.itemKey { it.id }
                        ) { index ->
                            val log = pagingItems[index]
                            if (log != null) {
                                AdminAccessLogItem(
                                    log = log,
                                    onClick = {
                                        navController.navigate(Screen.AdminAccessDetail.createRoute(log.id))
                                        navController.currentBackStackEntry?.savedStateHandle?.set("log", log)
                                    }
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }

                        if (pagingItems.loadState.append is LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAccessLogItem(
    log: AccessLog,
    onClick: () -> Unit
) {
    val statusColor = if (log.decision == "GRANTED") Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
    val timeStr = DateTimeUtils.formatRelativeTime(log.eventTimestamp)
    val location = log.gateId ?: log.buildingId ?: "Hệ thống"

    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = {
            Text(
                text = "Mở cửa: $location",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        supportingContent = {
            Column {
                Text(
                    text = "Thời gian: $timeStr",
                    style = MaterialTheme.typography.bodySmall
                )
                if (log.studentId != null) {
                    Text(
                        text = "Sinh viên: ${DataFormatter.formatId(log.studentId)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = "Phương thức: ${DataFormatter.formatAccessMethod(log.method)}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (log.denialReason != null) {
                    Text(
                        text = "Ghi chú: ${log.denialReason}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        trailingContent = {
            Icon(
                imageVector = if (log.decision == "GRANTED") Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(24.dp)
            )
        }
    )
}

@Composable
fun EmptyHistoryState() {
    EmptyView(
        message = "Chưa có hoạt động nào được ghi lại",
        icon = Icons.Default.History
    )
}
