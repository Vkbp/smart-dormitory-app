package com.ktx.dormitory.student.payment.presentation

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ktx.dormitory.student.payment.domain.model.Bill
import com.ktx.dormitory.student.payment.domain.usecase.GetPaymentHistoryPagingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class PaymentHistoryUiState(
    val isLoading: Boolean = false,
    val bills: List<Bill> = emptyList(),
    val error: String? = null
) : Parcelable

@HiltViewModel
class PaymentHistoryViewModel @Inject constructor(
    private val getPaymentHistoryPagingUseCase: GetPaymentHistoryPagingUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val uiState: StateFlow<PaymentHistoryUiState> = savedStateHandle.getStateFlow("uiState", PaymentHistoryUiState())

    val pagingFlow: Flow<PagingData<Bill>> = getPaymentHistoryPagingUseCase()
        .cachedIn(viewModelScope)

    private fun updateUiState(reducer: (PaymentHistoryUiState) -> PaymentHistoryUiState) {
        savedStateHandle["uiState"] = reducer(uiState.value)
    }

    fun loadPaymentHistory() {
        // Paging handle loading state internally, this is for non-paging if needed
    }
}
