package com.ktx.dormitory.student.checkout.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ktx.dormitory.core.util.DateTimeUtils
import com.ktx.dormitory.student.checkout.domain.model.CheckoutResponse
import com.ktx.dormitory.ui.components.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showForm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yêu cầu trả phòng sớm", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (!showForm && !uiState.hasPendingRequest && !uiState.hasUnpaidBills) {
                ExtendedFloatingActionButton(
                    onClick = { showForm = true },
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Tạo yêu cầu") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (showForm) {
                CheckoutForm(
                    isLoading = uiState.isLoading,
                    onDismiss = { showForm = false },
                    onSubmit = { date, reason, bankAcc, bankName ->
                        viewModel.onEvent(CheckoutUiEvent.Submit(date, reason, bankAcc, bankName))
                    }
                )
            } else {
                Column {
                    if (uiState.hasUnpaidBills) {
                        Surface(
                            color = Color(0xFFFFF3E0), // Light orange pastel
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, null, tint = Color(0xFFE65100))
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Bạn đang có hóa đơn chưa thanh toán. Vui lòng thanh toán toàn bộ nợ trước khi nộp đơn trả phòng.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFE65100),
                                        fontWeight = FontWeight.Medium
                                    )
                                    TextButton(
                                        onClick = { navController.navigate(com.ktx.dormitory.navigation.Screen.Payment.route) },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("THANH TOÁN NGAY", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.hasPendingRequest) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Đơn xin trả phòng của bạn đang được Ban Quản Lý chờ xử lý. Vui lòng đợi.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                    CheckoutHistoryList(
                        history = uiState.history,
                        isLoading = uiState.isLoading
                    )
                }
            }

            LaunchedEffect(uiState.submitSuccess) {
                if (uiState.submitSuccess) {
                    showForm = false
                    viewModel.onEvent(CheckoutUiEvent.ClearStatus)
                }
            }

            // Xử lý lỗi nợ tiền từ Backend (Cross-navigation Flow)
            if (uiState.debtErrorMessage != null) {
                AlertDialog(
                    onDismissRequest = { viewModel.onEvent(CheckoutUiEvent.ClearStatus) },
                    icon = { Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    title = { Text("Cảnh báo nợ phí", color = MaterialTheme.colorScheme.error) },
                    text = { Text(uiState.debtErrorMessage!!, style = MaterialTheme.typography.bodyMedium) },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.onEvent(CheckoutUiEvent.ClearStatus)
                                navController.navigate(com.ktx.dormitory.navigation.Screen.Payment.route)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Đến trang Hóa đơn")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.onEvent(CheckoutUiEvent.ClearStatus) }) {
                            Text("Đóng")
                        }
                    }
                )
            }

            if (uiState.error != null) {
                SnackbarHost(hostState = remember { SnackbarHostState() }.apply {
                    LaunchedEffect(uiState.error) {
                        showSnackbar(uiState.error!!)
                    }
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutForm(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String) -> Unit
) {
    var date by remember { mutableStateOf("") }
    var isoDate by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var bankAccount by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf<String?>(null) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        date = DateTimeUtils.formatDate(it)
                        isoDate = DateTimeUtils.formatToIso(it)
                        dateError = null
                    }
                    showDatePicker = false
                }) { Text("Chọn") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Hủy") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Điền thông tin trả phòng", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = date,
            onValueChange = { },
            label = { Text("Ngày dự định trả") },
            placeholder = { Text("Bấm để chọn ngày") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Chọn ngày")
                }
            },
            isError = dateError != null,
            supportingText = { dateError?.let { Text(it) } },
            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                showDatePicker = true
                            }
                        }
                    }
                }
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = reason,
            onValueChange = { reason = it },
            label = { Text("Lý do trả phòng") },
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = bankAccount,
            onValueChange = { bankAccount = it },
            label = { Text("Số tài khoản nhận lại tiền cọc") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = bankName,
            onValueChange = { bankName = it },
            label = { Text("Tên ngân hàng") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ví dụ: Vietcombank, MB Bank...") }
        )

        Spacer(Modifier.height(8.dp))

        Surface(
            color = Color(0xFFE3F2FD), // Light blue pastel
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Vui lòng cung cấp chính xác Số Tài Khoản Ngân hàng. Ban quản lý KTX sẽ chốt công nợ và chuyển hồ sơ sang Phòng Tài vụ của Trường để giải ngân tiền thừa (nếu có).",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1976D2)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { 
                if (isoDate.isNotBlank()) {
                    onSubmit(isoDate, reason, bankAccount, bankName)
                } else {
                    dateError = "Vui lòng chọn ngày dự định trả"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && date.isNotBlank() && reason.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Gửi yêu cầu")
            }
        }

        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
            Text("Hủy")
        }
    }
}

@Composable
fun CheckoutHistoryList(history: List<CheckoutResponse>, isLoading: Boolean) {
    if (isLoading && history.isEmpty()) {
        LoadingView()
    } else if (history.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Bạn chưa có yêu cầu trả phòng nào")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(history) { item ->
                CheckoutHistoryItem(item)
            }
        }
    }
}

@Composable
fun CheckoutHistoryItem(item: CheckoutResponse) {
    val statusColor = when (item.status.uppercase()) {
        "PENDING" -> MaterialTheme.colorScheme.primary
        "APPROVED" -> Color(0xFF4CAF50)
        "REJECTED" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Đơn trả phòng", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = when(item.status.uppercase()) {
                            "PENDING" -> "Chờ xử lý"
                            "APPROVED" -> "Hoàn tất Checkout"
                            "REJECTED" -> "Bị từ chối"
                            else -> item.status
                        },
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MeetingRoom, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text("Phòng: ${item.roomCode ?: "N/A"} - Giường: ${item.bedCode ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
            }
            
            Spacer(Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text("Ngày dự định: ${DateTimeUtils.formatIsoDateTime(item.intendedCheckoutDate)}", style = MaterialTheme.typography.bodyMedium)
            }
            
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            Spacer(Modifier.height(12.dp))

            val statusNote = when (item.status.uppercase()) {
                "PENDING" -> "Đang chờ BQL KTX kiểm tra tài sản và chốt công nợ."
                "APPROVED" -> "Đã thu hồi giường. Hồ sơ đang được Kế toán Trường xử lý giải ngân (nếu có)."
                "REJECTED" -> "Yêu cầu không được chấp thuận."
                else -> ""
            }

            if (statusNote.isNotEmpty()) {
                Text(
                    text = statusNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal
                )
            }
            
            if (item.status.uppercase() == "REJECTED" && !item.rejectReason.isNullOrEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Lý do từ chối: ${item.rejectReason}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
