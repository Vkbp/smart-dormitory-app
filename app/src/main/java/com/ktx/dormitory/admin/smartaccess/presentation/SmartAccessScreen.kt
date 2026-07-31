package com.ktx.dormitory.admin.smartaccess.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.window.PopupProperties
import com.ktx.dormitory.admin.common.data.dto.response.BuildingResponseDto
import com.ktx.dormitory.admin.common.data.dto.response.GateResponseDto
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Access Control") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            uiState.successMessage?.let {
                Text(it, color = Color.Green)
            }
            uiState.errorMessage?.let {
                Text(it, color = Color.Red)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showUnlockDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Default.DoorBack, contentDescription = null)
                    Column {
                        Text("Mở khóa cửa từ xa", style = MaterialTheme.typography.titleMedium)
                        Text("Mở cửa phòng/cổng bất kỳ cho sinh viên", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                onClick = { showEmergencyDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Column {
                        Text("Kích hoạt khẩn cấp", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                        Text("Mở toàn bộ cửa trong trường hợp PCCC", style = MaterialTheme.typography.bodySmall)
                    }
                }
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
        title = { Text("Mở khóa cửa") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Building Selection
                ExposedDropdownMenuBox(
                    expanded = buildingExpanded,
                    onExpandedChange = { buildingExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedBuilding?.name ?: "Chọn Tòa nhà",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tòa nhà") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = buildingExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
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
                                    selectedGate = null // Reset gate when building changes
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
                        label = { Text("Cổng/Phòng") },
                        enabled = selectedBuilding != null,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gateExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
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

                HorizontalDivider()
                
                // Student Search (Optional)
                Column {
                    Text("Sinh viên được mở hộ (Tùy chọn)", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(8.dp))
                    
                    if (selectedStudent != null) {
                        InputChip(
                            selected = true,
                            onClick = { onSelectStudent(null) },
                            label = { Text("${selectedStudent.fullName} (${selectedStudent.studentCode})") },
                            trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp)) }
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
                                placeholder = { Text("Tìm theo tên hoặc MSSV...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
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
                                                Text(student.fullName ?: "N/A", style = MaterialTheme.typography.bodyMedium)
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
                }
            ) { Text("Mở cửa") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
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
        title = { Text("Kích hoạt KHẨN CẤP") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Cảnh báo: Hành động này sẽ mở toàn bộ cửa sổ/cửa chính.", color = Color.Red)
                
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Lý do (PCCC,...)") },
                    modifier = Modifier.fillMaxWidth()
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
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
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
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                enabled = reason.isNotBlank(),
                onClick = {
                    onConfirm("GLOBAL_UNLOCK", reason, selectedBuilding?.id)
                }
            ) { Text("XÁC NHẬN") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}
