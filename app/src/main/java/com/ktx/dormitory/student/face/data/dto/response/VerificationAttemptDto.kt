package com.ktx.dormitory.student.face.data.dto.response

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class VerificationAttemptDto(
    @SerializedName("attemptId") val attemptId: UUID,
    @SerializedName("gateDeviceId") val gateDeviceId: String?,
    @SerializedName("status") val status: String,
    @SerializedName("confidenceScore") val confidenceScore: Double,
    @SerializedName("attemptedAt") val attemptedAt: String
)
