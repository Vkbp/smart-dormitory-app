package com.ktx.dormitory.student.room.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Send
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
import com.ktx.dormitory.student.room.domain.model.RoomInfo
import com.ktx.dormitory.student.room.domain.model.RoomTransferHistory
import com.ktx.dormitory.ui.components.EmptyView
import com.ktx.dormitory.ui.components.LoadingView
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
                1 -> HistoryList(state)
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = state.reason,
            onValueChange = { 
                viewModel.onEvent(RoomTransferUiEvent.ReasonChanged(it)) 
            },
            label = { Text("Lý do xin đổi phòng (Bắt buộc)") },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            placeholder = { Text("Ví dụ: Phòng hiện tại quá ồn ào và hay bị dột nước") },
            maxLines = 5,
            isError = state.reasonError != null,
            supportingText = {
                if (state.reasonError != null) {
                    Text(state.reasonError!!, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.targetRoomCode,
            onValueChange = { },
            label = { Text("Phòng mong muốn (Không bắt buộc)") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowRooms() },
            placeholder = { Text("Bấm để chọn phòng trống") },
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Chọn từ danh sách")
            },
            enabled = false, // Vẫn clickable do Modifier.clickable phía trên
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.onEvent(RoomTransferUiEvent.SubmitRequest) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !state.isSubmitting && state.reason.isNotBlank()
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Icon(Icons.Default.Send, contentDescription = "Gửi")
                Spacer(modifier = Modifier.width(8.dp))
                Text("GỬI YÊU CẦU", fontWeight = FontWeight.Bold)
            }
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
fun HistoryList(state: RoomTransferUiState) {
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
                HistoryItem(item)
            }
        }
    }
}

@Composable
fun HistoryItem(item: RoomTransferHistory) {
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
        modifier = Modifier.fillMaxWidth(),
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
