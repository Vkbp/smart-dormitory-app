package com.ktx.dormitory.student.access.domain.usecase

import com.ktx.dormitory.student.access.domain.model.CurfewRequest
import com.ktx.dormitory.student.access.domain.repository.AccessRepository
import javax.inject.Inject

class SubmitCurfewRequestUseCase @Inject constructor(
    private val repository: AccessRepository
) {
    suspend operator fun invoke(reason: String, expectedArrivalTime: String, note: String?): Result<CurfewRequest> {
        return repository.submitCurfewRequest(reason, expectedArrivalTime, note)
    }
}
