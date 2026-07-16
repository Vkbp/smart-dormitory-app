package com.ktx.dormitory.admin.checkin.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.request.CachePolicy
import com.ktx.dormitory.admin.common.domain.model.CheckInStudent
import com.ktx.dormitory.ui.components.LoadingView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    navController: NavController,
    viewModel: CheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var cccdText by remember { mutableStateOf("") }
    var showRfidDialog by remember { mutableStateOf(false) }
    var selectedStudentId by remember { mutableStateOf<UUID?>(null) }
    val context = LocalContext.current

    // Tự động tìm kiếm khi nhập đủ 12 số (định dạng CCCD mới) hoặc 9 số (định dạng cũ)
    LaunchedEffect(cccdText) {
        val cleanText = cccdText.trim()
        if (cleanText.length == 9 || cleanText.length == 12) {
            viewModel.onEvent(CheckInUiEvent.SearchStudent(cleanText))
        }
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CheckInUiEffect.ShowToast -> {
                    android.widget.Toast.makeText(context, effect.message, android.widget.Toast.LENGTH_LONG).show()
                    // Sau khi thành công, có thể xóa text CCCD để quét người tiếp theo
                    cccdText = ""
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Làm thủ tục nhận phòng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Ô nhập CCCD & Nút Tìm
                OutlinedTextField(
                    value = cccdText,
                    onValueChange = { cccdText = it },
                    label = { Text("Nhập số CCCD (9 hoặc 12 số)") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.onEvent(CheckInUiEvent.SearchStudent(cccdText)) }) {
                            Icon(Icons.Default.Search, contentDescription = "Tìm kiếm")
                        }
                    },
                    singleLine = true
                )

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                if (uiState.successMessage != null) {
                    Text(
                        text = uiState.successMessage!!,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                val student = uiState.studentInfo
                if (student != null) {
                    StudentCheckInInfo(
                        student = student,
                        onConfirm = { viewModel.onEvent(CheckInUiEvent.ConfirmCheckIn(student.assignmentId)) },
                        onAssignRfid = {
                            selectedStudentId = student.studentId
                            showRfidDialog = true
                        }
                    )
                }
            }

            if (showRfidDialog && selectedStudentId != null) {
                RfidAssignmentDialog(
                    onDismiss = { showRfidDialog = false },
                    onConfirm = { rfidCode ->
                        viewModel.onEvent(CheckInUiEvent.AssignRfid(selectedStudentId!!, rfidCode))
                        showRfidDialog = false
                    }
                )
            }

            // TÁCH BIỆT: Chỉ hiển thị LoadingView khi đang gọi API lấy JSON
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun StudentCheckInInfo(
    student: CheckInStudent,
    onConfirm: () -> Unit,
    onAssignRfid: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Thông tin sinh viên",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // TỐI ƯU ẢNH: Sử dụng SubcomposeAsyncImage để Lazy Load và Cache
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(student.portraitUrl)
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED) // Bật Disk Cache
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                loading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                },
                error = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                contentDescription = "Portrait",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CheckInDetailRow("Họ tên", student.studentName)
                CheckInDetailRow("MSSV", student.studentCode)
                CheckInDetailRow("CCCD", student.cccd)
                CheckInDetailRow("Giới tính", if (student.gender == "MALE") "Nam" else "Nữ")
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                CheckInDetailRow("Tòa", student.buildingName ?: "---")
                CheckInDetailRow("Phòng", student.roomName ?: "---")
                CheckInDetailRow("Giường", student.bedName ?: "---")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onAssignRfid,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("CẤP THẺ RFID", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("NHẬN PHÒNG", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RfidAssignmentDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var rfidCode by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cấp thẻ RFID") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Vui lòng quẹt thẻ vào đầu đọc hoặc nhập mã Hex thủ công.")
                OutlinedTextField(
                    value = rfidCode,
                    onValueChange = { rfidCode = it },
                    label = { Text("Mã thẻ (Hex)") },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (rfidCode.isNotBlank()) onConfirm(rfidCode) },
                enabled = rfidCode.isNotBlank()
            ) { Text("Xác nhận") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
fun CheckInDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
