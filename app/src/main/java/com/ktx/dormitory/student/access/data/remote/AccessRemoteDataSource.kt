package com.ktx.dormitory.student.access.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.student.access.data.dto.request.CurfewCreateRequest
import com.ktx.dormitory.student.access.data.dto.response.CurfewRequestDto
import com.ktx.dormitory.student.access.data.dto.response.AccessLogDto
import com.ktx.dormitory.core.common.PageResponse

import retrofit2.Response

interface AccessRemoteDataSource {
    suspend fun getAccessHistory(
        page: Int = 0,
        size: Int = 10,
        sort: String = "eventTimestamp,desc"
    ): Response<BaseResponse<PageResponse<AccessLogDto>>>

    suspend fun submitCurfewRequest(request: CurfewCreateRequest): Response<BaseResponse<CurfewRequestDto>>

    suspend fun getMyCurfewRequests(
        page: Int = 0,
        size: Int = 20
    ): Response<BaseResponse<PageResponse<CurfewRequestDto>>>
}
