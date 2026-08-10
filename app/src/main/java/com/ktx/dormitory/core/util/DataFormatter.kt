package com.ktx.dormitory.core.util

import java.util.Locale

object DataFormatter {

    /**
     * Rút ngắn ID dài (UUID) thành dạng "xxxx...xxxx" để dễ nhìn hơn trên giao diện.
     */
    fun formatId(id: String?): String {
        if (id.isNullOrBlank()) return "N/A"
        if (id.length <= 12) return id
        return "${id.take(6)}...${id.takeLast(6)}"
    }

    /**
     * Chuyển đổi phương thức truy cập sang tiếng Việt.
     */
    fun formatAccessMethod(method: String?): String {
        return when (method?.uppercase()) {
            "FACE" -> "Nhận diện khuôn mặt"
            "QR", "QR_CODE" -> "Mã QR"
            "RFID", "CARD" -> "Thẻ từ (RFID)"
            "REMOTE_ADMIN", "ADMIN" -> "Admin mở từ xa"
            "MANUAL" -> "Thủ công"
            null -> "Không xác định"
            else -> method
        }
    }

    /**
     * Chuyển đổi quyết định truy cập sang tiếng Việt.
     */
    fun formatAccessDecision(decision: String?): String {
        return when (decision?.uppercase()) {
            "GRANTED", "SUCCESS" -> "Thành công"
            "DENIED", "REJECTED", "FAIL" -> "Bị từ chối"
            null -> "N/A"
            else -> decision
        }
    }

    /**
     * Chuyển đổi trạng thái xác thực sang tiếng Việt.
     */
    fun formatVerificationStatus(status: String?): String {
        return when (status?.uppercase()) {
            "SUCCESS" -> "Xác thực thành công"
            "FAIL", "FAILED" -> "Xác thực thất bại"
            "PENDING" -> "Đang chờ"
            null -> "N/A"
            else -> status
        }
    }

    /**
     * Chuyển đổi trạng thái bảo trì sang tiếng Việt.
     */
    fun formatMaintenanceStatus(status: String?): String {
        return when (status?.uppercase()) {
            "PENDING" -> "Đang chờ xử lý"
            "IN_PROGRESS" -> "Đang sửa chữa"
            "DONE", "COMPLETED" -> "Đã hoàn thành"
            "REJECTED" -> "Đã từ chối"
            null -> "Không xác định"
            else -> status
        }
    }

    /**
     * Chuyển đổi trạng thái đổi phòng sang tiếng Việt.
     */
    fun formatRoomTransferStatus(status: String?): String {
        return when (status?.uppercase()) {
            "PENDING" -> "Đang chờ duyệt"
            "APPROVED" -> "Đã chấp thuận"
            "REJECTED" -> "Bị từ chối"
            null -> "Không xác định"
            else -> status
        }
    }

    /**
     * Chuyển đổi trạng thái đơn vào trễ/vắng mặt.
     */
    fun formatCurfewStatus(status: String?): String {
        return when (status?.uppercase()) {
            "PENDING" -> "Đang chờ duyệt"
            "APPROVED" -> "Đã chấp thuận"
            "REJECTED" -> "Bị từ chối"
            null -> "Không xác định"
            else -> status
        }
    }

    /**
     * Chuyển đổi ID người vận hành sang tên hiển thị thân thiện.
     */
    fun formatOperator(operatorId: String?): String {
        if (operatorId.isNullOrBlank()) return "Hệ thống"
        return when (operatorId.uppercase()) {
            "SYSTEM" -> "Hệ thống tự động"
            "ADMIN" -> "Quản trị viên"
            else -> "ID: ${formatId(operatorId)}"
        }
    }

    /**
     * Định dạng số tiền tệ VNĐ.
     */
    fun formatCurrency(amount: Any?): String {
        val value = when (amount) {
            is Number -> amount.toDouble()
            is String -> amount.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
        return String.format(Locale.getDefault(), "%,.0f VNĐ", value)
    }
}
