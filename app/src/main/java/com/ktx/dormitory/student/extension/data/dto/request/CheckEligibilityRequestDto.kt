package com.ktx.dormitory.student.extension.data.dto.request

import com.google.gson.annotations.SerializedName

data class CheckEligibilityRequestDto(
    @SerializedName("cccd") val cccd: String
)
