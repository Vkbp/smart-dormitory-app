package com.ktx.dormitory.shared.notification.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ktx.dormitory.shared.notification.domain.model.Notification
import com.ktx.dormitory.ui.components.EmptyView
import com.ktx.dormitory.ui.components.ErrorView
import com.ktx.dormitory.ui.components.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông báo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.onEvent(NotificationUiEvent.MarkAllAsRead) }) {
                        Text("Đánh dấu tất cả đã đọc")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoading && uiState.notifications.isEmpty() -> LoadingView()
                uiState.error != null && uiState.notifications.isEmpty() -> ErrorView(
                    message = uiState.error,
                    onRetry = { viewModel.onEvent(NotificationUiEvent.Refresh) }
                )
                uiState.notifications.isEmpty() -> EmptyView(
                    message = "Chưa có thông báo nào",
                    icon = Icons.Default.NotificationsNone
                )
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.notifications, key = { it.id }) { notification ->
                            NotificationItem(notification) {
                                viewModel.onEvent(NotificationUiEvent.MarkAsRead(notification.id))
                            }
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification, onClick: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(
                text = notification.title ?: "Thông báo",
                fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold
            )
        },
        supportingContent = {
            Text(text = notification.message ?: "")
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (notification.isRead) Color.Transparent else MaterialTheme.colorScheme.primary)
            )
        },
        modifier = Modifier.clickable { onClick() }
    )
}
