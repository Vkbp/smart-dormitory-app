package com.ktx.dormitory.shared.auth.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.shared.auth.data.dto.request.*
import com.ktx.dormitory.shared.auth.data.dto.response.*

interface AuthRemoteDataSource {
    suspend fun login(request: LoginRequest): BaseResponse<LoginResponse>
    suspend fun refreshToken(request: RefreshTokenRequest): BaseResponse<LoginResponse>
    suspend fun activate(request: ActivateRequest): BaseResponse<Unit>
    suspend fun getCurrentUser(): BaseResponse<UserResponse>
    suspend fun getGenericUser(): BaseResponse<UserResponse>
    suspend fun logout(): BaseResponse<Unit>
    suspend fun changePassword(request: ChangePasswordRequest): BaseResponse<Unit>
    suspend fun forgotPassword(request: ForgotPasswordRequest): BaseResponse<Unit>
    suspend fun resetPassword(request: ResetPasswordRequest): BaseResponse<Unit>
}
