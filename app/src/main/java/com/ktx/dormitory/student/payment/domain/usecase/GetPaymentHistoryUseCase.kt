package com.ktx.dormitory.student.payment.domain.usecase

import com.ktx.dormitory.student.payment.domain.model.Transaction
import com.ktx.dormitory.student.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class GetPaymentHistoryUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(): Result<List<Transaction>> {
        return paymentRepository.getPaymentHistory()
    }
}
