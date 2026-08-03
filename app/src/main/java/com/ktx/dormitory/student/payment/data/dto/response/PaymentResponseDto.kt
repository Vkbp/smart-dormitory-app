package com.ktx.dormitory.student.payment.data.dto.response

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * DTO trả về sau khi tạo yêu cầu thanh toán Online (Smart QR).
 * Khớp với PaymentResponse.java của Backend.
 */
data class PaymentResponseDto(
    @SerializedName("paymentId") val paymentId: String?,
    @SerializedName("billId") val billId: String?,
    @SerializedName("billCode") val billCode: String? = null,
    @SerializedName("transactionCode") val transactionCode: String?,
    @SerializedName("amount") val amount: BigDecimal?,
    @SerializedName("paymentMethod") val paymentMethod: String?,
    @SerializedName("paymentStatus") val paymentStatus: String?, // PENDING, SUCCESS, FAILED
    @SerializedName("paymentUrl") val paymentUrl: String?,      // Key field: URL ảnh QR từ SePay
    @SerializedName("paidAt") val paidAt: String? = null,
    @SerializedName("billStatus") val billStatus: String?,
    @SerializedName("assignmentStatus") val assignmentStatus: String?,
    @SerializedName("paidAmount") val paidAmount: BigDecimal?,
    @SerializedName("message") val message: String?
)
