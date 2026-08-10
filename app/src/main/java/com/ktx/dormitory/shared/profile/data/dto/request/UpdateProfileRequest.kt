package com.ktx.dormitory.shared.profile.data.dto.request

import com.google.gson.annotations.SerializedName

/**
 * DTO cho yêu cầu cập nhật hồ sơ
 */
data class UpdateProfileRequest(
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("fatherName") val fatherName: String? = null,
    @SerializedName("fatherPhone") val fatherPhone: String? = null,
    @SerializedName("motherName") val motherName: String? = null,
    @SerializedName("motherPhone") val motherPhone: String? = null,
    @SerializedName("emergencyContact") val emergencyContact: String? = null,
    @SerializedName("permanentAddress") val permanentAddress: String? = null,
    @SerializedName("avatarUrl") val avatarUrl: String? = null
)
