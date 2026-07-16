package com.ktx.dormitory.student.access.data.dto.request

import com.google.gson.annotations.SerializedName

data class CurfewCreateRequest(
    @SerializedName("reason") val reason: String,
    @SerializedName("expectedArrivalTime") val expectedArrivalTime: String,
    @SerializedName("note") val note: String?
)
