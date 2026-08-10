package com.ktx.dormitory.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Standard BaseViewModel for MVI-lite pattern.
 * Manages State, Events, and Effects.
 */
abstract class BaseViewModel<S : BaseContract.State, E : BaseContract.Event, Ef : BaseContract.Effect>(
    initialState: S
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _effect = Channel<Ef>(Channel.BUFFERED)
    val effect: Flow<Ef> = _effect.receiveAsFlow()

    protected val currentState: S
        get() = uiState.value

    abstract fun onEvent(event: E)

    protected fun updateState(update: (S) -> S) {
        _uiState.update(update)
    }

    protected fun sendEffect(effect: Ef) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
