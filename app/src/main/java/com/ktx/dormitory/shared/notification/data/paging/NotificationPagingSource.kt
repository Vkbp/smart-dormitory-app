package com.ktx.dormitory.shared.notification.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ktx.dormitory.shared.notification.data.mapper.toDomain
import com.ktx.dormitory.shared.notification.data.remote.NotificationApiService
import com.ktx.dormitory.shared.notification.domain.model.Notification

class NotificationPagingSource(
    private val apiService: NotificationApiService
) : PagingSource<Int, Notification>() {

    override fun getRefreshKey(state: PagingState<Int, Notification>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notification> {
        val page = params.key ?: 0
        val size = params.loadSize

        return try {
            val response = apiService.getNotificationsPaged(page, size)
            if (response.isSuccessful && response.body()?.success == true) {
                val pagedData = response.body()!!.data!!
                LoadResult.Page(
                    data = pagedData.content?.map { it.toDomain() } ?: emptyList(),
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (pagedData.last) null else page + 1
                )
            } else {
                LoadResult.Error(Exception("Lỗi tải thông báo"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
