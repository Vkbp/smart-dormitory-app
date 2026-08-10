package com.ktx.dormitory.student.extension.data.repository

import com.ktx.dormitory.core.network.toUserFriendlyMessage
import com.ktx.dormitory.student.extension.data.dto.request.CheckEligibilityRequestDto
import com.ktx.dormitory.student.extension.data.dto.response.CheckEligibilityResponseDto
import com.ktx.dormitory.student.extension.data.remote.ExtensionApiService
import com.ktx.dormitory.student.extension.domain.model.CheckEligibilityResult
import com.ktx.dormitory.student.extension.domain.model.ExtensionRequest
import com.ktx.dormitory.student.extension.domain.model.StayExtensionResponse
import com.ktx.dormitory.student.extension.domain.repository.ExtensionRepository
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtensionRepositoryImpl @Inject constructor(
    private val apiService: ExtensionApiService
) : ExtensionRepository {
    override suspend fun submitExtension(request: ExtensionRequest): Result<StayExtensionResponse> {
        return try {
            val response = apiService.submitExtension(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Unknown error occurred"))
                }
            } else {
                Result.failure(Exception(HttpException(response).toUserFriendlyMessage()))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun isExtensionPeriodActive(): Result<Boolean> {
        return try {
            val response = apiService.getActivePeriod()
            if (response.isSuccessful) {
                val body = response.body()
                // Kiểm tra xem đợt đăng ký có đang active không
                val isActive = body?.data?.let { 
                    it.registrationType == "CURRENT_RESIDENT" && it.isActive
                } ?: false
                Result.success(isActive)
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getMyExtensionApplication(): Result<StayExtensionResponse> {
        return try {
            val response = apiService.getMyExtensionApplication()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Bạn chưa nộp đơn gia hạn nào"))
                }
            } else if (response.code() == 404) {
                Result.failure(Exception("Bạn chưa nộp đơn gia hạn nào"))
            } else {
                Result.failure(Exception(HttpException(response).toUserFriendlyMessage()))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun checkEligibility(cccd: String): Result<CheckEligibilityResult> {
        return try {
            val response = apiService.checkEligibility(CheckEligibilityRequestDto(cccd))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val data = body.data
                    Result.success(CheckEligibilityResult(
                        eligible = data.eligible,
                        periodName = data.periodName,
                        fullName = data.fullName,
                        message = data.message
                    ))
                } else {
                    Result.failure(Exception(body?.message ?: "Kiểm tra điều kiện thất bại"))
                }
            } else {
                Result.failure(Exception(HttpException(response).toUserFriendlyMessage()))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}
