package com.ktx.dormitory.student.access.domain.usecase

import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.student.access.data.dto.response.AccessLogDto
import com.ktx.dormitory.student.access.domain.repository.AccessRepository
import javax.inject.Inject

class GetAccessHistoryUseCase @Inject constructor(
    private val repository: AccessRepository
) {
    suspend operator fun invoke(page: Int = 0, size: Int = 10): Result<PageResponse<AccessLogDto>> {
        return repository.getAccessHistory(page, size)
    }
}
