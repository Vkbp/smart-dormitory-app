package com.ktx.dormitory.student.checkout.data.repository

import com.ktx.dormitory.core.network.toUserFriendlyMessage
import com.ktx.dormitory.student.checkout.data.remote.CheckoutApiService
import com.ktx.dormitory.student.checkout.domain.model.CheckoutRequest
import com.ktx.dormitory.student.checkout.domain.model.CheckoutResponse
import com.ktx.dormitory.student.checkout.domain.repository.CheckoutRepository
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckoutRepositoryImpl @Inject constructor(
    private val apiService: CheckoutApiService
) : CheckoutRepository {

    override suspend fun submitCheckoutRequest(request: CheckoutRequest): Result<CheckoutResponse> {
        return try {
            val response = apiService.submitCheckoutRequest(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Gửi yêu cầu trả phòng thất bại"))
                }
            } else {
                // Sử dụng helper để parse message từ errorBody
                Result.failure(Exception(HttpException(response).toUserFriendlyMessage()))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getCheckoutHistory(): Result<List<CheckoutResponse>> {
        return try {
            val response = apiService.getCheckoutHistory()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Lấy lịch sử trả phòng thất bại"))
                }
            } else {
                Result.failure(Exception(HttpException(response).toUserFriendlyMessage()))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}
