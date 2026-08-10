package com.ktx.dormitory.student.checkout.domain.usecase

import com.ktx.dormitory.student.checkout.domain.model.CheckoutRequest
import com.ktx.dormitory.student.checkout.domain.model.CheckoutResponse
import com.ktx.dormitory.student.checkout.domain.repository.CheckoutRepository
import javax.inject.Inject

class SubmitCheckoutRequestUseCase @Inject constructor(
    private val repository: CheckoutRepository
) {
    suspend operator fun invoke(request: CheckoutRequest): Result<CheckoutResponse> {
        return repository.submitCheckoutRequest(request)
    }
}
