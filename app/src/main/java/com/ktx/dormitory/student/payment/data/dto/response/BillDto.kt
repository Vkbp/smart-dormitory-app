package com.ktx.dormitory.student.payment.data.dto.response

import com.google.gson.annotations.SerializedName

/**
 * DTO đại diện cho hóa đơn (Bill).
 * Khớp với BillResponse.java của Backend.
 */
data class BillDto(
    @SerializedName("billId") val id: String,
    @SerializedName("billType") val type: String?,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("paidAmount") val paidAmount: Double? = 0.0,
    @SerializedName("remainingAmount") val remainingAmount: Double? = 0.0,
    @SerializedName("status") val status: String?,
    @SerializedName("dueDate") val dueDate: String?,
    @SerializedName("description") val description: String? = null,
    @SerializedName("assignmentId") val assignmentId: String? = null,
    @SerializedName("roomCode") val roomCode: String? = null,
    @SerializedName("bedCode") val bedCode: String? = null
)
