package com.ktx.dormitory.student.payment.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentInstruction(
    val bankName: String?,
    val accountNumber: String?,
    val accountHolder: String?,
    val qrCodeUrl: String?,
    val contentPrefix: String?,
    val amount: java.math.BigDecimal?
) : Parcelable
