package com.ktx.dormitory.shared.notification.data.remote

import com.google.gson.annotations.SerializedName
import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.shared.notification.data.dto.response.NotificationResponse
import retrofit2.Response
import retrofit2.http.*

interface NotificationApiService {
    @GET("v1/notifications/unread-count")
    suspend fun getUnreadCount(): Response<BaseResponse<Long>>

    @GET("v1/notifications")
    suspend fun getNotifications(): Response<BaseResponse<List<NotificationResponse>>>

    @PATCH("v1/notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Long): Response<BaseResponse<Unit>>

    @PATCH("v1/notifications/read-all")
    suspend fun markAllRead(): Response<BaseResponse<Unit>>

    @POST("v1/notifications/issues")
    suspend fun reportIssue(
        @Body request: IssueReportRequest
    ): Response<BaseResponse<Unit>>

    @GET("v1/notifications/issues/history")
    suspend fun getIssueHistory(): Response<BaseResponse<List<IssueReportResponse>>>
}

data class IssueReportResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("description") val description: String,
    @SerializedName("status") val status: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("imageUrl") val imageUrl: String? = null
)

data class IssueReportRequest(
    @SerializedName("description") val description: String,
    @SerializedName("roomId") val roomId: String,
    @SerializedName("imageUrl") val imageUrl: String? = null
)
