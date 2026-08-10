package com.ktx.dormitory.student.access.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ktx.dormitory.core.network.toUserFriendlyMessage
import com.ktx.dormitory.student.access.data.paging.AccessHistoryPagingSource
import com.ktx.dormitory.student.access.data.mapper.mergeTimelineEvents
import com.ktx.dormitory.student.access.data.remote.AccessApiService
import com.ktx.dormitory.student.face.data.remote.FaceApiService
import com.ktx.dormitory.student.access.domain.model.UnifiedTimelineEvent
import com.ktx.dormitory.student.access.data.mapper.toDomain
import com.ktx.dormitory.student.access.data.mapper.toEntity
import com.ktx.dormitory.student.access.data.remote.AccessRemoteDataSource
import com.ktx.dormitory.student.access.data.local.AccessLogDao
import com.ktx.dormitory.student.access.data.local.CurfewRequestDao
import com.ktx.dormitory.student.face.data.remote.FaceRemoteDataSource
import com.ktx.dormitory.student.access.domain.model.AccessLog
import com.ktx.dormitory.student.access.domain.model.CurfewRequest
import com.ktx.dormitory.student.access.domain.repository.AccessRepository
import com.ktx.dormitory.student.access.data.dto.response.AccessLogDto
import com.ktx.dormitory.student.access.data.dto.request.CurfewCreateRequest
import com.ktx.dormitory.core.common.PageResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessRepositoryImpl @Inject constructor(
    private val remoteDataSource: AccessRemoteDataSource,
    private val apiService: AccessApiService,
    private val faceApiService: FaceApiService,
    private val logDao: AccessLogDao,
    private val curfewDao: CurfewRequestDao
) : AccessRepository {

    override val accessLogs: Flow<List<AccessLog>> = logDao.getAllLogs()
        .map { list -> list.map { it.toDomain() } }

    override val curfewRequests: Flow<List<CurfewRequest>> = curfewDao.getAllRequests()
        .map { list -> list.map { it.toDomain() } }

    override fun getAccessHistoryPaging(studentId: String): Flow<PagingData<UnifiedTimelineEvent>> {
        return Pager(
            config = PagingConfig(pageSize = 15, enablePlaceholders = false),
            pagingSourceFactory = { AccessHistoryPagingSource(apiService, faceApiService) }
        ).flow
    }

    override suspend fun getUnifiedAccessHistory(
        studentId: String,
        page: Int,
        size: Int
    ): Result<PageResponse<UnifiedTimelineEvent>> = coroutineScope {
        try {
            val accessDeferred = async { apiService.getAccessHistory(page, size) }
            val faceDeferred = async { faceApiService.getMyVerifications(page, size) }

            val accessRes = accessDeferred.await()
            val faceRes = faceDeferred.await()

            val accessBody = accessRes.body()
            val faceBody = faceRes.body()

            val isAccessSuccess = accessRes.isSuccessful && accessBody?.success == true
            // Xử lý 404 (chưa đăng ký khuôn mặt) như danh sách trống
            val isFaceSuccess = (faceRes.isSuccessful && faceBody?.success == true) || faceRes.code() == 404

            if (isAccessSuccess && isFaceSuccess) {
                val accessPage = accessBody?.data
                val facePage = faceBody?.data

                if (accessPage != null) {
                    val merged = mergeTimelineEvents(
                        facePage?.content ?: emptyList(),
                        accessPage.content ?: emptyList()
                    )

                    val pageResponse = PageResponse(
                        content = merged,
                        pageNumber = accessPage.pageNumber,
                        pageSize = accessPage.pageSize,
                        totalElements = accessPage.totalElements,
                        totalPages = accessPage.totalPages,
                        last = accessPage.last
                    )
                    Result.success(pageResponse)
                } else {
                    Result.failure(Exception("Dữ liệu truy cập không hợp lệ"))
                }
            } else {
                val errorMsg = accessBody?.message ?: faceBody?.message ?: "Lỗi tải dữ liệu lịch sử"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getAccessHistory(page: Int, size: Int): Result<PageResponse<AccessLogDto>> {
        return try {
            val response = apiService.getAccessHistory(page = page, size = size)
            val body = response.body()
            if (response.isSuccessful && body != null && body.success) {
                val pageData = body.data
                if (pageData != null) {
                    val logList = pageData.content ?: emptyList()
                    logDao.insertLogs(logList.map { it.toEntity() })
                    Result.success(pageData)
                } else {
                    Result.failure(Exception("Dữ liệu trống"))
                }
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi tải lịch sử"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun submitCurfewRequest(
        requestType: com.ktx.dormitory.student.access.domain.model.CurfewRequestType,
        reason: String,
        startDate: String?,
        expectedArrivalTime: String,
        note: String?
    ): Result<CurfewRequest> {
        return try {
            val request = CurfewCreateRequest(
                requestType = requestType.name,
                reason = reason,
                startDate = startDate,
                expectedArrivalTime = expectedArrivalTime,
                note = note
            )
            val response = remoteDataSource.submitCurfewRequest(request)
            val body = response.body()
            if (response.isSuccessful && body != null && body.success && body.data != null) {
                val domain = body.data.toDomain()
                curfewDao.insertRequest(body.data.toEntity())
                Result.success(domain)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi gửi yêu cầu"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getMyCurfewRequests(page: Int, size: Int): Result<PageResponse<CurfewRequest>> {
        return try {
            val response = remoteDataSource.getMyCurfewRequests(page, size)
            val body = response.body()
            if (response.isSuccessful && body != null && body.success && body.data != null) {
                val dtoList = body.data.content ?: emptyList()
                curfewDao.insertRequests(dtoList.map { it.toEntity() })
                
                val domainContent = dtoList.map { it.toDomain() }
                val domainPage = PageResponse(
                    content = domainContent,
                    pageNumber = body.data.pageNumber,
                    pageSize = body.data.pageSize,
                    totalElements = body.data.totalElements,
                    totalPages = body.data.totalPages,
                    last = body.data.last
                )
                Result.success(domainPage)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi tải danh sách"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}

