package com.ktx.dormitory.student.payment.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ktx.dormitory.student.payment.data.mapper.toDomain
import com.ktx.dormitory.student.payment.data.remote.PaymentApiService
import com.ktx.dormitory.student.payment.domain.model.Bill

class PaymentHistoryPagingSource(
    private val apiService: PaymentApiService
) : PagingSource<Int, Bill>() {

    override fun getRefreshKey(state: PagingState<Int, Bill>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Bill> {
        val page = params.key ?: 0
        val size = params.loadSize

        return try {
            val response = apiService.getPaymentHistoryPaged(page, size)
            if (response.success && response.data != null) {
                val pagedData = response.data
                LoadResult.Page(
                    data = pagedData.content?.map { it.toDomain() } ?: emptyList(),
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (pagedData.last) null else page + 1
                )
            } else {
                LoadResult.Error(Exception(response.message ?: "Lỗi tải dữ liệu"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
