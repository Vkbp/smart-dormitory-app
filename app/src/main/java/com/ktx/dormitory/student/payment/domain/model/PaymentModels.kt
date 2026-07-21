package com.ktx.dormitory.student.payment.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model đại diện cho Hóa đơn (Bill).
 * Đồng bộ với tài liệu payment_mobile_api.md
 */
@Parcelize
data class Bill(
    val id: String,
    val type: BillType?,
    val amount: Double?,
    val paidAmount: Double? = 0.0,
    val remainingAmount: Double? = 0.0,
    val status: BillStatus?,
    val dueDate: String?,
    val description: String?,
    val assignmentId: String? = null,
    val roomCode: String? = null,
    val bedCode: String? = null
) : Parcelable

/**
 * Model đại diện cho kết quả tạo thanh toán Online.
 */
@Parcelize
data class PaymentResult(
    val paymentId: String?,
    val billId: String?,
    val transactionCode: String?,
    val amount: Double?,
    val paymentMethod: PaymentMethod?,
    val paymentStatus: String?,
    val paymentUrl: String?,
    val paidAt: String? = null
) : Parcelable

/**
 * Loại hóa đơn (BillType).
 */
@Parcelize
enum class BillType : Parcelable {
    APPLICATION_FEE, ACCOMMODATION_FEE, ELECTRIC_FEE, WATER_FEE, PENALTY_FEE, DEPOSIT_FEE
}

/**
 * Trạng thái hóa đơn (BillStatus).
 */
@Parcelize
enum class BillStatus : Parcelable {
    UNPAID, PARTIALLY_PAID, PAID, OVERDUE, CANCELLED
}

/**
 * Phương thức thanh toán (PaymentMethod).
 */
@Parcelize
enum class PaymentMethod : Parcelable {
    BANK_TRANSFER, CASH
}
