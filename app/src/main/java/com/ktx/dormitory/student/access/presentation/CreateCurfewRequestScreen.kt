package com.ktx.dormitory.student.access.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ktx.dormitory.core.util.DateTimeUtils
import com.ktx.dormitory.student.access.domain.model.CurfewRequestType
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCurfewRequestScreen(
    navController: NavController,
    viewModel: AccessViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var requestType by remember { mutableStateOf(CurfewRequestType.LATE_RETURN) }
    var reason by remember { mutableStateOf("Đi làm thêm") }
    var note by remember { mutableStateOf("") }
    
    // Date/Time selection using Calendar (API 24 compatible)
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    val currentCalendar = remember { Calendar.getInstance() }
    var selectedYear by remember { mutableIntStateOf(currentCalendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(currentCalendar.get(Calendar.MONTH) + 1) }
    var selectedDay by remember { mutableIntStateOf(currentCalendar.get(Calendar.DAY_OF_MONTH)) }
    
    var arrivalYear by remember { mutableIntStateOf(currentCalendar.get(Calendar.YEAR)) }
    var arrivalMonth by remember { mutableIntStateOf(currentCalendar.get(Calendar.MONTH) + 1) }
    var arrivalDay by remember { mutableIntStateOf(currentCalendar.get(Calendar.DAY_OF_MONTH)) }
    var isSelectingArrivalDate by remember { mutableStateOf(false) }

    var selectedHour by remember { mutableIntStateOf(23) }
    var selectedMinute by remember { mutableIntStateOf(30) }

    val reasons = if (requestType == CurfewRequestType.LATE_RETURN) {
        listOf("Đi làm thêm", "Thực tập", "Xe hỏng", "Việc gia đình", "Lý do khác")
    } else {
        listOf("Về quê", "Đi du lịch", "Việc gia đình", "Lý do khác")
    }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.uiEffect) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is AccessUiEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                AccessUiEffect.NavigateBack -> navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tạo đơn mới", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Loại yêu cầu", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = requestType == CurfewRequestType.LATE_RETURN,
                    onClick = { 
                        requestType = CurfewRequestType.LATE_RETURN 
                        reason = "Đi làm thêm"
                    },
                    label = { Text("Xin về trễ") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = requestType == CurfewRequestType.ABSENCE,
                    onClick = { 
                        requestType = CurfewRequestType.ABSENCE 
                        reason = "Về quê"
                    },
                    label = { Text("Vắng mặt") },
                    modifier = Modifier.weight(1f)
                )
            }

            Text(text = "Thông tin chi tiết", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            // Reason Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = reason,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Lý do") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    reasons.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                reason = item
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Start Date (Only for ABSENCE)
            if (requestType == CurfewRequestType.ABSENCE) {
                OutlinedTextField(
                    value = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth, selectedYear),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ngày đi") },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Chọn ngày")
                        }
                    }
                )
            }

            // Arrival Time
            OutlinedTextField(
                value = if (requestType == CurfewRequestType.LATE_RETURN) 
                    String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                    else String.format(Locale.getDefault(), "%02d/%02d/%d %02d:%02d", arrivalDay, arrivalMonth, arrivalYear, selectedHour, selectedMinute),
                onValueChange = {},
                readOnly = true,
                label = { Text(if (requestType == CurfewRequestType.LATE_RETURN) "Giờ về dự kiến" else "Ngày về & Giờ về dự kiến") },
                leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { 
                        if (requestType == CurfewRequestType.ABSENCE) {
                            isSelectingArrivalDate = true
                            showDatePicker = true 
                        } else {
                            showTimePicker = true 
                        }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Chọn giờ")
                    }
                }
            )

            // Note
            val isReasonOther = reason == "Lý do khác"
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(if (isReasonOther) "Giải trình chi tiết (Bắt buộc)" else "Ghi chú thêm") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                minLines = 3,
                isError = isReasonOther && note.isBlank()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // Requirement: LocalDateTime ISO-8601
                    val arrivalIso = if (requestType == CurfewRequestType.LATE_RETURN) {
                        val now = Calendar.getInstance()
                        DateTimeUtils.createIsoDateTime(
                            year = now.get(Calendar.YEAR),
                            month = now.get(Calendar.MONTH) + 1,
                            day = now.get(Calendar.DAY_OF_MONTH),
                            hour = selectedHour,
                            minute = selectedMinute
                        )
                    } else {
                        DateTimeUtils.createIsoDateTime(arrivalYear, arrivalMonth, arrivalDay, selectedHour, selectedMinute)
                    }
                    
                    val startDateIso = if (requestType == CurfewRequestType.ABSENCE) {
                        DateTimeUtils.createIsoDateTime(selectedYear, selectedMonth, selectedDay, 0, 0)
                    } else null

                    viewModel.onEvent(
                        AccessUiEvent.SubmitCurfewRequest(
                            requestType = requestType,
                            reason = reason,
                            startDate = startDateIso,
                            expectedArrivalTime = arrivalIso,
                            note = note
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !uiState.isLoading && !(isReasonOther && note.isBlank()),
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("GỬI YÊU CẦU", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { 
                    showDatePicker = false
                    isSelectingArrivalDate = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val cal = Calendar.getInstance().apply { timeInMillis = millis }
                            if (isSelectingArrivalDate) {
                                arrivalYear = cal.get(Calendar.YEAR)
                                arrivalMonth = cal.get(Calendar.MONTH) + 1
                                arrivalDay = cal.get(Calendar.DAY_OF_MONTH)
                            } else {
                                selectedYear = cal.get(Calendar.YEAR)
                                selectedMonth = cal.get(Calendar.MONTH) + 1
                                selectedDay = cal.get(Calendar.DAY_OF_MONTH)
                            }
                        }
                        if (isSelectingArrivalDate) {
                            showDatePicker = false
                            isSelectingArrivalDate = false
                            showTimePicker = true // Automatically show time picker after picking arrival date
                        } else {
                            showDatePicker = false
                        }
                    }) { Text("OK") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            val timePickerState = rememberTimePickerState(initialHour = selectedHour, initialMinute = selectedMinute)
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        selectedHour = timePickerState.hour
                        selectedMinute = timePickerState.minute
                        showTimePicker = false
                    }) { Text("OK") }
                },
                text = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        TimePicker(state = timePickerState)
                    }
                }
            )
        }
    }
}
