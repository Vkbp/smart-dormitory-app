package com.ktx.dormitory.student.payment.data.dto.response

import com.google.gson.annotations.SerializedName

/**
 * DTO trả về sau khi tạo yêu cầu thanh toán Online (Smart QR).
 * Khớp với PaymentResponse.java của Backend.
 */
data class PaymentResponseDto(
    @SerializedName("paymentId") val paymentId: String?,
    @SerializedName("billId") val billId: String?,
    @SerializedName("transactionCode") val transactionCode: String?,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("paymentMethod") val paymentMethod: String?,
    @SerializedName("paymentStatus") val paymentStatus: String?, // PENDING, SUCCESS, FAILED
    @SerializedName("paymentUrl") val paymentUrl: String?,      // Key field: URL ảnh QR từ SePay
    @SerializedName("paidAt") val paidAt: String? = null
)
