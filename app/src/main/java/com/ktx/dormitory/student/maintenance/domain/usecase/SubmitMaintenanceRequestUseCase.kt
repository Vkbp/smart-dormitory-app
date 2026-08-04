package com.ktx.dormitory.student.maintenance.domain.usecase

import com.ktx.dormitory.student.maintenance.domain.repository.MaintenanceRepository
import javax.inject.Inject

class SubmitMaintenanceRequestUseCase @Inject constructor(
    private val repository: MaintenanceRepository
) {
    suspend operator fun invoke(description: String, imageUrl: String? = null): Result<Unit> {
        return repository.submitMaintenanceRequest(description, imageUrl)
    }
}
