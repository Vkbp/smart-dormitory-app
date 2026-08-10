package com.ktx.dormitory.student.face.presentation

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ktx.dormitory.navigation.Screen
import com.ktx.dormitory.navigation.components.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceStatusScreen(
    navController: NavController,
    viewModel: FaceManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý khuôn mặt") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(FaceManagementUiEvent.Refresh) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading && uiState.faceProfile == null) {
            LoadingView()
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image and Status
                Box(contentAlignment = Alignment.BottomEnd) {
                    val imageUrl = uiState.faceProfile?.faceImageUrl
                    AsyncImage(
                        model = imageUrl ?: "https://ui-avatars.com/api/?name=Face&background=random",
                        contentDescription = "Face Image",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                    
                    val status = uiState.faceProfile?.status ?: "NOT_REGISTERED"
                    val statusColor = if (uiState.faceProfile?.pendingFaceImageUrl != null && status == "APPROVED") {
                        Color(0xFFFFC107) // Yellow for pending replacement
                    } else {
                        when (status) {
                            "APPROVED" -> Color(0xFF4CAF50)
                            "PENDING" -> Color(0xFFFFC107)
                            "REJECTED" -> Color(0xFFF44336)
                            else -> Color.Gray
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when (uiState.faceProfile?.status) {
                        "APPROVED" -> {
                            if (uiState.faceProfile?.pendingFaceImageUrl != null) "Yêu cầu đổi ảnh đang chờ duyệt"
                            else "Đã được duyệt"
                        }
                        "PENDING" -> "Đang chờ duyệt"
                        "REJECTED" -> "Bị từ chối"
                        "REVOKED" -> "Bị thu hồi"
                        else -> "Chưa đăng ký"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.faceProfile?.pendingFaceImageUrl != null && uiState.faceProfile?.status == "APPROVED") {
                        Color(0xFFFFC107) // Yellow for pending replacement
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Detail Information
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        FaceInfoRow(Icons.Default.CalendarToday, "Ngày đăng ký", uiState.faceProfile?.createdAt?.substringBefore("T") ?: "---")
                        if (uiState.faceProfile?.status == "REJECTED") {
                            FaceInfoRow(Icons.Default.Error, "Lý do từ chối", uiState.faceProfile?.rejectionReason ?: "Không có lý do cụ thể", isError = true)
                        }
                        if (uiState.faceProfile?.pendingFaceImageUrl != null) {
                            FaceInfoRow(Icons.Default.History, "Đang chờ đổi ảnh", "Yêu cầu lúc: ${uiState.faceProfile?.replacementRequestedAt?.substringBefore("T")}")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                if (uiState.faceProfile == null) {
                    Button(
                        onClick = { navController.navigate(Screen.FaceRegistration.route) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("BẮT ĐẦU ĐĂNG KÝ")
                    }
                } else {
                    OutlinedButton(
                        onClick = { /* Navigate to history */ 
                            navController.navigate("face_verification_history")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ListAlt, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("LỊCH SỬ XÁC THỰC")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { /* Navigate to registration for update */ 
                            navController.navigate(Screen.FaceRegistration.route)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Cached, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("YÊU CẦU ĐỔI ẢNH")
                    }
                }
            }
        }
    }
}

@Composable
fun FaceInfoRow(icon: ImageVector, label: String, value: String, isError: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                value, 
                style = MaterialTheme.typography.bodyLarge, 
                fontWeight = FontWeight.Medium,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
