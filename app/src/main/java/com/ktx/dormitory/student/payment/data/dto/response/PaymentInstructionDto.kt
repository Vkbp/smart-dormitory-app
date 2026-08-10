package com.ktx.dormitory.student.payment.data.dto.response

import com.google.gson.annotations.SerializedName

/**
 * DTO cho hướng dẫn thanh toán.
 * Khớp với PaymentInstructionResponse.java từ Backend.
 */
data class PaymentInstructionDto(
    @SerializedName("bankName") val bankName: String?,
    @SerializedName("bankAccount") val accountNumber: String?,
    @SerializedName("accountHolder") val accountHolder: String?,
    @SerializedName("qrCodeUrl") val qrCodeUrl: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("amount") val amount: java.math.BigDecimal?
)
