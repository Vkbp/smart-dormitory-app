package com.ktx.dormitory.student.maintenance.domain.usecase

import com.ktx.dormitory.student.maintenance.domain.model.MaintenanceRequest
import com.ktx.dormitory.student.maintenance.domain.repository.MaintenanceRepository
import javax.inject.Inject

class GetMaintenanceHistoryUseCase @Inject constructor(
    private val repository: MaintenanceRepository
) {
    suspend operator fun invoke(): Result<List<MaintenanceRequest>> {
        return repository.getMaintenanceHistory()
    }
}
