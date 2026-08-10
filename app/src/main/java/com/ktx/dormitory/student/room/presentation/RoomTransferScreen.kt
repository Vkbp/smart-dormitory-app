package com.ktx.dormitory.student.room.presentation

import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.ktx.dormitory.student.room.domain.model.RoomInfo
import com.ktx.dormitory.student.room.domain.model.RoomTransferHistory
import com.ktx.dormitory.navigation.components.EmptyView
import com.ktx.dormitory.navigation.components.LoadingView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomTransferScreen(
    navController: NavController,
    viewModel: RoomTransferViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showRoomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yêu cầu Đổi phòng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = state.selectedTab) {
                Tab(
                    selected = state.selectedTab == 0,
                    onClick = { viewModel.onEvent(RoomTransferUiEvent.TabSelected(0)) },
                    text = { Text("Gửi yêu cầu") }
                )
                Tab(
                    selected = state.selectedTab == 1,
                    onClick = { viewModel.onEvent(RoomTransferUiEvent.TabSelected(1)) },
                    text = { Text("Lịch sử") }
                )
            }

            when (state.selectedTab) {
                0 -> RequestForm(state, viewModel) { showRoomSheet = true }
                1 -> HistoryList(state, navController)
            }
        }
    }

    if (showRoomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showRoomSheet = false },
            sheetState = sheetState
        ) {
            RoomSelectionContent(
                groupedRooms = state.groupedAvailableRooms,
                onRoomSelected = { id, code ->
                    viewModel.onEvent(RoomTransferUiEvent.RoomSelected(id, code))
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showRoomSheet = false
                    }
                }
            )
        }
    }

    if (state.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(RoomTransferUiEvent.ClearError) },
            title = { Text("Thông báo") },
            text = { Text(state.error!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(RoomTransferUiEvent.ClearError) }) {
                    Text("Đóng")
                }
            }
        )
    }
}

@Composable
fun RequestForm(
    state: RoomTransferUiState,
    viewModel: RoomTransferViewModel,
    onShowRooms: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Hãy mô tả chi tiết lý do bạn muốn đổi phòng để Admin có thể xem xét tốt nhất.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.reason,
            onValueChange = { 
                viewModel.onEvent(RoomTransferUiEvent.ReasonChanged(it)) 
            },
            label = { Text("Lý do xin đổi phòng") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ví dụ: Phòng hiện tại quá ồn ào, thiết bị trong phòng hỏng hóc nhiều...") },
            minLines = 4,
            maxLines = 6,
            isError = state.reasonError != null,
            supportingText = {
                state.reasonError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                } ?: run {
                    Text("Thông tin bắt buộc", style = MaterialTheme.typography.labelSmall)
                }
            },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Phòng mong muốn (Tùy chọn)",
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        if (state.targetRoomCode.isEmpty()) {
            OutlinedCard(
                onClick = onShowRooms,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Bấm để chọn phòng trống",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Phòng ${state.targetRoomCode}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Phòng đã chọn",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                    IconButton(onClick = { viewModel.onEvent(RoomTransferUiEvent.RoomSelected("", "")) }) {
                        Icon(Icons.Default.Close, contentDescription = "Bỏ chọn")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { viewModel.onEvent(RoomTransferUiEvent.SubmitRequest) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = !state.isSubmitting && (state.reason.length >= 10),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("GỬI YÊU CẦU ĐỔI PHÒNG", fontWeight = FontWeight.ExtraBold)
            }
        }
        
        if (state.reason.isNotEmpty() && state.reason.length < 10) {
            Text(
                "Lý do cần dài hơn 10 ký tự",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun RoomSelectionContent(
    groupedRooms: Map<String, List<RoomInfo>>,
    onRoomSelected: (String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
        Text(
            "Chọn phòng muốn chuyển đến",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold
        )

        if (groupedRooms.isEmpty()) {
            EmptyView(message = "Không có phòng trống nào phù hợp với bạn")
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                groupedRooms.forEach { (building, rooms) ->
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Tòa $building",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    items(rooms) { room ->
                        ListItem(
                            headlineContent = { Text("Phòng ${room.roomCode}") },
                            supportingContent = { 
                                Text("Tầng ${room.floor} - Còn ${room.availableBeds ?: 0} giường trống") 
                            },
                            leadingContent = {
                                Icon(Icons.Default.MeetingRoom, contentDescription = null)
                            },
                            modifier = Modifier.clickable {
                                onRoomSelected(room.roomId ?: "", room.roomCode ?: "")
                            }
                        )
                    }
                }

                item {
                    ListItem(
                        headlineContent = { Text("Bỏ chọn / Để Admin sắp xếp") },
                        leadingContent = {
                            Icon(Icons.Default.History, contentDescription = null)
                        },
                        modifier = Modifier.clickable {
                            onRoomSelected("", "")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryList(state: RoomTransferUiState, navController: NavController) {
    if (state.isLoading) {
        LoadingView()
    } else if (state.history.isEmpty()) {
        EmptyView(message = "Chưa có yêu cầu nào được gửi", icon = Icons.Default.History)
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.history) { item ->
                HistoryItem(item) {
                    navController.navigate(Screen.RoomTransferDetail.createRoute(item.id))
                }
            }
        }
    }
}

@Composable
fun HistoryItem(item: RoomTransferHistory, onClick: () -> Unit) {
    val statusColor = when (item.status) {
        "APPROVED" -> Color(0xFF4CAF50)
        "REJECTED" -> MaterialTheme.colorScheme.error
        else -> Color(0xFFFF9800) // PENDING
    }

    val statusText = when (item.status) {
        "APPROVED" -> "Đã duyệt"
        "REJECTED" -> "Từ chối"
        else -> "Đang chờ"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DateTimeUtils.formatIsoDateTime(item.createdAt),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Lý do: ${item.reason}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            if (!item.targetRoomName.isNullOrBlank()) {
                Text(
                    text = "Phòng mong muốn: ${item.targetRoomName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!item.adminNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Phản hồi từ Admin:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = item.adminNote,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
