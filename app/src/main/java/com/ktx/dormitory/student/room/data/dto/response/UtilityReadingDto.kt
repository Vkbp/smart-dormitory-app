package com.ktx.dormitory.student.room.data.dto.response

import com.google.gson.annotations.SerializedName

data class UtilityReadingDto(
    @SerializedName("id") val id: String?,
    @SerializedName("oldReading") val oldReading: Double?,
    @SerializedName("newReading") val newReading: Double?,
    @SerializedName("readingDate") val readingDate: String?,
    @SerializedName("utilityType") val type: String?
)
