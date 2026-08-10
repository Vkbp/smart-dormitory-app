package com.ktx.dormitory.student.checkout.data.dto.response

import com.google.gson.annotations.SerializedName

data class CheckoutResponseDto(
    @SerializedName("requestId") val requestId: String,
    @SerializedName("studentId") val studentId: String?,
    @SerializedName("studentCode") val studentCode: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("assignmentId") val assignmentId: String?,
    @SerializedName("roomCode") val roomCode: String?,
    @SerializedName("bedCode") val bedCode: String?,
    @SerializedName("intendedCheckoutDate") val intendedCheckoutDate: String,
    @SerializedName("reason") val reason: String?,
    @SerializedName("bankAccountNumber") val bankAccountNumber: String?,
    @SerializedName("bankName") val bankName: String?,
    @SerializedName("status") val status: String,
    @SerializedName("checkoutReason") val checkoutReason: String?,
    @SerializedName("estimatedRefundAmount") val estimatedRefundAmount: Double?,
    @SerializedName("rejectReason") val rejectReason: String?,
    @SerializedName("createdAt") val createdAt: String?
)
