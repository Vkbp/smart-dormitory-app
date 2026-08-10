package com.ktx.dormitory.student.room.data.dto.request

import com.google.gson.annotations.SerializedName

/**
 * Request body for submitting a room transfer request.
 */
data class RoomTransferRequest(
    @SerializedName("reason") val reason: String,
    @SerializedName("targetRoomId") val targetRoomId: String? = null
)
