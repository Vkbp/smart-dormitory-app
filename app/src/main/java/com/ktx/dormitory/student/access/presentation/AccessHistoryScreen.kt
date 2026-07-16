package com.ktx.dormitory.student.access.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ktx.dormitory.student.access.domain.model.UnifiedTimelineEvent
import com.ktx.dormitory.student.access.domain.model.UnifiedEventType
import com.ktx.dormitory.ui.components.EmptyView
import com.ktx.dormitory.ui.components.ErrorView
import com.ktx.dormitory.ui.components.LoadingView
import com.ktx.dormitory.core.util.DateTimeUtils

import com.ktx.dormitory.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AccessHistoryScreen(
    navController: NavController,
    viewModel: AccessViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.accessPagingFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử ra vào", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.CurfewRequest.route) }) {
                        Icon(Icons.Default.AddAlert, contentDescription = "Gửi yêu cầu vào trễ")
                    }
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
                pagingItems.loadState.refresh is androidx.paging.LoadState.Loading -> LoadingView()
                pagingItems.loadState.refresh is androidx.paging.LoadState.Error -> ErrorView(
                    message = "Không thể tải lịch sử",
                    onRetry = { pagingItems.refresh() }
                )
                pagingItems.itemCount == 0 -> EmptyHistoryState()
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            count = pagingItems.itemCount,
                            key = pagingItems.itemKey { it.id }
                        ) { index ->
                            val log = pagingItems[index]
                            if (log != null) {
                                UnifiedAccessLogItem(log) {
                                    navController.navigate(Screen.CurfewRequest.route)
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                        
                        if (pagingItems.loadState.append is androidx.paging.LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(32.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun DateHeader(formattedDate: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun UnifiedAccessLogItem(log: UnifiedTimelineEvent, onCurfewRequest: () -> Unit) {
    val statusColor = when (log.type) {
        UnifiedEventType.SUCCESS -> Color(0xFF4CAF50)
        UnifiedEventType.ACCESS_DENIED -> MaterialTheme.colorScheme.error
        UnifiedEventType.VERIFY_FAIL -> Color(0xFFFFC107)
        UnifiedEventType.UNKNOWN -> Color.Gray
    }

    val timeStr = (log.timestamp ?: "").substringAfter("T", "").substringBefore(".", "")
    val location = log.gateId ?: log.buildingId ?: "Cổng KTX"

    ListItem(
        headlineContent = {
            Text(
                text = location,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        supportingContent = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when {
                            log.method?.contains("FACE", true) == true -> Icons.Default.Face
                            log.method?.contains("QR", true) == true -> Icons.Default.QrCode
                            else -> Icons.Default.Nfc
                        },
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$timeStr • ${log.method ?: "Thẻ"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Render Rules based on type
                when (log.type) {
                    UnifiedEventType.SUCCESS -> {
                        Text("Vào ra thành công 🟢", color = Color(0xFF4CAF50), style = MaterialTheme.typography.labelSmall)
                    }
                    UnifiedEventType.ACCESS_DENIED -> {
                        Text(
                            text = "Từ chối: ${log.denialReason ?: "Không xác định"}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium
                        )
                        
                        // Call-to-Action for Curfew
                        if (log.denialReason?.contains("giới nghiêm") == true) {
                            Button(
                                onClick = onCurfewRequest,
                                modifier = Modifier.padding(top = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            ) {
                                Text("Gửi yêu cầu mở cửa khẩn cấp", fontSize = 10.sp)
                            }
                        }
                    }
                    UnifiedEventType.VERIFY_FAIL -> {
                        Text(
                            text = "AI không nhận diện được khuôn mặt. Vui lòng thử lại 🟡",
                            color = Color(0xFF8B6B00),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    else -> {}
                }
            }
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Icon(
                    imageVector = when (log.type) {
                        UnifiedEventType.SUCCESS -> Icons.Default.CheckCircle
                        UnifiedEventType.ACCESS_DENIED -> Icons.Default.Cancel
                        UnifiedEventType.VERIFY_FAIL -> Icons.Default.Warning
                        else -> Icons.Default.Help
                    },
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
fun EmptyHistoryState() {
    EmptyView(
        message = "Chưa có dữ liệu ra vào",
        icon = Icons.Default.History
    )
}
