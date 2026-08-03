package com.ktx.dormitory.student.payment.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

/**
 * Model đại diện cho Hóa đơn (Bill).
 * Đồng bộ với tài liệu payment_mobile_api.md
 */
@Parcelize
data class Bill(
    val id: String,
    val billCode: String?,
    val type: BillType?,
    val amount: BigDecimal?,
    val paidAmount: BigDecimal? = BigDecimal.ZERO,
    val remainingAmount: BigDecimal? = BigDecimal.ZERO,
    val status: BillStatus?,
    val dueDate: String?,
    val description: String?,
    val assignmentId: String? = null,
    val billStatus: String? = null,
    val assignmentStatus: String? = null,
    val message: String? = null,
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
    val billCode: String? = null,
    val transactionCode: String?,
    val amount: BigDecimal?,
    val paymentMethod: PaymentMethod?,
    val paymentStatus: String?,
    val paymentUrl: String?,
    val paidAt: String? = null,
    val billStatus: String? = null,
    val assignmentStatus: String? = null,
    val paidAmount: BigDecimal? = null,
    val message: String? = null
) : Parcelable

/**
 * Loại hóa đơn (BillType).
 */
@Parcelize
enum class BillType : Parcelable {
    APPLICATION_FEE, ACCOMMODATION_FEE, ELECTRIC_FEE, PENALTY_FEE, DEPOSIT_FEE
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
