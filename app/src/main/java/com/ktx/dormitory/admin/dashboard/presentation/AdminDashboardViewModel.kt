package com.ktx.dormitory.admin.dashboard.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.admin.dashboard.domain.usecase.GetDashboardStatsUseCase
import com.ktx.dormitory.admin.face.domain.usecase.GetPendingFaceProfilesUseCase
import com.ktx.dormitory.admin.checkout.domain.usecase.GetCheckoutRequestsUseCase
import com.ktx.dormitory.admin.extension.domain.usecase.GetStayExtensionsUseCase
import com.ktx.dormitory.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.ktx.dormitory.core.util.GlobalEvent
import com.ktx.dormitory.core.util.GlobalEventBus
import com.ktx.dormitory.core.network.toUserFriendlyMessage
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
            try {
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
                        pendingExtensions = extensionsRes.getOrNull()?.totalElements?.toInt() ?: state.pendingExtensions
                    )
                }

                if (statsRes.isFailure && showLoading) {
                    updateState { it.copy(error = statsRes.exceptionOrNull()?.message) }
                }
            } catch (e: Exception) {
                if (showLoading) {
                    updateState { it.copy(isLoading = false, error = e.toUserFriendlyMessage()) }
                }
                if (e is java.net.ConnectException || e is java.net.UnknownHostException) {
                    GlobalEventBus.emit(GlobalEvent.NetworkError(e.toUserFriendlyMessage()))
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
