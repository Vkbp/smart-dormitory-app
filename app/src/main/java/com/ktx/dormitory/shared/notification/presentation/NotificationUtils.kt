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
            "MAINTENANCE" -> Icons.Default.Build
            "APPLICATION" -> Icons.AutoMirrored.Filled.Assignment
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
            "PAYMENT" -> Color(0xFF4CAF50) // Green
            "MAINTENANCE" -> Color(0xFFFF9800) // Orange
            "APPLICATION" -> Color(0xFF2196F3) // Blue
            "ROOM" -> Color(0xFF9C27B0) // Purple
            "SMART_ACCESS" -> Color(0xFFF44336) // Red
            "FACE" -> Color(0xFF00BCD4) // Cyan
            "SYSTEM" -> Color(0xFFE91E63) // Pink
            else -> MaterialTheme.colorScheme.primary
        }
    }
}
