package com.ktx.dormitory.student.maintenance.domain.repository

import com.ktx.dormitory.student.maintenance.domain.model.MaintenanceRequest

interface MaintenanceRepository {
    suspend fun submitMaintenanceRequest(description: String, imageUrl: String?): Result<Unit>
    suspend fun getMaintenanceHistory(): Result<List<MaintenanceRequest>>
    suspend fun uploadImage(filePath: String): Result<String>
}
