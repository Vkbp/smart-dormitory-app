package com.ktx.dormitory.student.access.domain.repository

import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.student.access.data.dto.response.AccessLogDto
import com.ktx.dormitory.student.access.domain.model.AccessLog
import com.ktx.dormitory.student.access.domain.model.CurfewRequest
import kotlinx.coroutines.flow.Flow

interface AccessRepository {
    val accessLogs: Flow<List<AccessLog>>
    val curfewRequests: Flow<List<CurfewRequest>>
    suspend fun getAccessHistory(page: Int = 0, size: Int = 10): Result<PageResponse<AccessLogDto>>
    suspend fun submitCurfewRequest(reason: String, expectedArrivalTime: String, note: String?): Result<CurfewRequest>
    suspend fun getMyCurfewRequests(page: Int = 0, size: Int = 20): Result<PageResponse<CurfewRequest>>
}
