package com.ktx.dormitory.student.payment.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model giao dịch thanh toán
 */
@Parcelize
data class Transaction(
    val transactionId: String?,
    val amount: Double?,
    val method: String?,
    val status: String?,
    val createdAt: String?,
    val type: String? = null,
    val message: String? = null,
    val transactionCode: String? = null
) : Parcelable

@Parcelize
data class Invoice(
    val id: String,   // UUID from backend
    val type: InvoiceType?,
    val amount: Double?,
    val paidAmount: Double?,
    val remainingAmount: Double?,
    val status: PaymentStatus?,
    val dueDate: String?,
    val description: String?,
    val roomCode: String? = null,
    val bedCode: String? = null
) : Parcelable

/**
 * Loại hóa đơn (Đồng bộ với BillType của Backend)
 */
@Parcelize
enum class InvoiceType : Parcelable {
    ROOM, ELECTRICITY, WATER, SERVICE, APPLICATION, PENALTY, DEPOSIT
}

/**
 * Trạng thái thanh toán (Đồng bộ với BillStatus của Backend)
 */
@Parcelize
enum class PaymentStatus : Parcelable {
    UNPAID, PARTIALLY_PAID, PAID, OVERDUE, CANCELLED
}
