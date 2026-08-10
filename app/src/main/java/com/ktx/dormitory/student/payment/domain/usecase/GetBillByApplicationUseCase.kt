package com.ktx.dormitory.student.payment.domain.usecase

import com.ktx.dormitory.student.payment.domain.model.Bill
import com.ktx.dormitory.student.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class GetBillByApplicationUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(applicationId: String): Result<Bill> {
        return repository.getBillByApplication(applicationId)
    }
}
