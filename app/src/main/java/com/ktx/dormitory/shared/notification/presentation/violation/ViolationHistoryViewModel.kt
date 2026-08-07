package com.ktx.dormitory.shared.notification.presentation.violation

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.shared.notification.domain.model.NotificationType
import com.ktx.dormitory.shared.notification.domain.usecase.GetViolationsUseCase
import com.ktx.dormitory.shared.notification.domain.usecase.MarkNotificationReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViolationHistoryViewModel @Inject constructor(
    private val getViolationsUseCase: GetViolationsUseCase,
    private val markReadUseCase: MarkNotificationReadUseCase
) : BaseViewModel<ViolationHistoryUiState, ViolationHistoryUiEvent, ViolationHistoryUiEffect>(
    ViolationHistoryUiState()
) {

    init {
        loadViolations()
    }

    override fun onEvent(event: ViolationHistoryUiEvent) {
        when (event) {
            ViolationHistoryUiEvent.LoadViolations -> loadViolations()
            ViolationHistoryUiEvent.Refresh -> loadViolations()
            is ViolationHistoryUiEvent.MarkAsRead -> markAsRead(event.id)
        }
    }

    private fun loadViolations() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            getViolationsUseCase()
                .onSuccess { list ->
                    updateState { it.copy(violations = list, isLoading = false) }
                }
                .onFailure { e ->
                    updateState { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    private fun markAsRead(id: Long) {
        viewModelScope.launch {
            markReadUseCase(id)
        }
    }
}
