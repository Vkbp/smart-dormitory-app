package com.ktx.dormitory.core.sync

data class CreateRequestPayload(
    val type: String,
    val content: String
)

data class UpdateRequestStatusPayload(
    val requestId: String,
    val status: String
)

data class MarkNotificationReadPayload(
    val notificationId: String
)

data class RegisterFacePayload(
    val studentId: String,
    val imagePath: String,
    val name: String
)

data class VerifyQrPayload(
    val qrCode: String
)

/**
 * Payload cho đồng bộ xác nhận thanh toán.
 */
data class VerifyPaymentPayload(
    val billId: String,
    val amount: Double,
    val method: String,
    val transactionCode: String
)
