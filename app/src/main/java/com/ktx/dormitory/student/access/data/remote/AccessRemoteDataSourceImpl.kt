package com.ktx.dormitory.student.access.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.student.access.data.dto.request.CurfewCreateRequest
import com.ktx.dormitory.student.access.data.dto.response.CurfewRequestDto
import com.ktx.dormitory.student.access.data.dto.response.AccessLogDto
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessRemoteDataSourceImpl @Inject constructor(
    private val api: AccessApiService
) : AccessRemoteDataSource {
    override suspend fun getAccessHistory(page: Int, size: Int, sort: String): Response<BaseResponse<PageResponse<AccessLogDto>>> =
        api.getAccessHistory(page, size, sort)

    override suspend fun submitCurfewRequest(request: CurfewCreateRequest): Response<BaseResponse<CurfewRequestDto>> =
        api.submitCurfewRequest(request)

    override suspend fun getMyCurfewRequests(page: Int, size: Int): Response<BaseResponse<PageResponse<CurfewRequestDto>>> =
        api.getMyCurfewRequests(page, size)
}
