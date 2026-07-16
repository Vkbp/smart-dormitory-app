package com.ktx.dormitory.student.extension.data.dto.response

import com.google.gson.annotations.SerializedName

data class CheckEligibilityResponseDto(
    @SerializedName("eligible") val eligible: Boolean,
    @SerializedName("periodId") val periodId: String?,
    @SerializedName("periodName") val periodName: String?,
    @SerializedName("registrationType") val registrationType: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("target") val target: String?,
    @SerializedName("message") val message: String?
)
