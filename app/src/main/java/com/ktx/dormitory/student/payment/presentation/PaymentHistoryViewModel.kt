package com.ktx.dormitory.student.payment.presentation

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.student.payment.domain.model.Transaction
import com.ktx.dormitory.student.payment.domain.usecase.GetPaymentHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class PaymentHistoryUiState(
    val isLoading: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val error: String? = null
) : Parcelable

@HiltViewModel
class PaymentHistoryViewModel @Inject constructor(
    private val getPaymentHistoryUseCase: GetPaymentHistoryUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val uiState: StateFlow<PaymentHistoryUiState> = savedStateHandle.getStateFlow("uiState", PaymentHistoryUiState())

    private fun updateUiState(reducer: (PaymentHistoryUiState) -> PaymentHistoryUiState) {
        savedStateHandle["uiState"] = reducer(uiState.value)
    }

    init {
        if (uiState.value.transactions.isEmpty()) {
            loadPaymentHistory()
        }
    }

    fun loadPaymentHistory() {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true, error = null) }
            getPaymentHistoryUseCase()
                .onSuccess { list ->
                    updateUiState { it.copy(isLoading = false, transactions = list) }
                }
                .onFailure { e ->
                    updateUiState { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
