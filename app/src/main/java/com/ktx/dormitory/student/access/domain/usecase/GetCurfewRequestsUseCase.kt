package com.ktx.dormitory.student.access.domain.usecase

import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.student.access.domain.model.CurfewRequest
import com.ktx.dormitory.student.access.domain.repository.AccessRepository
import javax.inject.Inject

class GetCurfewRequestsUseCase @Inject constructor(
    private val repository: AccessRepository
) {
    suspend operator fun invoke(page: Int = 0, size: Int = 20): Result<PageResponse<CurfewRequest>> {
        return repository.getMyCurfewRequests(page, size)
    }
}
