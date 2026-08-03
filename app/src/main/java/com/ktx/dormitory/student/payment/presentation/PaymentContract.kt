package com.ktx.dormitory.student.payment.presentation

import android.os.Parcelable
import com.ktx.dormitory.student.payment.domain.model.Bill
import com.ktx.dormitory.student.payment.domain.model.PaymentResult
import com.ktx.dormitory.student.room.domain.model.Roommate
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

sealed class PaymentUiState : Parcelable {
    @Parcelize
    data object Loading : PaymentUiState()
    
    @Parcelize
    data class Success(
        val bills: List<Bill>,
        val totalUnpaid: BigDecimal,
        val isProcessing: Boolean = false,
        val smartQR: PaymentResult? = null, // Thông tin QR thông minh nếu đang mở
        val selectedBill: Bill? = null,
        val roommates: List<Roommate>? = null,
        val isSplitBillLoading: Boolean = false
    ) : PaymentUiState()
    
    @Parcelize
    data class Error(val message: String) : PaymentUiState()
}

sealed class PaymentUiEffect {
    data class ShowToast(val message: String) : PaymentUiEffect()
    data object NavigateToSuccess : PaymentUiEffect()
}
