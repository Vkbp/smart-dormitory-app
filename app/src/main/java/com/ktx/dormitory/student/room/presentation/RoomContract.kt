package com.ktx.dormitory.student.room.presentation

import android.os.Parcelable
import com.ktx.dormitory.student.room.domain.model.RoomInfo
import com.ktx.dormitory.student.room.domain.model.UtilityReading
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomUiState(
    val isLoading: Boolean = false,
    val roomInfo: RoomInfo? = null,
    val latestUtility: UtilityReading? = null,
    val error: String? = null
) : Parcelable

sealed interface RoomUiEvent {
    data object LoadRoomInfo : RoomUiEvent
}
