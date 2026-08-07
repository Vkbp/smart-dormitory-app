package com.ktx.dormitory.admin.smartaccess.domain.repository

import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.admin.common.data.dto.response.*
import com.ktx.dormitory.admin.common.domain.model.DashboardStats
import com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto
import java.util.UUID

interface AdminRepository {
    suspend fun remoteUnlock(gateId: UUID, buildingId: UUID, studentId: UUID? = null): Result<Unit>
    suspend fun emergencyOverride(actionType: String, reason: String, buildingId: UUID?): Result<Unit>

    suspend fun searchStudents(query: String): Result<List<com.ktx.dormitory.shared.profile.data.dto.response.StudentResponse>>
    
    suspend fun getPendingFaceProfiles(page: Int, size: Int): Result<PageResponse<FaceProfileDto>>
    suspend fun approveFace(profileId: UUID): Result<String>
    suspend fun rejectFace(profileId: UUID, reason: String): Result<String>
    suspend fun revokeFace(profileId: UUID, reason: String): Result<String>
    suspend fun approveReplacement(profileId: UUID): Result<String>
    suspend fun rejectReplacement(profileId: UUID, reason: String): Result<String>
    
    suspend fun getCheckoutRequests(status: String?, page: Int, size: Int): Result<PageResponse<CheckoutRequestResponseDto>>
    suspend fun reviewCheckoutRequest(requestId: UUID, status: String, rejectReason: String?): Result<CheckoutRequestResponseDto>
    
    suspend fun getStayExtensions(status: String?, page: Int, size: Int): Result<PageResponse<StayExtensionResponseDto>>
    suspend fun reviewStayExtension(id: UUID, status: String, rejectReason: String?): Result<StayExtensionResponseDto>
    
    suspend fun searchStudentForCheckIn(cccd: String): Result<CheckInSearchResponseDto>
    suspend fun confirmCheckIn(assignmentId: UUID): Result<String>
    
    suspend fun broadcastNotification(title: String, message: String, targetAudience: String): Result<BroadcastResponse>
    suspend fun assignRfid(studentId: UUID, rfidCode: String): Result<String>

    suspend fun getBuildings(): Result<List<BuildingResponseDto>>
    suspend fun getGates(): Result<List<GateResponseDto>>
    
    suspend fun getDashboardStats(): Result<DashboardStats>

    suspend fun getStudentProfile(studentId: UUID): Result<com.ktx.dormitory.shared.profile.domain.model.UserProfile>

    suspend fun getAdminAccessHistory(page: Int, size: Int): Result<PageResponse<com.ktx.dormitory.student.access.data.dto.response.AccessLogDto>>
}
