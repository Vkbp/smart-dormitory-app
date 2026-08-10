package com.ktx.dormitory.student.extension.data.dto.response

import com.google.gson.annotations.SerializedName

/**
 * DTO cho đợt đăng ký (Gia hạn/Đăng ký mới).
 * Khớp với RegistrationPeriodResponse.java từ Backend.
 */
data class RegistrationPeriodDto(
    @SerializedName("periodId") val periodId: String,
    @SerializedName("periodName") val periodName: String,
    @SerializedName("registrationType") val registrationType: String,
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("stayStartDate") val stayStartDate: String? = null,
    @SerializedName("stayEndDate") val stayEndDate: String? = null
)
