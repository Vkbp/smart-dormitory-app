package com.ktx.dormitory.student.payment.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.student.payment.domain.model.BillStatus
import com.ktx.dormitory.student.payment.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getUnpaidInvoicesUseCase: GetUnpaidInvoicesUseCase,
    private val createSmartQRUseCase: CreateSmartQRUseCase,
    private val getBillByApplicationUseCase: GetBillByApplicationUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val uiState: StateFlow<PaymentUiState> = savedStateHandle.getStateFlow("uiState", PaymentUiState.Loading)

    private val _uiEffect = MutableSharedFlow<PaymentUiEffect>()
    val uiEffect: SharedFlow<PaymentUiEffect> = _uiEffect.asSharedFlow()

    private var pollingJob: Job? = null

    init {
        loadInvoices()
    }

    private fun updateUiState(newState: PaymentUiState) {
        savedStateHandle["uiState"] = newState
    }

    fun loadInvoices() {
        viewModelScope.launch {
            updateUiState(PaymentUiState.Loading)
            getUnpaidInvoicesUseCase()
                .onSuccess { result ->
                    updateUiState(PaymentUiState.Success(result.bills, result.totalAmount))
                }
                .onFailure { e ->
                    updateUiState(PaymentUiState.Error(e.message ?: "Lỗi tải hóa đơn"))
                }
        }
    }

    /**
     * Tạo mã QR thông minh và bắt đầu Polling để kiểm tra trạng thái thanh toán.
     */
    fun createSmartQR(billId: String, amount: BigDecimal) {
        viewModelScope.launch {
            val currentState = uiState.value
            if (currentState is PaymentUiState.Success) {
                updateUiState(currentState.copy(isProcessing = true))
                createSmartQRUseCase(billId, amount)
                    .onSuccess { result ->
                        updateUiState(currentState.copy(isProcessing = false, smartQR = result))
                        startPolling(billId)
                    }
                    .onFailure { e ->
                        updateUiState(currentState.copy(isProcessing = false))
                        _uiEffect.emit(PaymentUiEffect.ShowToast(e.message ?: "Không thể tạo mã QR"))
                    }
            }
        }
    }

    /**
     * Polling mỗi 5 giây để kiểm tra xem hóa đơn đã được thanh toán chưa.
     * Dừng lại sau 15 phút hoặc khi đã thanh toán thành công.
     */
    private fun startPolling(billId: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val timeout = 15 * 60 * 1000 // 15 phút

            while (System.currentTimeMillis() - startTime < timeout) {
                delay(5000) // Poll mỗi 5 giây
                
                // Sử dụng getInvoices hoặc endpoint chi tiết bill để check status
                getUnpaidInvoicesUseCase().onSuccess { result ->
                    val bill = result.bills.find { it.id == billId }
                    // Nếu không thấy bill trong list unpaid nữa, hoặc status là PAID
                    if (bill == null || bill.status == BillStatus.PAID) {
                        onPaymentSuccess()
                        return@launch
                    }
                }
            }
            // Hết thời gian polling mà chưa thành công
            stopPolling()
        }
    }

    private fun onPaymentSuccess() {
        stopPolling()
        viewModelScope.launch {
            _uiEffect.emit(PaymentUiEffect.NavigateToSuccess)
        }
        loadInvoices()
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
        val currentState = uiState.value
        if (currentState is PaymentUiState.Success) {
            updateUiState(currentState.copy(smartQR = null))
        }
    }

    fun dismissError() {
        loadInvoices()
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
