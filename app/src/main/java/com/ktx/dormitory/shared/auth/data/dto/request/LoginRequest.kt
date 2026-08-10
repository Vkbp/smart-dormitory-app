package com.ktx.dormitory.shared.auth.data.dto.request

import com.google.gson.annotations.SerializedName

/**
 * DTO cho yêu cầu Đăng nhập
 */
data class LoginRequest(
    @SerializedName("usernameOrEmail") val usernameOrEmail: String,
    @SerializedName("password") val password: String
)
