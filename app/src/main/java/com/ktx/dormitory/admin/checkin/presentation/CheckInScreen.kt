package com.ktx.dormitory.admin.checkin.presentation

import android.annotation.SuppressLint
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.ktx.dormitory.admin.checkin.presentation.components.QrScannerView
import com.ktx.dormitory.admin.checkin.util.QrParser
import com.ktx.dormitory.admin.common.domain.model.CheckInStudent
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
    var showSuccessDialog by remember { mutableStateOf(false) }
    var selectedStudentId by remember { mutableStateOf<UUID?>(null) }
    var lastScannedCccd by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Tự động yêu cầu quyền khi vào màn hình nếu chưa có
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Logic: Khi có studentInfo thì mở Bottom Sheet
    LaunchedEffect(uiState.studentInfo) {
        if (uiState.studentInfo != null) {
            showBottomSheet = true
        }
    }

    // Logic: Khi có successMessage thì đóng Bottom Sheet và hiện Success Dialog
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            showBottomSheet = false
            showSuccessDialog = true
        }
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CheckInUiEffect.ShowToast -> {
                    android.widget.Toast.makeText(context, effect.message, android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quét mã nhận phòng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // PHẦN 1: CAMERA SCANNER (CHIẾM 60% CHIỀU CAO)
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxWidth()
                        .background(Color.Black)
                ) {
                    if (hasCameraPermission) {
                        QrScannerView(
                            onQrDetected = { qrData ->
                                val cccd = QrParser.parseCccdQr(qrData)
                                // Tự động quét: Chỉ search nếu CCCD hợp lệ và khác với mã vừa quét xong
                                if (cccd != null && cccd != lastScannedCccd && !uiState.isLoading && !showBottomSheet) {
                                    lastScannedCccd = cccd
                                    viewModel.onEvent(CheckInUiEvent.SearchStudent(cccd))
                                    // Rung nhẹ để báo hiệu đã quét được (Haptic feedback)
                                    val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as android.os.Vibrator
                                    @SuppressLint("MissingPermission")
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        vibrator.vibrate(android.os.VibrationEffect.createOneShot(100, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
                                    } else {
                                        @Suppress("DEPRECATION")
                                        vibrator.vibrate(100)
                                    }
                                }
                            }
                        )
                        
                        // Scanning Overlay (Optional: Frame, Text hint)
                        ScannerOverlay()
                        
                        Text(
                            text = "Đưa mã QR CCCD vào khung hình",
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 14.sp
                        )
                    } else {
                        // HIỂN THỊ KHI CHƯA CÓ QUYỀN CAMERA
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Ứng dụng cần quyền Camera để quét mã QR",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("CẤP QUYỀN CAMERA")
                            }
                        }
                    }
                }

                // PHẦN 2: NHẬP TAY (CHIẾM 40% CHIỀU CAO)
                Surface(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxWidth(),
                    tonalElevation = 2.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Tìm kiếm thủ công",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        OutlinedTextField(
                            value = cccdText,
                            onValueChange = { cccdText = it },
                            label = { Text("Nhập số CCCD") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { 
                                    if (cccdText.length >= 9) {
                                        viewModel.onEvent(CheckInUiEvent.SearchStudent(cccdText))
                                    }
                                }) {
                                    Icon(Icons.Default.Search, contentDescription = "Tìm kiếm")
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (uiState.errorMessage != null) {
                            Text(
                                text = uiState.errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }

                        Button(
                            onClick = { 
                                if (cccdText.length >= 9) {
                                    viewModel.onEvent(CheckInUiEvent.SearchStudent(cccdText))
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = cccdText.length >= 9 && !uiState.isLoading
                        ) {
                            Text("TÌM KIẾM SINH VIÊN", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // LOADING OVERLAY
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }

        // BOTTOM SHEET HIỂN THỊ KẾT QUẢ
        if (showBottomSheet && uiState.studentInfo != null) {
            ModalBottomSheet(
                onDismissRequest = { 
                    showBottomSheet = false
                    lastScannedCccd = "" // Reset để có thể quét lại cùng mã đó sau khi đóng sheet
                    viewModel.onEvent(CheckInUiEvent.ClearStatus) 
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                StudentCheckInBottomSheetContent(
                    student = uiState.studentInfo!!,
                    onConfirm = { viewModel.onEvent(CheckInUiEvent.ConfirmCheckIn(uiState.studentInfo!!.assignmentId)) },
                    onAssignRfid = {
                        selectedStudentId = uiState.studentInfo!!.studentId
                        showRfidDialog = true
                    }
                )
            }
        }

        // RFID DIALOG
        if (showRfidDialog) {
            RfidAssignmentDialog(
                onDismiss = { showRfidDialog = false },
                onConfirm = { rfidCode: String ->
                    // Sử dụng ID từ studentInfo nếu selectedStudentId vì lý do nào đó bị null
                    val idToAssign = selectedStudentId ?: uiState.studentInfo?.studentId
                    if (idToAssign != null) {
                        viewModel.onEvent(CheckInUiEvent.AssignRfid(idToAssign, rfidCode))
                    } else {
                        android.widget.Toast.makeText(context, "Không xác định được sinh viên để gán thẻ", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    showRfidDialog = false
                }
            )
        }

        // SUCCESS DIALOG
        if (showSuccessDialog) {
            SuccessCheckInDialog(
                message = uiState.successMessage ?: "Làm thủ tục thành công!",
                onDismiss = {
                    showSuccessDialog = false
                    viewModel.onEvent(CheckInUiEvent.ClearStatus)
                    cccdText = ""
                }
            )
        }
    }
}

@Composable
fun StudentCheckInBottomSheetContent(
    student: CheckInStudent,
    onConfirm: () -> Unit,
    onAssignRfid: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "KẾT QUẢ TÌM KIẾM",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        // ẢNH CHÂN DUNG TO RÕ (YÊU CẦU ĐẶC BIỆT)
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(student.portraitUrl)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
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
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
            },
            contentDescription = "Portrait",
            modifier = Modifier
                .size(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CheckInDetailRow("Họ và tên", student.studentName)
            CheckInDetailRow("Mã sinh viên", student.studentCode)
            CheckInDetailRow("Số CCCD", student.cccd)
            CheckInDetailRow("Giới tính", if (student.gender == "MALE") "Nam" else "Nữ")
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)
            
            // THÔNG TIN PHÒNG IN ĐẬM
            CheckInDetailRowBold("Tòa nhà", student.buildingName ?: "---")
            CheckInDetailRowBold("Phòng", student.roomName ?: "---")
            CheckInDetailRowBold("Vị trí giường", student.bedName ?: "---")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onAssignRfid,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = true // Mở khóa nút này (User yêu cầu)
            ) {
                Text("GÁN THẺ RFID", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier.weight(1.4f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("XÁC NHẬN & GIAO KHÓA", fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun ScannerOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val linePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "linePosition"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                drawContent()
                // Vẽ lớp phủ tối xung quanh khung quét
                val frameSize = size.width * 0.7f
                val left = (size.width - frameSize) / 2
                val top = (size.height - frameSize) / 2
                
                // Đục lỗ giữa
                clipRect(
                    left = left,
                    top = top,
                    right = left + frameSize,
                    bottom = top + frameSize,
                    clipOp = androidx.compose.ui.graphics.ClipOp.Difference
                ) {
                    drawRect(Color.Black.copy(alpha = 0.5f))
                }
            }
    ) {
        // Khung góc
        Box(
            modifier = Modifier
                .size(width = 250.dp, height = 250.dp)
                .align(Alignment.Center)
                .border(BorderStroke(2.dp, Color.White.copy(alpha = 0.8f)), RoundedCornerShape(4.dp))
        ) {
            // Đường quét chạy lên xuống
            Canvas(modifier = Modifier.fillMaxSize()) {
                val lineY = this.size.height * linePosition
                drawLine(
                    color = Color.Green,
                    start = Offset(0f, lineY),
                    end = Offset(this.size.width, lineY),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
    }
}

@Composable
fun SuccessCheckInDialog(message: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(72.dp)
                )
                Text(
                    "Thành công!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    message,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ĐÃ HIỂU", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CheckInDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
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
fun CheckInDetailRowBold(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
    }
}
