package com.ktx.dormitory.admin.face.domain.usecase

import com.ktx.dormitory.admin.smartaccess.domain.repository.AdminRepository
import java.util.UUID
import javax.inject.Inject

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
