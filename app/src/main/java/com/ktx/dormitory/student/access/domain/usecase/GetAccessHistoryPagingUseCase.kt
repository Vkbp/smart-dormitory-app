package com.ktx.dormitory.student.access.domain.usecase

import androidx.paging.PagingData
import com.ktx.dormitory.student.access.domain.model.UnifiedTimelineEvent
import com.ktx.dormitory.student.access.domain.repository.AccessRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccessHistoryPagingUseCase @Inject constructor(
    private val repository: AccessRepository
) {
    operator fun invoke(studentId: String): Flow<PagingData<UnifiedTimelineEvent>> {
        return repository.getAccessHistoryPaging(studentId)
    }
}
