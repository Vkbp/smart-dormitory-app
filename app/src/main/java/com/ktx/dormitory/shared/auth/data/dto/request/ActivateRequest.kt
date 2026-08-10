package com.ktx.dormitory.shared.auth.data.dto.request

import com.google.gson.annotations.SerializedName

data class ActivateRequest(
    @SerializedName("email") val email: String,
    @SerializedName("tempPassword") val tempPassword: String,
    @SerializedName("newPassword") val newPassword: String
)
