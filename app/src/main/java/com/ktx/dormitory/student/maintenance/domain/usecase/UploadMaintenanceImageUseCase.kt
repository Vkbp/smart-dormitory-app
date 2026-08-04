package com.ktx.dormitory.student.maintenance.domain.usecase

import com.ktx.dormitory.student.maintenance.domain.repository.MaintenanceRepository
import javax.inject.Inject

class UploadMaintenanceImageUseCase @Inject constructor(
    private val repository: MaintenanceRepository
) {
    suspend operator fun invoke(filePath: String): Result<String> {
        return repository.uploadImage(filePath)
    }
}
