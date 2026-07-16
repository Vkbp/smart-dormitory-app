package com.ktx.dormitory.student.payment.presentation

import android.os.Parcelable
import com.ktx.dormitory.student.payment.domain.model.Invoice
import kotlinx.parcelize.Parcelize

sealed class PaymentUiState : Parcelable {
    @Parcelize
    data object Loading : PaymentUiState()
    
    @Parcelize
    data class Success(
        val invoices: List<Invoice>,
        val totalUnpaid: Double,
        val isVerifying: Boolean = false
    ) : PaymentUiState()
    
    @Parcelize
    data class Error(val message: String) : PaymentUiState()
}
