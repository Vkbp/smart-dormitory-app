package com.ktx.dormitory.student.checkout.domain.usecase

import com.ktx.dormitory.student.checkout.domain.model.CheckoutResponse
import com.ktx.dormitory.student.checkout.domain.repository.CheckoutRepository
import javax.inject.Inject

class GetCheckoutHistoryUseCase @Inject constructor(
    private val repository: CheckoutRepository
) {
    suspend operator fun invoke(): Result<List<CheckoutResponse>> {
        return repository.getCheckoutHistory()
    }
}
