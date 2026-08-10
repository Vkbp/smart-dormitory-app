package com.ktx.dormitory.admin.checkout.domain.usecase

import com.ktx.dormitory.admin.smartaccess.domain.repository.AdminRepository
import java.util.UUID
import javax.inject.Inject

class GetCheckoutRequestsUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(status: String?, page: Int, size: Int) = repository.getCheckoutRequests(status, page, size)
}

class ReviewCheckoutRequestUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(requestId: UUID, status: String, rejectReason: String?) = 
        repository.reviewCheckoutRequest(requestId, status, rejectReason)
}
