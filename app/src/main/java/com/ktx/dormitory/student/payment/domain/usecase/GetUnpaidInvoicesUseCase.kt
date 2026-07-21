package com.ktx.dormitory.student.payment.domain.usecase

import com.ktx.dormitory.student.payment.domain.model.Bill
import com.ktx.dormitory.student.payment.domain.model.BillStatus
import com.ktx.dormitory.student.payment.domain.repository.PaymentRepository
import javax.inject.Inject

data class UnpaidBillsResult(
    val bills: List<Bill>,
    val totalAmount: Double
)

class GetUnpaidInvoicesUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(): Result<UnpaidBillsResult> {
        return repository.getInvoices().map { list ->
            val unpaid = list.filter { it.status != BillStatus.PAID && it.status != BillStatus.CANCELLED }
            val total = unpaid.sumOf { it.remainingAmount ?: it.amount ?: 0.0 }
            UnpaidBillsResult(unpaid, total)
        }
    }
}
