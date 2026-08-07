package com.ktx.dormitory.navigation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination // THÊM DÒNG NÀY
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ktx.dormitory.navigation.Screen

import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ktx.dormitory.shared.auth.presentation.LoginViewModel
import com.ktx.dormitory.shared.notification.presentation.NotificationViewModel

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BottomNavBar(
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    
    val notificationViewModel: NotificationViewModel = if (activity != null) {
        hiltViewModel(activity)
    } else {
        hiltViewModel()
    }

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    val notificationState by notificationViewModel.uiState.collectAsStateWithLifecycle()
    val loginState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val userRole = loginState.userData?.role?.uppercase() ?: ""

    // Nếu chưa lấy được Role, chưa hiển thị gì để tránh nhầm lẫn UI (như hiện nhầm Student bar)
    if (userRole.isEmpty()) return

    val items = when {
        userRole.contains("ADMIN") || userRole.contains("STAFF") -> listOf(
            BottomNavItem(Screen.AdminDashboard, Icons.Default.Dashboard, "Tổng quan"),
            BottomNavItem(Screen.AdminFaceApproval, Icons.Default.Face, "Duyệt mặt"),
            BottomNavItem(Screen.AdminCheckIn, Icons.Default.HowToReg, "Nhận phòng"),
            BottomNavItem(Screen.AdminSmartAccess, Icons.Default.DoorSliding, "Cửa"),
            BottomNavItem(Screen.AdminNotificationBroadcast, Icons.Default.BroadcastOnHome, "Thông báo")
        )
        else -> listOf(
            BottomNavItem(Screen.StudentHome, Icons.Default.Home, "Trang chủ"),
            BottomNavItem(Screen.RoomInfo, Icons.Default.MeetingRoom, "Phòng"),
            BottomNavItem(Screen.Payment, Icons.Default.Payments, "Thanh toán"),
            BottomNavItem(Screen.Notifications, Icons.Default.Notifications, "Thông báo"),
            BottomNavItem(Screen.Profile, Icons.Default.Person, "Hồ sơ")
        )
    }

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    if (item.screen == Screen.Notifications && notificationState.unreadCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge {
                                    Text(notificationState.unreadCount.toString())
                                }
                            }
                        ) {
                            Icon(item.icon, contentDescription = item.label)
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.label)
                    }
                },
                label = { Text(item.label) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
