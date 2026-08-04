package com.ktx.dormitory.student.room.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ktx.dormitory.core.util.DateTimeUtils
import com.ktx.dormitory.student.room.domain.model.UtilityReading
import com.ktx.dormitory.ui.components.ErrorView
import com.ktx.dormitory.ui.components.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomUtilitiesScreen(
    navController: NavController,
    viewModel: RoomUtilitiesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉ số điện nước", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoading -> LoadingView()
                uiState.error != null -> ErrorView(
                    message = uiState.error!!,
                    onRetry = { viewModel.onEvent(RoomUtilitiesUiEvent.Refresh) }
                )
                uiState.utilities.isNotEmpty() -> {
                    val utilities = uiState.utilities
                    val latestReading = utilities.first()
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Tháng hiện tại",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        UtilityCard(
                            title = "Chỉ số điện",
                            oldReading = latestReading.oldReading,
                            newReading = latestReading.newReading,
                            readingDate = latestReading.readingDate,
                            icon = Icons.Default.ElectricBolt,
                            color = Color(0xFFFFC107)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        InfoSection()

                        if (utilities.size > 1) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Lịch sử chốt sổ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            utilities.drop(1).forEach { reading ->
                                UtilityHistoryItem(reading)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
                else -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Chưa có dữ liệu chỉ số điện nước")
                    }
                }
            }
        }
    }
}

@Composable
fun UtilityCard(
    title: String,
    oldReading: Double?,
    newReading: Double?,
    readingDate: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = icon, 
                        contentDescription = null, 
                        tint = color,
                        modifier = Modifier.padding(8.dp).size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    if (readingDate != null) {
                        Text(
                            text = "Ngày chốt: ${DateTimeUtils.formatIsoDate(readingDate)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ReadingItem(
                    label = "Chỉ số cũ",
                    value = oldReading?.toString() ?: "0.0",
                    modifier = Modifier.weight(1f)
                )
                Divider(modifier = Modifier.height(40.dp).width(1.dp).align(Alignment.CenterVertically))
                ReadingItem(
                    label = "Chỉ số mới",
                    value = if (newReading == null) "Đang sử dụng..." else newReading.toString(),
                    modifier = Modifier.weight(1f),
                    isHighlighted = newReading != null,
                    alignment = Alignment.End
                )
            }
        }
    }
}

@Composable
fun UtilityHistoryItem(reading: UtilityReading) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Ngày chốt: ${DateTimeUtils.formatIsoDate(reading.readingDate)}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Chỉ số: ${reading.oldReading} -> ${reading.newReading ?: "..."}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (reading.newReading != null && reading.oldReading != null) {
                val usage = reading.newReading - reading.oldReading
                Text(
                    text = "+${String.format("%.1f", usage)} kWh",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ReadingItem(
    label: String, 
    value: String, 
    modifier: Modifier, 
    isHighlighted: Boolean = false,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(modifier = modifier, horizontalAlignment = alignment) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun InfoSection() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Chỉ số cũ (oldReading) chính là con số đã được chốt sổ lúc bạn vừa dọn vào ở. Hãy căn cứ vào đây để tính tiền sử dụng trong tháng.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
