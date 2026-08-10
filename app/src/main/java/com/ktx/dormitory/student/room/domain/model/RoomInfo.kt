package com.ktx.dormitory.student.room.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model thông tin phòng
 */
@Parcelize
data class RoomInfo(
    val roomId: String? = null,
    val building: String? = null,
    val buildingName: String? = null,
    val floor: Int? = null,
    val roomCode: String? = null,
    val bedCode: String? = null,
    val status: String? = null,
    val checkInAt: String? = null,
    val expectedCheckOutAt: String? = null,
    val availableBeds: Int? = null
) : Parcelable
