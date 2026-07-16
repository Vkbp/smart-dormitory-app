package com.ktx.dormitory.student.payment.domain.usecase

import com.ktx.dormitory.student.payment.domain.model.PaymentInstruction
import com.ktx.dormitory.student.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class GetPaymentInstructionUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(): Result<PaymentInstruction> {
        return repository.getPaymentInstructions()
    }
}
