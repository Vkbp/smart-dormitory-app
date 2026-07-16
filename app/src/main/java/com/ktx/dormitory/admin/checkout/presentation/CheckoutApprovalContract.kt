package com.ktx.dormitory.admin.checkout.presentation

import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.admin.common.data.dto.response.CheckoutRequestResponseDto
import java.util.UUID

data class CheckoutApprovalUiState(
    val isLoading: Boolean = false,
    val requests: List<CheckoutRequestResponseDto> = emptyList(),
    val error: String? = null,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false
) : BaseContract.State

sealed class CheckoutApprovalUiEvent : BaseContract.Event {
    data class LoadRequests(val refresh: Boolean = false) : CheckoutApprovalUiEvent()
    data class ReviewRequest(val requestId: UUID, val status: String, val reason: String?) : CheckoutApprovalUiEvent()
}

sealed class CheckoutApprovalUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : CheckoutApprovalUiEffect()
}
