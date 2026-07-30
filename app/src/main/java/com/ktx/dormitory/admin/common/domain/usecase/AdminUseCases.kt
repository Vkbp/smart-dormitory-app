package com.ktx.dormitory.admin.common.domain.usecase

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

class GetPendingFaceProfilesUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(page: Int, size: Int) = repository.getPendingFaceProfiles(page, size)
}

class ApproveFaceUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(profileId: UUID) = repository.approveFace(profileId)
}

class RejectFaceUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(profileId: UUID, reason: String) = repository.rejectFace(profileId, reason)
}

class RevokeFaceUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(profileId: UUID, reason: String) = repository.revokeFace(profileId, reason)
}

class ApproveReplacementUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(profileId: UUID) = repository.approveReplacement(profileId)
}

class RejectReplacementUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(profileId: UUID, reason: String) = repository.rejectReplacement(profileId, reason)
}

class GetCheckoutRequestsUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(status: String?, page: Int, size: Int) = repository.getCheckoutRequests(status, page, size)
}

class ReviewCheckoutRequestUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(requestId: UUID, status: String, rejectReason: String?) = 
        repository.reviewCheckoutRequest(requestId, status, rejectReason)
}

class GetStayExtensionsUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(status: String?, page: Int, size: Int) = repository.getStayExtensions(status, page, size)
}

class ReviewStayExtensionUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(id: UUID, status: String, rejectReason: String?) = 
        repository.reviewStayExtension(id, status, rejectReason)
}

class SearchStudentForCheckInUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(cccd: String) = repository.searchStudentForCheckIn(cccd)
}

class ConfirmCheckInUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(assignmentId: UUID) = repository.confirmCheckIn(assignmentId)
}

class BroadcastNotificationUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(title: String, message: String, targetAudience: String) = 
        repository.broadcastNotification(title, message, targetAudience)
}

class AssignRfidUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(studentId: UUID, rfidCode: String) = 
        repository.assignRfid(studentId, rfidCode)
}

class GetBuildingsUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke() = repository.getBuildings()
}

class GetGatesUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke() = repository.getGates()
}

class GetDashboardStatsUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke() = repository.getDashboardStats()
}

class GetDetailedStudentProfileUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(studentId: UUID) = repository.getStudentProfile(studentId)
}
