package com.ktx.dormitory.shared.auth.data.dto.response

import com.google.gson.annotations.SerializedName

/**
 * DTO cho thông tin tài khoản người dùng (Sinh viên).
 * Tương thích với StudentProfileResponse.java từ Backend khi gọi /v1/students/me.
 */
data class UserResponse(
    @SerializedName("studentId") val id: String? = null,
    @SerializedName("studentCode") val studentCode: String? = null,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("cccd") val cccd: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("faculty") val faculty: String? = null,
    @SerializedName("academicYear") val academicYear: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("roomRole") val roomRole: String? = null
)
