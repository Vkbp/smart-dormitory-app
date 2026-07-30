package com.ktx.dormitory.student.checkout.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.student.checkout.domain.model.CheckoutRequest
import com.ktx.dormitory.student.checkout.domain.usecase.GetCheckoutHistoryUseCase
import com.ktx.dormitory.student.checkout.domain.usecase.SubmitCheckoutRequestUseCase
import com.ktx.dormitory.student.payment.domain.model.BillStatus
import com.ktx.dormitory.student.payment.domain.usecase.GetInvoicesUseCase
import com.ktx.dormitory.student.room.domain.usecase.GetRoomInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val submitUseCase: SubmitCheckoutRequestUseCase,
    private val getHistoryUseCase: GetCheckoutHistoryUseCase,
    private val getInvoicesUseCase: GetInvoicesUseCase,
    private val getRoomInfoUseCase: GetRoomInfoUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val uiState: StateFlow<CheckoutUiState> = savedStateHandle.getStateFlow("uiState", CheckoutUiState())

    private fun updateUiState(reducer: (CheckoutUiState) -> CheckoutUiState) {
        savedStateHandle["uiState"] = reducer(uiState.value)
    }

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Reset trạng thái để tránh dùng nhầm dữ liệu của tài khoản trước đó
            updateUiState { it.copy(isLoading = true, error = null, isResident = true) }
            try {
                // 1. Kiểm tra đơn PENDING
                val historyResult = getHistoryUseCase()
                // 2. Kiểm tra nợ hóa đơn
                val billsResult = getInvoicesUseCase()

                val history = historyResult.getOrDefault(emptyList())
                val hasPending = history.any { it.status.uppercase() == "PENDING" }
                
                val bills = billsResult.getOrDefault(emptyList())
                val hasUnpaid = bills.any { it.status == BillStatus.OVERDUE }

                // 3. Kiểm tra thông tin phòng hiện tại
                val roomResult = getRoomInfoUseCase()
                val isResident = roomResult.isSuccess && roomResult.getOrNull()?.roomCode != null

                updateUiState { 
                    it.copy(
                        isLoading = false, 
                        history = history, 
                        hasPendingRequest = hasPending,
                        hasUnpaidBills = hasUnpaid,
                        isResident = isResident
                    ) 
                }
            } catch (e: Exception) {
                updateUiState { it.copy(isLoading = false, error = "Lỗi kết nối máy chủ") }
            }
        }
    }

    fun onEvent(event: CheckoutUiEvent) {
        when (event) {
            is CheckoutUiEvent.Submit -> submitRequest(event)
            CheckoutUiEvent.FetchHistory -> loadInitialData()
            CheckoutUiEvent.ClearStatus -> updateUiState { it.copy(submitSuccess = false, error = null, debtErrorMessage = null) }
        }
    }

    private fun submitRequest(event: CheckoutUiEvent.Submit) {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true, error = null, debtErrorMessage = null) }
            try {
                val request = CheckoutRequest(
                    intendedCheckoutDate = event.intendedDate,
                    reason = event.reason,
                    bankAccountNumber = event.bankAccount,
                    bankName = event.bankName
                )
                submitUseCase(request)
                    .onSuccess {
                        updateUiState { it.copy(submitSuccess = true) }
                        loadInitialData()
                    }
                    .onFailure { e ->
                        val errorMsg = e.message ?: "Gửi yêu cầu thất bại"
                        if (errorMsg.contains("hóa đơn", ignoreCase = true) || errorMsg.contains("thanh toán", ignoreCase = true)) {
                            updateUiState { it.copy(debtErrorMessage = errorMsg) }
                        } else {
                            updateUiState { it.copy(error = errorMsg) }
                        }
                    }
            } finally {
                updateUiState { it.copy(isLoading = false) }
            }
        }
    }
}
