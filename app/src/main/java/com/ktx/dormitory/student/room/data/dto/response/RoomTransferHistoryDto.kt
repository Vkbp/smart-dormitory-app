package com.ktx.dormitory.student.room.data.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for room transfer history items.
 */
data class RoomTransferHistoryDto(
    @SerializedName("id") val id: Long,
    @SerializedName("reason") val reason: String,
    @SerializedName("currentRoomName") val currentRoomName: String?,
    @SerializedName("targetRoomName") val targetRoomName: String?,
    @SerializedName("status") val status: String,
    @SerializedName("adminNote") val adminNote: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)
