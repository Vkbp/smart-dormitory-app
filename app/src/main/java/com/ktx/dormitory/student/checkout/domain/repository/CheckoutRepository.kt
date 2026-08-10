package com.ktx.dormitory.student.checkout.domain.repository

import com.ktx.dormitory.student.checkout.domain.model.CheckoutRequest
import com.ktx.dormitory.student.checkout.domain.model.CheckoutResponse

interface CheckoutRepository {
    suspend fun submitCheckoutRequest(request: CheckoutRequest): Result<CheckoutResponse>
    suspend fun getCheckoutHistory(): Result<List<CheckoutResponse>>
}
