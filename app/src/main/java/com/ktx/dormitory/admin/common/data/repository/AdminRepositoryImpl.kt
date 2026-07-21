package com.ktx.dormitory.admin.common.data.repository

import com.google.gson.Gson
import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.core.network.toUserFriendlyMessage
import com.ktx.dormitory.admin.common.data.remote.AdminApiService
import com.ktx.dormitory.admin.common.data.dto.request.*
import com.ktx.dormitory.admin.common.data.dto.response.*
import com.ktx.dormitory.admin.common.domain.model.DashboardStats
import com.ktx.dormitory.admin.common.data.mapper.toDomain
import com.ktx.dormitory.admin.smartaccess.domain.repository.AdminRepository
import com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto
import com.ktx.dormitory.shared.profile.data.mapper.toDomain
import retrofit2.HttpException
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val apiService: AdminApiService
) : AdminRepository {

    private fun <T> handleResponse(response: Response<BaseResponse<T>>): Result<T> {
        return try {
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    @Suppress("UNCHECKED_CAST")
                    // Handle Unit type correctly when body.data is null
                    val data = (body.data ?: Unit) as T
                    Result.success(data)
                } else {
                    Result.failure(Exception(body?.message ?: "Lỗi hệ thống"))
                }
            } else {
                Result.failure(Exception(HttpException(response).toUserFriendlyMessage()))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    private fun handleMessageResponse(response: Response<BaseResponse<Unit>>): Result<String> {
        return try {
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body.message ?: "Thao tác thành công")
                } else {
                    Result.failure(Exception(body?.message ?: "Thao tác thất bại"))
                }
            } else {
                Result.failure(Exception(HttpException(response).toUserFriendlyMessage()))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun remoteUnlock(gateId: UUID, buildingId: UUID) = 
        handleResponse(apiService.remoteUnlock(gateId, buildingId))

    override suspend fun emergencyOverride(actionType: String, reason: String, buildingId: UUID?) =
        handleResponse(apiService.emergencyOverride(actionType, reason, buildingId))

    override suspend fun getPendingFaceProfiles(page: Int, size: Int) =
        handleResponse(apiService.getPendingFaceProfiles(page, size))

    override suspend fun approveFace(profileId: UUID) =
        handleMessageResponse(apiService.approveFace(profileId))

    override suspend fun rejectFace(profileId: UUID, reason: String) =
        handleMessageResponse(apiService.rejectFace(profileId, FaceRejectionRequest(reason)))

    override suspend fun revokeFace(profileId: UUID, reason: String) =
        handleMessageResponse(apiService.revokeFace(profileId, FaceRevocationRequest(reason)))

    override suspend fun approveReplacement(profileId: UUID) =
        handleMessageResponse(apiService.approveReplacement(profileId))

    override suspend fun rejectReplacement(profileId: UUID, reason: String) =
        handleMessageResponse(apiService.rejectReplacement(profileId, FaceRejectionRequest(reason)))

    override suspend fun getCheckoutRequests(status: String?, page: Int, size: Int) =
        handleResponse(apiService.getCheckoutRequests(status, page, size))

    override suspend fun reviewCheckoutRequest(requestId: UUID, status: String, rejectReason: String?) =
        handleResponse(apiService.reviewCheckoutRequest(requestId, CheckoutRequestReviewDto(status, rejectReason)))

    override suspend fun getStayExtensions(status: String?, page: Int, size: Int) =
        handleResponse(apiService.getStayExtensions(status, page, size))

    override suspend fun reviewStayExtension(id: UUID, status: String, rejectReason: String?) =
        handleResponse(apiService.reviewStayExtension(id, StayExtensionReviewRequest(status, rejectReason)))

    override suspend fun searchStudentForCheckIn(cccd: String) =
        handleResponse(apiService.searchStudentForCheckIn(cccd))

    override suspend fun confirmCheckIn(assignmentId: UUID): Result<String> {
        return try {
            val response = apiService.confirmCheckIn(assignmentId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body.data?.get("message") ?: body.message ?: "Check-in thành công")
                } else {
                    Result.failure(Exception(body?.message ?: "Check-in thất bại"))
                }
            } else {
                val errorMsg = try {
                    val errorBody = response.errorBody()?.string()
                    val apiResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                    apiResponse.message
                } catch (e: Exception) {
                    null
                }
                Result.failure(Exception(errorMsg ?: "Lỗi kết nối: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun broadcastNotification(title: String, message: String, targetAudience: String) =
        handleResponse(apiService.broadcastNotification(BroadcastRequest(title, message, targetAudience)))

    override suspend fun assignRfid(studentId: UUID, rfidCode: String) =
        handleMessageResponse(apiService.assignRfid(studentId, rfidCode))

    override suspend fun getBuildings() = handleResponse(apiService.getBuildings())

    override suspend fun getGates() = handleResponse(apiService.getGates())

    override suspend fun getDashboardStats(): Result<DashboardStats> {
        return try {
            val response = apiService.getDashboardStats()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data.toDomain())
                } else {
                    Result.failure(Exception(body?.message ?: "Lỗi hệ thống"))
                }
            } else {
                Result.failure(Exception(HttpException(response).toUserFriendlyMessage()))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getStudentProfile(studentId: UUID): Result<com.ktx.dormitory.shared.profile.domain.model.UserProfile> {
        return try {
            val response = apiService.getStudentProfile(studentId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data.toDomain())
                } else {
                    Result.failure(Exception(body?.message ?: "Lỗi hệ thống"))
                }
            } else {
                Result.failure(Exception(HttpException(response).toUserFriendlyMessage()))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}
