package com.ktx.dormitory.shared.notification.data.remote

import com.google.gson.annotations.SerializedName
import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.shared.notification.data.dto.response.NotificationResponse
import retrofit2.Response
import retrofit2.http.*

interface NotificationApiService {
    @GET("v1/notifications/unread-count")
    suspend fun getUnreadCount(): Response<BaseResponse<Long>>

    @GET("v1/notifications")
    suspend fun getNotificationsPaged(
        @Query("page") page: Int,
        @Query("size") size: Int = 20
    ): Response<BaseResponse<PageResponse<NotificationResponse>>>

    @PATCH("v1/notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Long): Response<BaseResponse<Unit>>

    @PATCH("v1/notifications/read-all")
    suspend fun markAllRead(): Response<BaseResponse<Unit>>

    @POST("v1/notifications/issues")
    suspend fun reportIssue(
        @Body request: IssueReportRequest
    ): Response<BaseResponse<Unit>>
}

data class IssueReportRequest(
    @SerializedName("description") val description: String,
    @SerializedName("isCommonArea") val isCommonArea: Boolean = false
)
