package com.ktx.dormitory.shared.notification.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object NotificationUtils {
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
            else -> Color(0xFF757575) // Fallback default grey
        }
    }
}
