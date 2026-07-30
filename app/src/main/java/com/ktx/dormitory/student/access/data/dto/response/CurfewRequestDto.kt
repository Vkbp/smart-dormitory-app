package com.ktx.dormitory.student.access.data.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for Curfew Request details.
 */
data class CurfewRequestDto(
    @SerializedName("id", alternate = ["requestId", "curfewRequestId", "curfew_request_id"]) val id: String?,
    @SerializedName("studentId", alternate = ["student_id"]) val studentId: String?,
    @SerializedName("requestType") val requestType: String?,
    @SerializedName("reason") val reason: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("expectedArrivalTime") val expectedArrivalTime: String?,
    @SerializedName("note") val note: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("approvedAt") val approvedAt: String?,
    @SerializedName("approvedBy") val approvedBy: String?
)
