package com.ktx.dormitory.student.payment.data.dto.response

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * DTO đại diện cho hóa đơn (Bill).
 * Khớp với BillResponse.java của Backend.
 */
data class BillDto(
    @SerializedName("billId") val id: String,
    @SerializedName("billCode") val billCode: String?,
    @SerializedName("billType") val type: String?,
    @SerializedName("amount") val amount: BigDecimal?,
    @SerializedName("paidAmount") val paidAmount: BigDecimal? = BigDecimal.ZERO,
    @SerializedName("remainingAmount") val remainingAmount: BigDecimal? = BigDecimal.ZERO,
    @SerializedName("status") val status: String?,
    @SerializedName("dueDate") val dueDate: String?,
    @SerializedName("description") val description: String? = null,
    @SerializedName("assignmentId") val assignmentId: String? = null,
    @SerializedName("billStatus") val billStatus: String? = null,
    @SerializedName("assignmentStatus") val assignmentStatus: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("isBillOwner") val isBillOwner: Boolean? = false,
    @SerializedName("roomCode") val roomCode: String? = null,
    @SerializedName("bedCode") val bedCode: String? = null
)
