package com.ktx.dormitory.core.util

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val isoDateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val displayDateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    fun formatIsoDate(isoDate: String?): String {
        if (isoDate.isNullOrBlank()) return "N/A"
        return try {
            val date = isoDateFormat.parse(isoDate)
            if (date != null) displayDateFormat.format(date) else isoDate
        } catch (e: Exception) {
            isoDate
        }
    }

    fun formatIsoDateTime(isoDateTime: String?): String {
        if (isoDateTime.isNullOrBlank()) return "N/A"
        return try {
            val date = isoDateTimeFormat.parse(isoDateTime)
            if (date != null) displayDateTimeFormat.format(date) else isoDateTime
        } catch (e: Exception) {
            formatIsoDate(isoDateTime)
        }
    }

    fun formatDate(timestamp: Long): String {
        return displayDateFormat.format(Date(timestamp))
    }

    fun formatToIso(timestamp: Long): String {
        return isoDateTimeFormat.format(Date(timestamp))
    }

    fun calculateDaysRemaining(expectedCheckOutAt: String?): Long {
        if (expectedCheckOutAt.isNullOrBlank()) return -1
        return try {
            val checkOutDate = isoDateTimeFormat.parse(expectedCheckOutAt) ?: return -1
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
            
            val diff = checkOutDate.time - today.time
            diff / (1000 * 60 * 60 * 24)
        } catch (e: Exception) {
            -1
        }
    }

    /**
     * Chuyển đổi định dạng ngày nhập từ User (dd/MM/yyyy) thành chuẩn ISO-8601 (YYYY-MM-DDTHH:mm:ss)
     * để gửi lên Backend.
     */
    fun toPayloadDateTime(userInputDate: String): String? {
        val formats = listOf(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        )
        
        for (format in formats) {
            try {
                val date = format.parse(userInputDate)
                if (date != null) {
                    return isoDateTimeFormat.format(date)
                }
            } catch (e: Exception) {
                // Thử format tiếp theo
            }
        }
        return null
    }
}
