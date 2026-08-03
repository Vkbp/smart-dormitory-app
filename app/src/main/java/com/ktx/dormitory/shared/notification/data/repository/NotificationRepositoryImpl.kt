package com.ktx.dormitory.shared.notification.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ktx.dormitory.core.network.toUserFriendlyMessage
import com.ktx.dormitory.shared.notification.data.mapper.toDomain
import com.ktx.dormitory.shared.notification.data.paging.NotificationPagingSource
import com.ktx.dormitory.shared.notification.data.remote.IssueReportRequest
import com.ktx.dormitory.shared.notification.data.remote.NotificationApiService
import com.ktx.dormitory.shared.notification.domain.model.Notification
import com.ktx.dormitory.shared.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val apiService: NotificationApiService
) : NotificationRepository {

    override suspend fun getUnreadCount(): Result<Long> {
        return try {
            val response = apiService.getUnreadCount()
            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: 0L)
            } else {
                Result.failure(Exception(HttpException(response).toUserFriendlyMessage()))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override fun getNotificationsPaging(): Flow<PagingData<Notification>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { NotificationPagingSource(apiService) }
        ).flow
    }

    override suspend fun markAsRead(id: Long): Result<Unit> {
        return try {
            val response = apiService.markAsRead(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(HttpException(response).toUserFriendlyMessage()))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun markAllRead(): Result<Unit> {
        return try {
            val response = apiService.markAllRead()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(HttpException(response).toUserFriendlyMessage()))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun reportIssue(
        description: String,
        isCommonArea: Boolean
    ): Result<String> {
        return try {
            val response = apiService.reportIssue(IssueReportRequest(description, isCommonArea))
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    Result.success(body.message ?: "Đã gửi báo cáo vấn đề thành công")
                } else {
                    Result.failure(Exception(body?.message ?: "Gửi báo cáo thất bại"))
                }
            } else {
                Result.failure(Exception(HttpException(response).toUserFriendlyMessage()))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}
