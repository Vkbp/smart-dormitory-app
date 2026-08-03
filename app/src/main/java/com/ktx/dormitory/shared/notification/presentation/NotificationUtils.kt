package com.ktx.dormitory.shared.notification.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object NotificationUtils {
    @Composable
    fun getTypeIcon(type: String?): ImageVector {
        return when (type?.uppercase()) {
            "PAYMENT" -> Icons.Default.AccountBalanceWallet
            "ELECTRIC_FEE" -> Icons.Default.FlashOn
            "WATER_FEE" -> Icons.Default.WaterDrop
            "ACCOMMODATION_FEE" -> Icons.Default.Home
            "APPLICATION_FEE", "DEPOSIT_FEE" -> Icons.AutoMirrored.Filled.Assignment
            "PENALTY_FEE" -> Icons.Default.ReportProblem
            "MAINTENANCE" -> Icons.Default.Build
            "APPLICATION" -> Icons.AutoMirrored.Filled.Assignment
            "ANNOUNCEMENT" -> Icons.Default.Campaign
            "ROOM" -> Icons.Default.Home
            "SMART_ACCESS" -> Icons.Default.LockOpen
            "FACE" -> Icons.Default.Face
            "SYSTEM" -> Icons.Default.Warning
            else -> Icons.Default.Notifications
        }
    }

    @Composable
    fun getTypeColor(type: String?): Color {
        return when (type?.uppercase()) {
            "PAYMENT", "ELECTRIC_FEE", "WATER_FEE", "ACCOMMODATION_FEE", "APPLICATION_FEE", "PENALTY_FEE", "DEPOSIT_FEE" -> Color(0xFF4CAF50) // Green for all payments
            "MAINTENANCE" -> Color(0xFFFF9800) // Orange
            "APPLICATION", "ANNOUNCEMENT" -> Color(0xFF2196F3) // Blue
            "ROOM" -> Color(0xFF9C27B0) // Purple
            "SMART_ACCESS" -> Color(0xFFF44336) // Red
            "FACE" -> Color(0xFF00BCD4) // Cyan
            "SYSTEM" -> Color(0xFFE91E63) // Pink
            else -> MaterialTheme.colorScheme.primary
        }
    }
}
