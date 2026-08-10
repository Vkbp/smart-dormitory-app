package com.ktx.dormitory.admin.smartaccess.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ktx.dormitory.admin.common.data.remote.AdminApiService
import com.ktx.dormitory.admin.smartaccess.data.paging.AdminAccessHistoryPagingSource
import com.ktx.dormitory.student.access.domain.model.AccessLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAdminAccessHistoryUseCase @Inject constructor(
    private val apiService: AdminApiService
) {
    operator fun invoke(): Flow<PagingData<AccessLog>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { AdminAccessHistoryPagingSource(apiService) }
        ).flow
    }
}
