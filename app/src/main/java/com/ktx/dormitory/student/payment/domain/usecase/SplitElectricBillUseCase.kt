package com.ktx.dormitory.student.payment.domain.usecase

import com.ktx.dormitory.student.payment.domain.repository.PaymentRepository
import java.math.BigDecimal
import javax.inject.Inject

class SplitElectricBillUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(
        billId: String,
        nonPayingStudentIds: List<String>,
        amountPerStudent: BigDecimal
    ): Result<Unit> {
        return repository.splitElectricBill(billId, nonPayingStudentIds, amountPerStudent)
    }
}
