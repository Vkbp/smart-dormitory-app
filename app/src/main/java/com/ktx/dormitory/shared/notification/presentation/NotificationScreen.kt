package com.ktx.dormitory.shared.notification.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ktx.dormitory.navigation.Screen
import com.ktx.dormitory.core.util.DateTimeUtils
import com.ktx.dormitory.shared.notification.domain.model.Notification
import com.ktx.dormitory.shared.notification.domain.model.NotificationType
import com.ktx.dormitory.ui.components.EmptyView
import com.ktx.dormitory.ui.components.ErrorView
import com.ktx.dormitory.ui.components.LoadingView
import com.ktx.dormitory.shared.notification.presentation.components.NotificationDetailBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagingItems = uiState.pagingFlow.collectAsLazyPagingItems()

    if (uiState.selectedNotification != null) {
        NotificationDetailBottomSheet(
            notification = uiState.selectedNotification!!,
            onActionClick = { actionUrl ->
                handleNotificationNavigation(actionUrl, navController)
                viewModel.onEvent(NotificationUiEvent.SelectNotification(null))
            }
        ) { 
            viewModel.onEvent(NotificationUiEvent.SelectNotification(null)) 
            pagingItems.refresh() // Refresh list to update read status visually if needed
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Trung tâm thông báo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        pagingItems.refresh()
                        viewModel.refresh()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Tải lại")
                    }
                    // Kiểm tra xem trong danh sách hiện tại có tin nào chưa đọc không
                    val hasUnreadInList = remember(pagingItems.itemCount) {
                        (0 until pagingItems.itemCount).any { pagingItems[it]?.isRead == false }
                    }

                    if (uiState.unreadCount > 0 || hasUnreadInList) {
                        IconButton(onClick = { viewModel.onEvent(NotificationUiEvent.MarkAllAsRead) }) {
                            Icon(Icons.Default.DoneAll, contentDescription = "Đọc tất cả")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
        ) {
            FilterChips(
                selectedType = uiState.selectedType,
                onTypeSelected = { viewModel.onEvent(NotificationUiEvent.FilterByType(it)) }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    pagingItems.loadState.refresh is LoadState.Loading -> LoadingView()
                    pagingItems.loadState.refresh is LoadState.Error -> {
                        val e = pagingItems.loadState.refresh as LoadState.Error
                        ErrorView(
                            message = e.error.message ?: "Lỗi tải thông báo",
                            onRetry = { pagingItems.refresh() }
                        )
                    }
                    (pagingItems.itemCount == 0 && pagingItems.loadState.refresh !is LoadState.Loading) -> EmptyView(
                        message = if (uiState.selectedType == NotificationType.ALL) "Chưa có thông báo nào" 
                                  else "Không có thông báo ${uiState.selectedType.displayName.lowercase()}",
                        icon = Icons.Default.NotificationsNone
                    )
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                count = pagingItems.itemCount,
                                key = pagingItems.itemKey { it.id }
                            ) { index ->
                                val notification = pagingItems[index]
                                if (notification != null) {
                                    val isReadLocally = uiState.readIds.contains(notification.id) || uiState.isAllReadMarked
                                    NotificationCard(
                                        notification = notification.copy(isRead = notification.isRead || isReadLocally),
                                        onClick = { 
                                            if (!notification.isRead) {
                                                viewModel.onEvent(NotificationUiEvent.MarkAsRead(notification.id))
                                            }
                                            viewModel.onEvent(NotificationUiEvent.SelectNotification(notification))
                                        }
                                    )
                                }
                            }
                            
                            if (pagingItems.loadState.append is LoadState.Loading) {
                                item {
                                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(Modifier.size(24.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun handleNotificationNavigation(actionUrl: String?, navController: NavController) {
    if (actionUrl.isNullOrBlank()) return
    
    // Switch-case navigation based on actionUrl format
    when {
        actionUrl.contains("/student/bills") || actionUrl.contains("/payment") -> {
            navController.navigate(Screen.Payment.route)
        }
        actionUrl.contains("/student/room") || actionUrl.contains("/room-info") -> {
            navController.navigate(Screen.RoomInfo.route)
        }
        actionUrl.contains("/student/face") || actionUrl.contains("/face-status") -> {
            navController.navigate(Screen.FaceStatus.route)
        }
        actionUrl.contains("/student/checkout") || actionUrl.contains("/checkout") -> {
            navController.navigate(Screen.Checkout.route)
        }
        actionUrl.contains("/student/extension") || actionUrl.contains("/quick-extend") -> {
            navController.navigate(Screen.QuickExtend.route)
        }
        actionUrl.contains("/student/access") || actionUrl.contains("/access-history") -> {
            navController.navigate(Screen.AccessHistory.route)
        }
        actionUrl.contains("/student/maintenance") || actionUrl.contains("/maintenance") -> {
            navController.navigate(Screen.Maintenance.route)
        }
    }
}

@Composable
fun FilterChips(
    selectedType: NotificationType,
    onTypeSelected: (NotificationType) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(NotificationType.entries.toTypedArray()) { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = { Text(type.displayName) },
                leadingIcon = if (selectedType == type) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedType == type,
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun NotificationCard(
    notification: Notification,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) 
                MaterialTheme.colorScheme.surface 
            else 
                Color(0xFFE3F2FD) // Blue-ish for unread
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 0.dp else 2.dp),
        border = if (notification.isRead) 
            CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color.LightGray.copy(alpha = 0.2f), Color.Transparent)))
        else 
            BorderStroke(1.dp, Color(0xFF2196F3).copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Unread indicator vertical bar
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Icon based on type
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NotificationUtils.getTypeColor(notification.type).copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = NotificationUtils.getTypeIcon(notification.type),
                        contentDescription = null,
                        tint = NotificationUtils.getTypeColor(notification.type),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = notification.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (notification.isRead) FontWeight.Bold else FontWeight.Black,
                            color = if (notification.isRead) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (!notification.isRead) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2196F3))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = DateTimeUtils.formatRelativeTime(notification.createdAt),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 11.sp
                            )
                        }
                        
                        if (!notification.eventId.isNullOrBlank()) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = notification.eventId,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
