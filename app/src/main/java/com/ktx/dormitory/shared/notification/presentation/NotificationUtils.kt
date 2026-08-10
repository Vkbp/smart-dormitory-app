package com.ktx.dormitory.shared.notification.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.ktx.dormitory.navigation.Screen

object NotificationUtils {
    
    fun navigate(actionUrl: String?, navController: NavController) {
        if (actionUrl.isNullOrBlank()) return

        when {
            actionUrl.contains("/student/bills") || actionUrl.contains("/payment") -> {
                navController.navigate(Screen.Payment.route)
            }
            actionUrl.contains("/student/room") || actionUrl.contains("/room-info") -> {
                navController.navigate(Screen.RoomInfo.route)
            }
            actionUrl.contains("/student/face") || actionUrl.contains("/face-status") -> {
                navController.navigate(Screen.FaceStatus.route)
            }
            actionUrl.contains("/student/checkout") || actionUrl.contains("/checkout") -> {
                navController.navigate(Screen.Checkout.route)
            }
            actionUrl.contains("/student/extension") || actionUrl.contains("/quick-extend") -> {
                navController.navigate(Screen.QuickExtend.route)
            }
            actionUrl.contains("/student/access") || actionUrl.contains("/access-history") -> {
                navController.navigate(Screen.AccessHistory.route)
            }
            actionUrl.contains("/student/maintenance") || actionUrl.contains("/maintenance") -> {
                navController.navigate(Screen.Maintenance.route)
            }
            // Fallback for direct routes if they happen to be valid
            else -> {
                try {
                    navController.navigate(actionUrl)
                } catch (e: Exception) {
                    // Ignore or log error
                }
            }
        }
    }

    @Composable
    fun getTypeIcon(type: String?): ImageVector {
        val typeUpper = type?.uppercase() ?: ""
        return when (typeUpper) {
            "APPLICATION" -> Icons.AutoMirrored.Filled.Assignment
            "ROOM" -> Icons.Default.Key
            "SYSTEM" -> Icons.Default.SmartToy
            "ANNOUNCEMENT" -> Icons.Default.Campaign
            "MAINTENANCE" -> Icons.Default.Build
            "PAYMENT" -> Icons.Default.CheckCircle
            "ELECTRIC_FEE", "ACCOMMODATION_FEE", "PENALTY_FEE" -> Icons.Default.FlashOn
            "VIOLATION" -> Icons.Default.Warning
            else -> Icons.Default.Notifications // Fallback
        }
    }

    @Composable
    fun getTypeColor(type: String?): Color {
        val typeUpper = type?.uppercase() ?: ""
        return when (typeUpper) {
            "APPLICATION" -> Color(0xFF2196F3) // Blue
            "ROOM" -> Color(0xFF9C27B0) // Purple
            "SYSTEM" -> Color(0xFF9E9E9E) // Grey
            "ANNOUNCEMENT" -> Color(0xFF4CAF50) // Green
            "MAINTENANCE" -> Color(0xFFFFC107) // Amber/Yellow
            "PAYMENT" -> Color(0xFF4CAF50) // Green (Success)
            "ELECTRIC_FEE", "ACCOMMODATION_FEE", "PENALTY_FEE" -> Color(0xFFF44336) // Red (Báo nợ)
            "VIOLATION" -> Color(0xFFD32F2F) // Deep Red (Error/Warning)
            else -> Color(0xFF757575) // Fallback default grey
        }
    }
}
