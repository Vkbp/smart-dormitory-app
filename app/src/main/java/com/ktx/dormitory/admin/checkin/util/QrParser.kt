package com.ktx.dormitory.admin.checkin.util

import android.util.Log

object QrParser {
    /**
     * Parses the raw QR data from a Vietnam CCCD (chip-based).
     * Format: cccd|old_id|name|dob|gender|address|issue_date
     * Example: 07920100xxxx|021234567|NGUYỄN VĂN A|15052001|Nam|123 Lê Lợi...
     * 
     * @param qrData The raw string from the QR code.
     * @return The 12-digit CCCD string if valid, null otherwise.
     */
    fun parseCccdQr(qrData: String): String? {
        return try {
            val parts = qrData.split('|')
            if (parts.isNotEmpty()) {
                val cccd = parts[0].trim()
                // Validate that it's exactly 12 digits
                if (cccd.length == 12 && cccd.all { it.isDigit() }) {
                    cccd
                } else if (cccd.length == 9 && cccd.all { it.isDigit() }) {
                    // Support old 9-digit IDs if they appear in this format
                    cccd
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("QrParser", "Error parsing CCCD QR: ${e.message}")
            null
        }
    }
}
