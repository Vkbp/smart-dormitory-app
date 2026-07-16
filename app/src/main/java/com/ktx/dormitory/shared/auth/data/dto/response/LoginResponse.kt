package com.ktx.dormitory.shared.auth.data.dto.response

import com.google.gson.annotations.SerializedName

/**
 * DTO cho phản hồi Đăng nhập
 */
data class LoginResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("tokenType") val tokenType: String = "Bearer",
    @SerializedName("role") val role: String? = null
)
