package com.ktx.dormitory.core.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object GlobalEventBus {
    private val _events = MutableSharedFlow<GlobalEvent>()
    val events = _events.asSharedFlow()

    suspend fun emit(event: GlobalEvent) {
        _events.emit(event)
    }
}

sealed class GlobalEvent {
    data class NetworkError(val message: String) : GlobalEvent()
}
