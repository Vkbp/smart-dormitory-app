package com.ktx.dormitory.student.payment.domain.usecase

import com.ktx.dormitory.student.payment.domain.model.PaymentResult
import com.ktx.dormitory.student.payment.domain.repository.PaymentRepository
import java.math.BigDecimal
import javax.inject.Inject

class CreateSmartQRUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(billId: String, amount: BigDecimal): Result<PaymentResult> {
        return repository.createSmartQR(billId, amount)
    }
}
