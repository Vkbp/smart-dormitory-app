package com.ktx.dormitory.student.maintenance.data.repository

import com.ktx.dormitory.core.network.toUserFriendlyMessage
import com.ktx.dormitory.student.maintenance.data.dto.request.CreateMaintenanceRequest
import com.ktx.dormitory.student.maintenance.data.mapper.toDomain
import com.ktx.dormitory.student.maintenance.data.remote.MaintenanceApiService
import com.ktx.dormitory.student.maintenance.domain.model.MaintenanceRequest
import com.ktx.dormitory.student.maintenance.domain.repository.MaintenanceRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaintenanceRepositoryImpl @Inject constructor(
    private val apiService: MaintenanceApiService
) : MaintenanceRepository {

    override suspend fun submitMaintenanceRequest(description: String, imageUrl: String?): Result<Unit> {
        return try {
            val response = apiService.submitMaintenanceRequest(CreateMaintenanceRequest(description, imageUrl))
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getMaintenanceHistory(): Result<List<MaintenanceRequest>> {
        return try {
            val response = apiService.getMaintenanceHistory()
            if (response.success && response.data != null) {
                val history = response.data.content?.map { it.toDomain() } ?: emptyList()
                Result.success(history)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun uploadImage(filePath: String): Result<String> {
        return try {
            val file = File(filePath)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            
            val response = apiService.uploadImage(body)
            if (response.success && response.data != null) {
                Result.success(response.data.url)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}
