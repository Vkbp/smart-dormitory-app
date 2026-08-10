package com.ktx.dormitory.admin.smartaccess.domain.usecase

import com.ktx.dormitory.admin.common.data.dto.response.BuildingResponseDto
import com.ktx.dormitory.admin.common.data.dto.response.GateResponseDto
import com.ktx.dormitory.admin.smartaccess.domain.repository.AdminRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.UUID
import javax.inject.Inject

data class SmartAccessResources(
    val buildings: List<BuildingResponseDto>,
    val gates: List<GateResponseDto>
)

class GetSmartAccessResourcesUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(): Result<SmartAccessResources> = coroutineScope {
        try {
            val buildingsDef = async { repository.getBuildings() }
            val gatesDef = async { repository.getGates() }

            val buildingsRes = buildingsDef.await()
            val gatesRes = gatesDef.await()

            if (buildingsRes.isSuccess && gatesRes.isSuccess) {
                Result.success(
                    SmartAccessResources(
                        buildings = buildingsRes.getOrDefault(emptyList()),
                        gates = gatesRes.getOrDefault(emptyList())
                    )
                )
            } else {
                val error = buildingsRes.exceptionOrNull()?.message 
                    ?: gatesRes.exceptionOrNull()?.message 
                    ?: "Không thể tải danh sách tài nguyên"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class RemoteUnlockUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(gateId: UUID, buildingId: UUID, studentId: UUID? = null) = 
        repository.remoteUnlock(gateId, buildingId, studentId)
}

class SearchStudentsUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(query: String) = repository.searchStudents(query)
}

class EmergencyOverrideUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(actionType: String, reason: String, buildingId: UUID?) = 
        repository.emergencyOverride(actionType, reason, buildingId)
}

class GetBuildingsUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke() = repository.getBuildings()
}

class GetGatesUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke() = repository.getGates()
}
