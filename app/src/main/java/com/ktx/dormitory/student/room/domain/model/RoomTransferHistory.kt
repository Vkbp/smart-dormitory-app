package com.ktx.dormitory.student.room.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Domain model for room transfer history items.
 */
@Parcelize
data class RoomTransferHistory(
    val id: Long,
    val reason: String,
    val currentRoomName: String?,
    val targetRoomName: String?,
    val status: String,
    val adminNote: String?,
    val createdAt: String,
    val updatedAt: String
) : Parcelable
