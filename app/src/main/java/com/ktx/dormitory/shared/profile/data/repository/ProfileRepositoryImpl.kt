package com.ktx.dormitory.shared.profile.data.repository

import com.ktx.dormitory.core.network.toUserFriendlyMessage
import com.ktx.dormitory.shared.profile.data.local.ProfileLocalDataSource
import com.ktx.dormitory.shared.profile.data.mapper.*
import com.ktx.dormitory.shared.profile.data.remote.ProfileRemoteDataSource
import com.ktx.dormitory.shared.profile.data.dto.request.UpdateProfileRequest
import com.ktx.dormitory.shared.profile.domain.model.UserProfile
import com.ktx.dormitory.shared.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProfileRemoteDataSource,
    private val localDataSource: ProfileLocalDataSource
) : ProfileRepository {

    override suspend fun getProfile(): Result<UserProfile> {
        return try {
            val response = remoteDataSource.getDetailedProfile()
            if (response.success && response.data != null) {
                val profile = response.data.toDomain()
                localDataSource.saveProfile(response.data.toEntity())
                Result.success(profile)
            } else {
                val cached = localDataSource.getProfile().firstOrNull()?.toDomain()
                if (cached != null) Result.success(cached)
                else Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            val cached = localDataSource.getProfile().firstOrNull()?.toDomain()
            if (cached != null) {
                android.util.Log.d("ProfileRepo", "Using cached profile due to error: ${e.message}")
                Result.success(cached)
            } else {
                Result.failure(Exception("Không thể kết nối máy chủ. Vui lòng kiểm tra mạng."))
            }
        }
    }
    
    fun getStudentProfile(studentId: String) {
        // Implement if needed for other places, but we'll use AdminRepository for Admin side
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): Result<Unit> {
        return try {
            val response = remoteDataSource.updateProfile(request.toDto())
            if (response.success) Result.success(Unit) else Result.failure(Exception(response.message))
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun uploadAvatar(filePath: String): Result<String> {
        return try {
            val file = File(filePath)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            
            val response = remoteDataSource.uploadAvatar(body)
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

