package com.ktx.dormitory.student.access.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ktx.dormitory.student.access.data.dto.response.AccessLogDto
import com.ktx.dormitory.student.access.data.mapper.mergeTimelineEvents
import com.ktx.dormitory.student.access.data.remote.AccessApiService
import com.ktx.dormitory.student.face.data.dto.response.VerificationAttemptDto
import com.ktx.dormitory.student.face.data.remote.FaceApiService
import com.ktx.dormitory.student.access.domain.model.UnifiedTimelineEvent
import com.ktx.dormitory.student.access.domain.model.UnifiedEventType

class AccessHistoryPagingSource(
    private val accessApi: AccessApiService,
    private val faceApi: FaceApiService
) : PagingSource<Int, UnifiedTimelineEvent>() {

    override fun getRefreshKey(state: PagingState<Int, UnifiedTimelineEvent>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnifiedTimelineEvent> {
        val page = params.key ?: 0
        val size = params.loadSize

        return try {
            val accessResponse = accessApi.getAccessHistory(page = page, size = size)
            val faceResponse = faceApi.getMyVerifications(page = page, size = size)

            val accessBody = accessResponse.body()
            val faceBody = faceResponse.body()

            val isAccessSuccess = accessResponse.isSuccessful && accessBody?.success == true
            // Xử lý 404 (chưa có hồ sơ) như một danh sách trống thay vì báo lỗi
            val isFaceSuccess = (faceResponse.isSuccessful && faceBody?.success == true) || faceResponse.code() == 404

            if (isAccessSuccess && isFaceSuccess) {
                val accessPage = accessBody?.data
                val facePage = faceBody?.data

                val faceList = facePage?.content ?: emptyList<VerificationAttemptDto>()
                val accessList = accessPage?.content ?: emptyList<AccessLogDto>()

                val merged = mergeTimelineEvents(faceList, accessList)

                LoadResult.Page(
                    data = merged,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (accessPage?.last == true) null else page + 1
                )
            } else {
                val errorMsg = accessBody?.message ?: faceBody?.message ?: "Lỗi tải dữ liệu"
                LoadResult.Error(Exception(errorMsg))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

