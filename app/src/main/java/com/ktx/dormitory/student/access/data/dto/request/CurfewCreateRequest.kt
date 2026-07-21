package com.ktx.dormitory.student.access.data.dto.request

import com.google.gson.annotations.SerializedName

/**
 * Request DTO for creating a new curfew/absence request.
 * Aligned with CurfewRequestCreateRequest.java in Backend.
 */
data class CurfewCreateRequest(
    @SerializedName("requestType") val requestType: String, // LATE_RETURN, ABSENCE
    @SerializedName("reason") val reason: String,
    @SerializedName("startDate") val startDate: String? = null, // Only for ABSENCE
    @SerializedName("expectedArrivalTime") val expectedArrivalTime: String, // Required for both
    @SerializedName("note") val note: String? = null
)
