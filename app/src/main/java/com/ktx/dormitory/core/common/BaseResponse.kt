package com.ktx.dormitory.core.common

import com.google.gson.annotations.SerializedName

/**
 * Standard API Response Wrapper.
 */
data class BaseResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("errorCode") val errorCode: String? = null,
    @SerializedName("data") val data: T? = null
)
