package com.ktx.dormitory.student.access.domain.usecase

import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.student.access.domain.model.UnifiedTimelineEvent
import com.ktx.dormitory.student.access.domain.repository.AccessRepository
import javax.inject.Inject

class GetUnifiedAccessHistoryUseCase @Inject constructor(
    private val repository: AccessRepository
) {
    suspend operator fun invoke(studentId: String, page: Int = 0, size: Int = 15): Result<PageResponse<UnifiedTimelineEvent>> {
        return repository.getUnifiedAccessHistory(studentId, page, size)
    }
}
