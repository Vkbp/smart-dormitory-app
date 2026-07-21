package com.ktx.dormitory.student.access.data.remote

import com.ktx.dormitory.student.access.data.dto.request.CurfewCreateRequest
import com.ktx.dormitory.student.access.data.dto.response.CurfewRequestDto
import com.ktx.dormitory.student.access.data.dto.response.AccessLogDto
import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.core.common.PageResponse
import retrofit2.Response
import retrofit2.http.*

interface AccessApiService {
    @GET("v1/access/history/me")
    suspend fun getAccessHistory(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "eventTimestamp,desc"
    ): Response<BaseResponse<PageResponse<AccessLogDto>>>

    @POST("smart-access/curfew-requests")
    suspend fun submitCurfewRequest(
        @Body request: CurfewCreateRequest
    ): Response<BaseResponse<CurfewRequestDto>>

    @GET("smart-access/curfew-requests/my-requests")
    suspend fun getMyCurfewRequests(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "createdAt,desc"
    ): Response<BaseResponse<PageResponse<CurfewRequestDto>>>
}
