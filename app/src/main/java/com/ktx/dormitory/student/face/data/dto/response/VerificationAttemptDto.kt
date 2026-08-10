package com.ktx.dormitory.student.face.data.dto.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class VerificationAttemptDto(
    @SerializedName("attemptId") val attemptId: UUID,
    @SerializedName("gateDeviceId") val gateDeviceId: String?,
    @SerializedName("gateName") val gateName: String? = null,
    @SerializedName("status") val status: String,
    @SerializedName("confidenceScore") val confidenceScore: Double,
    @SerializedName("attemptedAt") val attemptedAt: String
) : Parcelable
