package com.ktx.dormitory.student.room.data.dto.response

import com.google.gson.annotations.SerializedName

data class RoomInfoDto(
    @SerializedName("roomId") val roomId: String? = null,
    @SerializedName("buildingCode") val building: String? = null,
    @SerializedName("buildingName") val buildingName: String? = null,
    @SerializedName("floorNumber") val floor: Int? = null,
    @SerializedName("roomCode") val roomCode: String? = null,
    @SerializedName("bedCode") val bedCode: String? = null,
    @SerializedName("assignmentStatus") val status: String? = null,
    @SerializedName("checkInAt") val checkInAt: String? = null,
    @SerializedName("expectedCheckOutAt") val expectedCheckOutAt: String? = null,
    @SerializedName("availableBeds") val availableBeds: Int? = null
)
