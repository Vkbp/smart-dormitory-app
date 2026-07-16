package com.ktx.dormitory.student.payment.data.dto.response

import com.google.gson.annotations.SerializedName

data class InvoiceDto(
    @SerializedName("billId") val id: String,
    @SerializedName("billType") val type: String?,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("paidAmount") val paidAmount: Double? = null,
    @SerializedName("remainingAmount") val remainingAmount: Double? = null,
    @SerializedName("status") val status: String?,
    @SerializedName("dueDate") val dueDate: String?,
    @SerializedName("description") val description: String? = null,
    @SerializedName("roomCode") val roomCode: String? = null,
    @SerializedName("bedCode") val bedCode: String? = null
)
