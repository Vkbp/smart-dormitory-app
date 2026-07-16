package com.ktx.dormitory.admin.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ktx.dormitory.navigation.Screen
import com.ktx.dormitory.ui.components.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    loginViewModel: com.ktx.dormitory.shared.auth.presentation.LoginViewModel,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val loginState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val adminTools = listOf(
        AdminTool("Điều khiển cửa", Screen.AdminSmartAccess, Icons.Default.DoorSliding, Color(0xFF673AB7)),
        AdminTool("Duyệt khuôn mặt", Screen.AdminFaceApproval, Icons.Default.Face, Color(0xFFE91E63)),
        AdminTool("Duyệt trả phòng", Screen.AdminCheckoutApproval, Icons.AutoMirrored.Filled.ExitToApp, Color(0xFFFF5722)),
        AdminTool("Duyệt gia hạn", Screen.AdminExtensionApproval, Icons.Default.Update, Color(0xFF4CAF50)),
        AdminTool("Nhận phòng", Screen.AdminCheckIn, Icons.Default.HowToReg, Color(0xFF2196F3)),
        AdminTool("Gửi thông báo", Screen.AdminNotificationBroadcast, Icons.Default.BroadcastOnHome, Color(0xFFFF9800))
    )

    Scaffold(
        bottomBar = { BottomNavBar(navController, loginViewModel) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            DashboardHeader(
                username = loginState.userData?.fullName ?: "Admin",
                onLogout = {
                    loginViewModel.logout {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Statistics Section (Like Backend)
                Text(
                    text = "Báo cáo hệ thống",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (uiState.isLoading && uiState.stats == null) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val stats = uiState.stats
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.height(260.dp), // Fixed height for LazyVerticalGrid inside Column
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        userScrollEnabled = false
                    ) {
                        item { StatCard("Hồ sơ chờ duyệt", stats?.pendingApplications ?: 0, Color(0xFFFFC107)) }
                        item { StatCard("Chưa đóng tiền", stats?.waitingForPayment ?: 0, Color(0xFFF44336)) }
                        item { StatCard("Chờ Check-in", stats?.pendingCheckIn ?: 0, Color(0xFF9C27B0)) }
                        item { StatCard("Đang lưu trú", stats?.occupiedAssignments ?: 0, Color(0xFF4CAF50)) }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Tools Section
                Text(
                    text = "Tiện ích quản lý",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                adminTools.chunked(2).forEach { rowTools ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowTools.forEach { tool ->
                            AdminToolCard(tool, modifier = Modifier.weight(1f)) {
                                navController.navigate(tool.screen.route)
                            }
                        }
                        if (rowTools.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DashboardHeader(username: String, onLogout: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(top = 48.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Xin chào,",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = username,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            IconButton(
                onClick = onLogout,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: Int, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            }
        }
    }
}

data class AdminTool(val name: String, val screen: Screen, val icon: ImageVector, val color: Color)

@Composable
fun AdminToolCard(tool: AdminTool, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tool.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    tint = tool.color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = tool.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
