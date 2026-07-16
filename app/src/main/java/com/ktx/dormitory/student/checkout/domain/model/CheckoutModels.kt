package com.ktx.dormitory.student.checkout.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckoutRequest(
    val intendedCheckoutDate: String,
    val reason: String,
    val bankAccountNumber: String,
    val bankName: String
) : Parcelable

@Parcelize
data class CheckoutResponse(
    val requestId: String,
    val roomCode: String?,
    val bedCode: String?,
    val intendedCheckoutDate: String,
    val status: String,
    val rejectReason: String?
) : Parcelable
