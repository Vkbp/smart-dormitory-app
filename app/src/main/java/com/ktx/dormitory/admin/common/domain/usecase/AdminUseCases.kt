package com.ktx.dormitory.admin.common.domain.usecase

import com.ktx.dormitory.admin.smartaccess.domain.repository.AdminRepository
import java.util.UUID
import javax.inject.Inject

class RemoteUnlockUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(gateId: UUID, buildingId: UUID) = repository.remoteUnlock(gateId, buildingId)
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
    suspend operator fun invoke(page: Int, size: Int) = repository.getStayExtensions(page, size)
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
