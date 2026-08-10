package com.ktx.dormitory.student.room.presentation

import android.os.Parcelable
import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.student.room.domain.model.RoomInfo
import com.ktx.dormitory.student.room.domain.model.RoomTransferHistory
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomTransferUiState(
    val isLoading: Boolean = false,
    val history: List<RoomTransferHistory> = emptyList(),
    val availableRooms: List<RoomInfo> = emptyList(),
    val groupedAvailableRooms: Map<String, List<RoomInfo>> = emptyMap(),
    val reason: String = "",
    val targetRoomId: String = "",
    val targetRoomCode: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val reasonError: String? = null,
    val selectedTab: Int = 0 // 0: Gửi yêu cầu, 1: Lịch sử
) : BaseContract.State, Parcelable

sealed interface RoomTransferUiEvent : BaseContract.Event {
    data object LoadHistory : RoomTransferUiEvent
    data object LoadAvailableRooms : RoomTransferUiEvent
    data class ReasonChanged(val reason: String) : RoomTransferUiEvent
    data class RoomSelected(val roomId: String, val roomCode: String) : RoomTransferUiEvent
    data object SubmitRequest : RoomTransferUiEvent
    data class CancelRequest(val id: Long) : RoomTransferUiEvent
    data class TabSelected(val index: Int) : RoomTransferUiEvent
    data object ClearError : RoomTransferUiEvent
}

sealed interface RoomTransferUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : RoomTransferUiEffect
    data object NavigateBack : RoomTransferUiEffect
    data object SubmissionSuccess : RoomTransferUiEffect
    data object CancelSuccess : RoomTransferUiEffect
}
