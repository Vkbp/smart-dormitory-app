package com.ktx.dormitory.student.home.presentation

import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.student.room.domain.model.RoomInfo
import com.ktx.dormitory.shared.auth.domain.model.UserData

interface HomeContract : BaseContract {
    data class State(
        val userData: UserData? = null,
        val roomInfo: RoomInfo? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val isResident: Boolean = true
    ) : BaseContract.State

    sealed interface Event : BaseContract.Event {
        data object RefreshData : Event
    }
}
