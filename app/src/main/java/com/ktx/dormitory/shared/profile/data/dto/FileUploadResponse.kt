package com.ktx.dormitory.shared.profile.data.dto

import com.google.gson.annotations.SerializedName

data class FileUploadResponse(
    @SerializedName("url") val url: String
)
