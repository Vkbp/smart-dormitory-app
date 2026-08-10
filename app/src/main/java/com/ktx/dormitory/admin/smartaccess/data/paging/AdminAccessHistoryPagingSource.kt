package com.ktx.dormitory.admin.smartaccess.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ktx.dormitory.admin.common.data.remote.AdminApiService
import com.ktx.dormitory.student.access.data.dto.response.AccessLogDto
import com.ktx.dormitory.student.access.data.mapper.toDomain
import com.ktx.dormitory.student.access.domain.model.AccessLog

class AdminAccessHistoryPagingSource(
    private val apiService: AdminApiService
) : PagingSource<Int, AccessLog>() {

    override fun getRefreshKey(state: PagingState<Int, AccessLog>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AccessLog> {
        val page = params.key ?: 0
        val size = params.loadSize

        return try {
            val response = apiService.getAdminAccessHistory(page, size)
            if (response.isSuccessful && response.body()?.success == true) {
                val pageResponse = response.body()?.data
                val data = pageResponse?.content?.map { it.toDomain() } ?: emptyList()
                LoadResult.Page(
                    data = data,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (pageResponse?.last == true) null else page + 1
                )
            } else {
                LoadResult.Error(Exception(response.body()?.message ?: "Lỗi tải lịch sử"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
