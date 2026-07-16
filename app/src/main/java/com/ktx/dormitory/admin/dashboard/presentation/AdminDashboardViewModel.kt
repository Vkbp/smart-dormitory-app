package com.ktx.dormitory.admin.dashboard.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.admin.common.domain.usecase.GetDashboardStatsUseCase
import com.ktx.dormitory.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase,
) : BaseViewModel<AdminDashboardUiState, AdminDashboardUiEvent, AdminDashboardUiEffect>(AdminDashboardUiState()) {

    init {
        onEvent(AdminDashboardUiEvent.LoadStats)
    }

    override fun onEvent(event: AdminDashboardUiEvent) {
        when (event) {
            AdminDashboardUiEvent.LoadStats -> loadStats()
            AdminDashboardUiEvent.Refresh -> loadStats()
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            getDashboardStatsUseCase()
                .onSuccess { stats ->
                    updateState { it.copy(isLoading = false, stats = stats) }
                }
                .onFailure { error ->
                    updateState { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
}
