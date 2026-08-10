package com.ktx.dormitory.admin.smartaccess.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ktx.dormitory.admin.common.data.dto.response.BuildingResponseDto
import com.ktx.dormitory.admin.common.data.dto.response.GateResponseDto
import com.ktx.dormitory.navigation.Screen
import com.ktx.dormitory.shared.profile.data.dto.response.StudentResponse
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartAccessScreen(
    navController: NavController,
    viewModel: SmartAccessViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showUnlockDialog by remember { mutableStateOf(false) }
    var showEmergencyDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Smart Access Control", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.AdminAccessHistory.route) }) {
                        Icon(Icons.Default.History, contentDescription = "Lịch sử hoạt động")
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
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    )
                )
        ) {
            // Header Status Section
            StatusOverview(isLoading = uiState.isLoading)

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Main Operations Section
                SectionTitle(title = "Hành động điều khiển", icon = Icons.Default.SettingsRemote)
                
                OperationCard(
                    title = "Mở khóa cửa từ xa",
                    description = "Cấp quyền truy cập tạm thời cho sinh viên hoặc mở cổng KTX từ xa.",
                    icon = Icons.Default.DoorBack,
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    onClick = { showUnlockDialog = true }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Safety & Danger Zone Section
                SectionTitle(title = "Vùng an toàn & Khẩn cấp", icon = Icons.Default.HealthAndSafety, color = Color(0xFFD32F2F))
                
                OperationCard(
                    title = "Kích hoạt KHẨN CẤP",
                    description = "MỞ TOÀN BỘ CỬA trong trường hợp PCCC hoặc sự cố nghiêm trọng.",
                    icon = Icons.Default.Warning,
                    containerColor = Color(0xFFFFEBEE),
                    contentColor = Color(0xFFD32F2F),
                    onClick = { showEmergencyDialog = true }
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                // Feedback Area
                FeedbackDisplay(
                    successMessage = uiState.successMessage,
                    errorMessage = uiState.errorMessage,
                    onClear = { viewModel.onEvent(SmartAccessUiEvent.ClearStatus) }
                )
            }
        }
    }

    if (showUnlockDialog) {
        RemoteUnlockDialog(
            buildings = uiState.buildings,
            gates = uiState.gates,
            studentSearchResults = uiState.studentSearchResults,
            isSearchingStudent = uiState.isSearchingStudent,
            selectedStudent = uiState.selectedStudent,
            onSearchStudent = { viewModel.onEvent(SmartAccessUiEvent.SearchStudent(it)) },
            onSelectStudent = { viewModel.onEvent(SmartAccessUiEvent.SelectStudent(it)) },
            onDismiss = { 
                showUnlockDialog = false
                viewModel.onEvent(SmartAccessUiEvent.SelectStudent(null))
            },
            onConfirm = { gateId, buildingId, studentId ->
                viewModel.onEvent(SmartAccessUiEvent.RemoteUnlock(gateId, buildingId, studentId))
                showUnlockDialog = false
            }
        )
    }

    if (showEmergencyDialog) {
        EmergencyOverrideDialog(
            buildings = uiState.buildings,
            onDismiss = { showEmergencyDialog = false },
            onConfirm = { type, reason, buildingId ->
                viewModel.onEvent(SmartAccessUiEvent.EmergencyOverride(type, reason, buildingId))
                showEmergencyDialog = false
            }
        )
    }
}

@Composable
fun StatusOverview(isLoading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                color = if (isLoading) MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f) else Color(0xFFE8F5E9),
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                    } else {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF2E7D32))
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = if (isLoading) "Đang kết nối..." else "Hệ thống trực tuyến",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isLoading) "Vui lòng đợi trong giây lát" else "Sẵn sàng nhận lệnh điều khiển",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, icon: ImageVector, color: Color = MaterialTheme.colorScheme.primary) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Black,
            color = color,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun OperationCard(
    title: String,
    description: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = contentColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = contentColor.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun FeedbackDisplay(
    successMessage: String?,
    errorMessage: String?,
    onClear: () -> Unit
) {
    AnimatedVisibility(
        visible = successMessage != null || errorMessage != null,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        val color = if (successMessage != null) Color(0xFF2E7D32) else Color(0xFFD32F2F)
        val bgColor = if (successMessage != null) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        val icon = if (successMessage != null) Icons.Default.CheckCircle else Icons.Default.Error

        Surface(
            color = bgColor,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = color)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = successMessage ?: errorMessage ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Đóng", tint = color, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteUnlockDialog(
    buildings: List<BuildingResponseDto>,
    gates: List<GateResponseDto>,
    studentSearchResults: List<StudentResponse>,
    isSearchingStudent: Boolean,
    selectedStudent: StudentResponse?,
    onSearchStudent: (String) -> Unit,
    onSelectStudent: (StudentResponse?) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (UUID, UUID, UUID?) -> Unit
) {
    var selectedBuilding by remember { mutableStateOf<BuildingResponseDto?>(null) }
    var selectedGate by remember { mutableStateOf<GateResponseDto?>(null) }
    
    var buildingExpanded by remember { mutableStateOf(false) }
    var gateExpanded by remember { mutableStateOf(false) }
    
    var searchQuery by remember { mutableStateOf("") }
    var searchExpanded by remember { mutableStateOf(false) }

    val filteredGates = remember(selectedBuilding, gates) {
        if (selectedBuilding == null) gates else gates.filter { it.buildingId == selectedBuilding?.id }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Mở khóa cửa từ xa", 
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold 
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Chọn tòa nhà và cổng/phòng tương ứng để thực hiện mở khóa.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Building Selection
                ExposedDropdownMenuBox(
                    expanded = buildingExpanded,
                    onExpandedChange = { buildingExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedBuilding?.name ?: "Chọn Tòa nhà",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("1. Tòa nhà") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = buildingExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = buildingExpanded,
                        onDismissRequest = { buildingExpanded = false }
                    ) {
                        buildings.forEach { building ->
                            DropdownMenuItem(
                                text = { Text(building.name) },
                                onClick = {
                                    selectedBuilding = building
                                    buildingExpanded = false
                                    selectedGate = null 
                                }
                            )
                        }
                    }
                }

                // Gate Selection
                ExposedDropdownMenuBox(
                    expanded = gateExpanded,
                    onExpandedChange = { if (selectedBuilding != null) gateExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedGate?.gateName ?: "Chọn Cổng/Phòng",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("2. Cổng/Phòng") },
                        enabled = selectedBuilding != null,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gateExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = gateExpanded,
                        onDismissRequest = { gateExpanded = false }
                    ) {
                        filteredGates.forEach { gate ->
                            DropdownMenuItem(
                                text = { Text(gate.gateName) },
                                onClick = {
                                    selectedGate = gate
                                    gateExpanded = false
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                
                // Student Search (Optional)
                Column {
                    Text("3. Gán cho sinh viên (Tùy chọn)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    
                    if (selectedStudent != null) {
                        InputChip(
                            selected = true,
                            onClick = { onSelectStudent(null) },
                            label = { Text("${selectedStudent.fullName} (${selectedStudent.studentCode})") },
                            trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            colors = InputChipDefaults.inputChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(8.dp)
                        )
                    } else {
                        Box {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { 
                                    searchQuery = it
                                    onSearchStudent(it)
                                    searchExpanded = true
                                },
                                placeholder = { Text("Tên hoặc MSSV...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = {
                                    if (isSearchingStudent) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    }
                                }
                            )
                            
                            DropdownMenu(
                                expanded = searchExpanded && studentSearchResults.isNotEmpty(),
                                onDismissRequest = { searchExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.8f),
                                properties = PopupProperties(focusable = false)
                            ) {
                                studentSearchResults.forEach { student ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(student.fullName ?: "N/A", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                                Text(student.studentCode ?: "N/A", style = MaterialTheme.typography.bodySmall)
                                            }
                                        },
                                        onClick = {
                                            onSelectStudent(student)
                                            searchQuery = ""
                                            searchExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = selectedBuilding?.id != null && selectedGate?.id != null,
                onClick = {
                    val gateId = selectedGate?.id
                    val buildingId = selectedBuilding?.id
                    if (gateId != null && buildingId != null) {
                        val studentIdStr = selectedStudent?.id
                        val studentId = if (studentIdStr != null) UUID.fromString(studentIdStr) else null
                        onConfirm(gateId, buildingId, studentId)
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) { 
                Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Xác nhận mở") 
            }
        },
        dismissButton = { 
            TextButton(onClick = onDismiss) { Text("Hủy") } 
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyOverrideDialog(
    buildings: List<BuildingResponseDto>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, UUID?) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    var selectedBuilding by remember { mutableStateOf<BuildingResponseDto?>(null) }
    var buildingExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F))
                Spacer(Modifier.width(12.dp))
                Text("Kích hoạt KHẨN CẤP", color = Color(0xFFD32F2F), fontWeight = FontWeight.Black)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "CẢNH BÁO: Hành động này sẽ MỞ TOÀN BỘ CỬA trong phạm vi được chọn. Chỉ sử dụng trong trường hợp PCCC hoặc sự cố đặc biệt.",
                        color = Color(0xFFD32F2F),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Lý do kích hoạt (Bắt buộc)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )

                ExposedDropdownMenuBox(
                    expanded = buildingExpanded,
                    onExpandedChange = { buildingExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedBuilding?.name ?: "Tất cả tòa nhà",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Phạm vi ảnh hưởng") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = buildingExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = buildingExpanded,
                        onDismissRequest = { buildingExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tất cả tòa nhà") },
                            onClick = {
                                selectedBuilding = null
                                buildingExpanded = false
                            }
                        )
                        buildings.forEach { building ->
                            DropdownMenuItem(
                                text = { Text(building.name) },
                                onClick = {
                                    selectedBuilding = building
                                    buildingExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                enabled = reason.isNotBlank(),
                onClick = {
                    onConfirm("GLOBAL_UNLOCK", reason, selectedBuilding?.id)
                },
                shape = RoundedCornerShape(12.dp)
            ) { 
                Text("XÁC NHẬN KÍCH HOẠT", fontWeight = FontWeight.Black) 
            }
        },
        dismissButton = { 
            TextButton(onClick = onDismiss) { Text("Hủy bỏ") } 
        }
    )
}
