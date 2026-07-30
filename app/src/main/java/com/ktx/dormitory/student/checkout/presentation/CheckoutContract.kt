package com.ktx.dormitory.student.checkout.presentation

import android.os.Parcelable
import com.ktx.dormitory.student.checkout.domain.model.CheckoutResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckoutUiState(
    val isLoading: Boolean = false,
    val history: List<CheckoutResponse> = emptyList(),
    val hasPendingRequest: Boolean = false,
    val hasUnpaidBills: Boolean = false,
    val isResident: Boolean = true,
    val submitSuccess: Boolean = false,
    val error: String? = null,
    val debtErrorMessage: String? = null
) : Parcelable

sealed interface CheckoutUiEvent {
    data class Submit(
        val intendedDate: String,
        val reason: String,
        val bankAccount: String,
        val bankName: String
    ) : CheckoutUiEvent
    data object FetchHistory : CheckoutUiEvent
    data object ClearStatus : CheckoutUiEvent
}
