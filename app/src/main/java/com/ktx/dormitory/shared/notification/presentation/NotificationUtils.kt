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
        val typeUpper = type?.uppercase() ?: ""
        return when {
            typeUpper in listOf("PAYMENT", "BILL", "INVOICE") -> Icons.Default.AccountBalanceWallet
            typeUpper.contains("ELECTRIC") || typeUpper == "TIEN_DIEN" -> Icons.Default.FlashOn
            typeUpper == "ACCOMMODATION_FEE" -> Icons.Default.Home
            typeUpper == "PENALTY_FEE" -> Icons.Default.ReportProblem
            typeUpper == "MAINTENANCE" -> Icons.Default.Build
            typeUpper == "APPLICATION" -> Icons.AutoMirrored.Filled.Assignment
            typeUpper == "ANNOUNCEMENT" -> Icons.Default.Campaign
            typeUpper == "ROOM" -> Icons.Default.Home
            typeUpper == "SMART_ACCESS" -> Icons.Default.LockOpen
            typeUpper == "FACE" -> Icons.Default.Face
            typeUpper == "SYSTEM" -> Icons.Default.Warning
            else -> Icons.Default.Notifications
        }
    }

    @Composable
    fun getTypeColor(type: String?): Color {
        val typeUpper = type?.uppercase() ?: ""
        val isPayment = typeUpper in listOf(
            "PAYMENT", "ELECTRIC_FEE", "ACCOMMODATION_FEE",
            "PENALTY_FEE", "BILL", "INVOICE", "PAYMENT_NOTICE"
        )
        
        return when {
            isPayment -> Color(0xFF4CAF50) // Green
            typeUpper == "MAINTENANCE" -> Color(0xFFFF9800) // Orange
            typeUpper in listOf("APPLICATION", "ANNOUNCEMENT") -> Color(0xFF2196F3) // Blue
            typeUpper == "ROOM" -> Color(0xFF9C27B0) // Purple
            typeUpper == "SMART_ACCESS" -> Color(0xFFF44336) // Red
            typeUpper == "FACE" -> Color(0xFF00BCD4) // Cyan
            typeUpper == "SYSTEM" -> Color(0xFFE91E63) // Pink
            else -> MaterialTheme.colorScheme.primary
        }
    }
}
