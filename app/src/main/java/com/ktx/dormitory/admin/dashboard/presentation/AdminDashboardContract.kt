package com.ktx.dormitory.admin.dashboard.presentation

import com.ktx.dormitory.admin.common.domain.model.DashboardStats
import com.ktx.dormitory.core.base.BaseContract

data class AdminDashboardUiState(
    val isLoading: Boolean = false,
    val stats: DashboardStats? = null,
    val error: String? = null
) : BaseContract.State

sealed class AdminDashboardUiEvent : BaseContract.Event {
    data object LoadStats : AdminDashboardUiEvent()
    data object Refresh : AdminDashboardUiEvent()
}

sealed class AdminDashboardUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : AdminDashboardUiEffect()
}
