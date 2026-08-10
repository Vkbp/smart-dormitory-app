package com.ktx.dormitory.shared.auth.data.dto.request

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String
)
