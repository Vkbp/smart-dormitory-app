package com.ktx.dormitory.admin.notification.presentation

import com.ktx.dormitory.core.base.BaseContract

data class NotificationBroadcastUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
) : BaseContract.State

sealed class NotificationBroadcastUiEvent : BaseContract.Event {
    data class Broadcast(val title: String, val message: String, val target: String) : NotificationBroadcastUiEvent()
    data object ClearStatus : NotificationBroadcastUiEvent()
}

sealed class NotificationBroadcastUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : NotificationBroadcastUiEffect()
}
