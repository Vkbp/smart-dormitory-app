package com.ktx.dormitory.student.payment.data.dto.response

import com.google.gson.annotations.SerializedName

/**
 * DTO cho Lịch sử giao dịch thanh toán.
 * Ánh xạ từ endpoint /api/v1/bills của Backend.
 */
data class TransactionDto(
    @SerializedName("billId") val transactionId: String?,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("billType") val type: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("dueDate") val createdAt: String?,
    @SerializedName("billCode") val transactionCode: String? = null,
    @SerializedName("studentName") val message: String? = null
)
