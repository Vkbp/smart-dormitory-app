package com.ktx.dormitory.student.room.data.dto.response

import com.google.gson.annotations.SerializedName

data class RoomInfoDto(
    @SerializedName("buildingCode") val building: String?,
    @SerializedName("floorNumber") val floor: Int?,
    @SerializedName("roomCode") val roomCode: String?,
    @SerializedName("bedCode") val bedCode: String?,
    @SerializedName("assignmentStatus") val status: String?,
    @SerializedName("checkInAt") val checkInAt: String?,
    @SerializedName("expectedCheckOutAt") val expectedCheckOutAt: String?
)
