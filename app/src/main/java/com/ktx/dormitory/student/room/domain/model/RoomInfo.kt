package com.ktx.dormitory.student.room.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model thông tin phòng
 */
@Parcelize
data class RoomInfo(
    val building: String?,
    val floor: Int?,
    val roomCode: String?,
    val bedCode: String?,
    val status: String?,
    val checkInAt: String?,
    val expectedCheckOutAt: String?
) : Parcelable
