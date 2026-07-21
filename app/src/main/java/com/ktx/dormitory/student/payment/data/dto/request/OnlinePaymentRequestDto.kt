package com.ktx.dormitory.student.payment.data.dto.request

import com.google.gson.annotations.SerializedName

/**
 * Request DTO cho việc tạo thanh toán online (Smart QR).
 * Khớp với OnlinePaymentRequest.java của Backend.
 */
data class OnlinePaymentRequestDto(
    @SerializedName("billId") val billId: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("paymentMethod") val paymentMethod: String = "BANK_TRANSFER",
    @SerializedName("transactionCode") val transactionCode: String? = null
)
