package com.ktx.dormitory.admin.common.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.admin.common.data.dto.request.*
import com.ktx.dormitory.admin.common.data.dto.response.*
import com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface AdminApiService {

    // --- Smart Access ---
    @POST("v1/access/gates/{gateId}/unlock")
    suspend fun remoteUnlock(
        @Path("gateId") gateId: UUID,
        @Query("buildingId") buildingId: UUID,
        @Query("studentId") studentId: UUID? = null
    ): Response<BaseResponse<Unit>>

    @GET("v1/students")
    suspend fun searchStudents(
        @Query("search") query: String
    ): Response<BaseResponse<com.ktx.dormitory.admin.common.data.dto.response.StudentSearchResponse>>

    @POST("v1/access/emergency")
    suspend fun emergencyOverride(
        @Query("actionType") actionType: String,
        @Query("reason") reason: String,
        @Query("buildingId") buildingId: UUID?
    ): Response<BaseResponse<Unit>>

    // --- Face Approval ---
    @GET("v1/admin/faces/pending")
    suspend fun getPendingFaceProfiles(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<BaseResponse<PageResponse<FaceProfileDto>>>

    @POST("v1/admin/faces/{profileId}/approve")
    suspend fun approveFace(@Path("profileId") profileId: UUID): Response<BaseResponse<Unit>>

    @POST("v1/admin/faces/{profileId}/reject")
    suspend fun rejectFace(
        @Path("profileId") profileId: UUID,
        @Body request: FaceRejectionRequest
    ): Response<BaseResponse<Unit>>

    @POST("v1/admin/faces/{profileId}/revoke")
    suspend fun revokeFace(
        @Path("profileId") profileId: UUID,
        @Body request: FaceRevocationRequest
    ): Response<BaseResponse<Unit>>

    @POST("v1/admin/faces/{profileId}/replacements/approve")
    suspend fun approveReplacement(@Path("profileId") profileId: UUID): Response<BaseResponse<Unit>>

    @POST("v1/admin/faces/{profileId}/replacements/reject")
    suspend fun rejectReplacement(
        @Path("profileId") profileId: UUID,
        @Body request: FaceRejectionRequest
    ): Response<BaseResponse<Unit>>

    // --- Checkout Approval ---
    @GET("v1/admin/checkout-requests")
    suspend fun getCheckoutRequests(
        @Query("status") status: String?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<BaseResponse<PageResponse<CheckoutRequestResponseDto>>>

    @POST("v1/admin/checkout-requests/{requestId}/review")
    suspend fun reviewCheckoutRequest(
        @Path("requestId") requestId: UUID,
        @Body request: CheckoutRequestReviewDto
    ): Response<BaseResponse<CheckoutRequestResponseDto>>

    // --- Stay Extension ---
    @GET("v1/admin/extensions")
    suspend fun getStayExtensions(
        @Query("status") status: String?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<BaseResponse<PageResponse<StayExtensionResponseDto>>>

    @PUT("v1/admin/extensions/{id}/status")
    suspend fun reviewStayExtension(
        @Path("id") id: UUID,
        @Body request: StayExtensionReviewRequest
    ): Response<BaseResponse<StayExtensionResponseDto>>

    // --- Quick Check-in ---
    @GET("v1/admin/check-in/search")
    suspend fun searchStudentForCheckIn(@Query("cccd") cccd: String): Response<BaseResponse<CheckInSearchResponseDto>>

    @POST("v1/admin/check-in/{assignmentId}")
    suspend fun confirmCheckIn(@Path("assignmentId") assignmentId: UUID): Response<BaseResponse<Map<String, String>>>

    // --- RFID ---
    @POST("v1/students/{studentId}/rfid")
    suspend fun assignRfid(
        @Path("studentId") studentId: UUID,
        @Query("rfidCode") rfidCode: String
    ): Response<BaseResponse<Unit>>

    // --- Notification ---
    @POST("v1/admin/notifications/broadcast")
    suspend fun broadcastNotification(@Body request: BroadcastRequest): Response<BaseResponse<BroadcastResponse>>

    @GET("v1/access/history")
    suspend fun getAdminAccessHistory(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("methods") methods: String? = null
    ): Response<BaseResponse<PageResponse<com.ktx.dormitory.student.access.data.dto.response.AccessLogDto>>>

    // --- Selection Resources ---
    @GET("v1/admin/buildings")
    suspend fun getBuildings(): Response<BaseResponse<List<BuildingResponseDto>>>

    @GET("v1/gates")
    suspend fun getGates(): Response<BaseResponse<List<GateResponseDto>>>

    // --- Dashboard ---
    @GET("v1/dashboard/stats")
    suspend fun getDashboardStats(): Response<BaseResponse<DashboardStatsResponseDto>>

    // --- Student Profile ---
    @GET("v1/students/{id}/profile")
    suspend fun getStudentProfile(@Path("id") studentId: UUID): Response<BaseResponse<com.ktx.dormitory.shared.profile.data.dto.response.StudentResponse>>
}
