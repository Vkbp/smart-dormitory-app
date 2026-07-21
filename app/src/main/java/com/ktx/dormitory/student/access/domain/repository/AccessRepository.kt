package com.ktx.dormitory.student.access.domain.repository

import androidx.paging.PagingData
import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.student.access.data.dto.response.AccessLogDto
import com.ktx.dormitory.student.access.domain.model.AccessLog
import com.ktx.dormitory.student.access.domain.model.CurfewRequest
import com.ktx.dormitory.student.access.domain.model.CurfewRequestType
import com.ktx.dormitory.student.access.domain.model.UnifiedTimelineEvent
import kotlinx.coroutines.flow.Flow

interface AccessRepository {
    val accessLogs: Flow<List<AccessLog>>
    val curfewRequests: Flow<List<CurfewRequest>>
    fun getAccessHistoryPaging(studentId: String): Flow<PagingData<UnifiedTimelineEvent>>
    suspend fun getAccessHistory(page: Int = 0, size: Int = 10): Result<PageResponse<AccessLogDto>>
    suspend fun getUnifiedAccessHistory(studentId: String, page: Int, size: Int): Result<PageResponse<UnifiedTimelineEvent>>
    
    suspend fun submitCurfewRequest(
        requestType: CurfewRequestType,
        reason: String,
        startDate: String?,
        expectedArrivalTime: String,
        note: String?
    ): Result<CurfewRequest>

    suspend fun getMyCurfewRequests(page: Int = 0, size: Int = 20): Result<PageResponse<CurfewRequest>>
}
