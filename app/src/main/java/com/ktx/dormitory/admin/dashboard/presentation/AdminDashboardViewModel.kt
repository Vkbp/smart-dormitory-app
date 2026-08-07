package com.ktx.dormitory.admin.dashboard.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.admin.dashboard.domain.usecase.GetDashboardStatsUseCase
import com.ktx.dormitory.admin.face.domain.usecase.GetPendingFaceProfilesUseCase
import com.ktx.dormitory.admin.checkout.domain.usecase.GetCheckoutRequestsUseCase
import com.ktx.dormitory.admin.extension.domain.usecase.GetStayExtensionsUseCase
import com.ktx.dormitory.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase,
    private val getPendingFaceProfilesUseCase: GetPendingFaceProfilesUseCase,
    private val getCheckoutRequestsUseCase: GetCheckoutRequestsUseCase,
    private val getStayExtensionsUseCase: GetStayExtensionsUseCase,
) : BaseViewModel<AdminDashboardUiState, AdminDashboardUiEvent, AdminDashboardUiEffect>(AdminDashboardUiState()) {

    private var pollingJob: Job? = null

    init {
        onEvent(AdminDashboardUiEvent.LoadStats)
        startPolling()
    }

    override fun onEvent(event: AdminDashboardUiEvent) {
        when (event) {
            AdminDashboardUiEvent.LoadStats -> loadStats(showLoading = true)
            AdminDashboardUiEvent.Refresh -> loadStats(showLoading = true)
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(30000) // 30 giây
                loadStats(showLoading = false)
            }
        }
    }

    private fun loadStats(showLoading: Boolean) {
        viewModelScope.launch {
            if (showLoading) {
                updateState { it.copy(isLoading = true, error = null) }
            }
            
            // Gọi song song các API để lấy số lượng hồ sơ chờ xử lý cho Mobile
            val statsDef = async { getDashboardStatsUseCase() }
            val facesDef = async { getPendingFaceProfilesUseCase(0, 1) }
            val checkoutsDef = async { getCheckoutRequestsUseCase("PENDING", 0, 1) }
            val extensionsDef = async { getStayExtensionsUseCase("PENDING", 0, 1) }


            val statsRes = statsDef.await()
            val facesRes = facesDef.await()
            val checkoutsRes = checkoutsDef.await()
            val extensionsRes = extensionsDef.await()

            updateState { state ->
                state.copy(
                    isLoading = false,
                    stats = statsRes.getOrNull() ?: state.stats,
                    pendingFaces = facesRes.getOrNull()?.totalElements?.toInt() ?: state.pendingFaces,
                    pendingCheckouts = checkoutsRes.getOrNull()?.totalElements?.toInt() ?: state.pendingCheckouts,
                    // Lọc thực tế số lượng PENDING từ content trả về để khớp với UI màn hình list
                    pendingExtensions = extensionsRes.getOrNull()?.let { response ->
                        response.content?.count { it.status.uppercase() == "PENDING" }
                    } ?: state.pendingExtensions
                )
            }

            if (statsRes.isFailure && showLoading) {
                updateState { it.copy(error = statsRes.exceptionOrNull()?.message) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
