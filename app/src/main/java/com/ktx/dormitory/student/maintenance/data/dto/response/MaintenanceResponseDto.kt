package com.ktx.dormitory.student.maintenance.data.dto.response

import com.google.gson.annotations.SerializedName

data class MaintenanceResponseDto(
    @SerializedName("id") val id: String?,
    @SerializedName("roomId") val roomId: String?,
    @SerializedName("roomCode") val roomCode: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("createdAt") val createdAt: String?
)
