package com.ktx.dormitory.admin.smartaccess.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ktx.dormitory.admin.common.data.dto.response.BuildingResponseDto
import com.ktx.dormitory.admin.common.data.dto.response.GateResponseDto
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
            onDismiss = { showUnlockDialog = false },
            onConfirm = { gateId, buildingId ->
                viewModel.onEvent(SmartAccessUiEvent.RemoteUnlock(gateId, buildingId))
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
    onDismiss: () -> Unit,
    onConfirm: (UUID, UUID) -> Unit
) {
    var selectedBuilding by remember { mutableStateOf<BuildingResponseDto?>(null) }
    var selectedGate by remember { mutableStateOf<GateResponseDto?>(null) }
    
    var buildingExpanded by remember { mutableStateOf(false) }
    var gateExpanded by remember { mutableStateOf(false) }

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
            }
        },
        confirmButton = {
            Button(
                enabled = selectedBuilding?.id != null && selectedGate?.id != null,
                onClick = {
                    val gateId = selectedGate?.id
                    val buildingId = selectedBuilding?.id
                    if (gateId != null && buildingId != null) {
                        onConfirm(gateId, buildingId)
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
                    onConfirm("EMERGENCY_OPEN", reason, selectedBuilding?.id)
                }
            ) { Text("XÁC NHẬN") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}
