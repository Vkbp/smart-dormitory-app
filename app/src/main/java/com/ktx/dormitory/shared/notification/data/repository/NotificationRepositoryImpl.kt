package com.ktx.dormitory.shared.notification.data.repository

import com.ktx.dormitory.core.network.toUserFriendlyMessage
import com.ktx.dormitory.shared.notification.data.mapper.toDomain
import com.ktx.dormitory.shared.notification.data.remote.IssueReportRequest
import com.ktx.dormitory.shared.notification.data.remote.NotificationApiService
import com.ktx.dormitory.shared.notification.domain.model.Notification
import com.ktx.dormitory.shared.notification.domain.repository.NotificationRepository
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

    override suspend fun getNotifications(): Result<List<Notification>> {
        return try {
            val response = apiService.getNotifications()
            if (response.isSuccessful) {
                val list = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.success(list)
            } else {
                Result.failure(Exception(HttpException(response).toUserFriendlyMessage()))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
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
