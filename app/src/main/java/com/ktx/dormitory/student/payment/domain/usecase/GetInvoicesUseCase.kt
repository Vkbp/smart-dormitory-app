package com.ktx.dormitory.student.payment.domain.usecase

import com.ktx.dormitory.student.payment.domain.model.Bill
import com.ktx.dormitory.student.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class GetInvoicesUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(): Result<List<Bill>> {
        return paymentRepository.getInvoices()
    }
}
