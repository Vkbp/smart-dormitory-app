package com.ktx.dormitory.admin.checkout.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.admin.common.domain.usecase.GetCheckoutRequestsUseCase
import com.ktx.dormitory.admin.common.domain.usecase.ReviewCheckoutRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CheckoutApprovalViewModel @Inject constructor(
    private val getRequestsUseCase: GetCheckoutRequestsUseCase,
    private val reviewUseCase: ReviewCheckoutRequestUseCase
) : BaseViewModel<CheckoutApprovalUiState, CheckoutApprovalUiEvent, CheckoutApprovalUiEffect>(CheckoutApprovalUiState()) {

    init {
        onEvent(CheckoutApprovalUiEvent.LoadRequests(refresh = true))
    }

    override fun onEvent(event: CheckoutApprovalUiEvent) {
        when (event) {
            is CheckoutApprovalUiEvent.LoadRequests -> loadRequests(event.refresh)
            is CheckoutApprovalUiEvent.ChangeStatus -> {
                updateState { it.copy(selectedStatus = event.status) }
                onEvent(CheckoutApprovalUiEvent.LoadRequests(refresh = true))
            }
            is CheckoutApprovalUiEvent.ReviewRequest -> review(event.requestId, event.status, event.reason)
        }
    }

    private fun loadRequests(refresh: Boolean) {
        viewModelScope.launch {
            if (refresh) updateState { it.copy(isLoading = true, requests = emptyList(), currentPage = 0, isLastPage = false) }
            
            val page = if (refresh) 0 else currentState.currentPage + 1
            val status = if (currentState.selectedStatus == "ALL") null else currentState.selectedStatus
            
            getRequestsUseCase(status, page, 15).onSuccess { response ->
                updateState { state ->
                    val newList = if (refresh) (response.content ?: emptyList()) else state.requests + (response.content ?: emptyList())
                    state.copy(
                        requests = newList,
                        currentPage = page,
                        isLastPage = page >= response.totalPages - 1,
                        isLoading = false
                    )
                }
            }.onFailure { e ->
                updateState { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun review(requestId: UUID, status: String, reason: String?) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            reviewUseCase(requestId, status, reason).onSuccess {
                sendEffect(CheckoutApprovalUiEffect.ShowToast("Đã xử lý yêu cầu"))
                loadRequests(refresh = true)
            }.onFailure { e ->
                updateState { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
