package com.ktx.dormitory.student.access.data.repository

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessRepositoryImpl @Inject constructor(
    private val remoteDataSource: AccessRemoteDataSource,
    private val logDao: AccessLogDao,
    private val curfewDao: CurfewRequestDao
) : AccessRepository {

    override val accessLogs: Flow<List<AccessLog>> = logDao.getAllLogs()
        .map { list -> list.map { it.toDomain() } }

    override val curfewRequests: Flow<List<CurfewRequest>> = curfewDao.getAllRequests()
        .map { list -> list.map { it.toDomain() } }

    override suspend fun getAccessHistory(page: Int, size: Int): Result<PageResponse<AccessLogDto>> {
        return try {
            val response = remoteDataSource.getAccessHistory(page = page, size = size)
            if (response.success && response.data != null) {
                val pageData = response.data
                val logList = pageData.content ?: emptyList()
                logDao.insertLogs(logList.map { it.toEntity() })
                Result.success(pageData)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitCurfewRequest(
        reason: String,
        expectedArrivalTime: String,
        note: String?
    ): Result<CurfewRequest> {
        return try {
            val request = CurfewCreateRequest(reason, expectedArrivalTime, note)
            val response = remoteDataSource.submitCurfewRequest(request)
            if (response.success && response.data != null) {
                val domain = response.data.toDomain()
                curfewDao.insertRequest(response.data.toEntity())
                Result.success(domain)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyCurfewRequests(page: Int, size: Int): Result<PageResponse<CurfewRequest>> {
        return try {
            val response = remoteDataSource.getMyCurfewRequests(page, size)
            if (response.success && response.data != null) {
                val dtoList = response.data.content ?: emptyList()
                curfewDao.insertRequests(dtoList.map { it.toEntity() })
                
                val domainContent = dtoList.map { it.toDomain() }
                val domainPage = PageResponse(
                    content = domainContent,
                    pageNumber = response.data.pageNumber,
                    pageSize = response.data.pageSize,
                    totalElements = response.data.totalElements,
                    totalPages = response.data.totalPages,
                    last = response.data.last
                )
                Result.success(domainPage)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

