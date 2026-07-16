package com.ktx.dormitory.shared.auth.data.remote

import com.ktx.dormitory.shared.auth.data.dto.request.*
import com.ktx.dormitory.shared.auth.data.dto.response.*
import com.ktx.dormitory.core.common.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    @POST("v1/auth/login")
    suspend fun login(@Body request: LoginRequest): BaseResponse<LoginResponse>

    @POST("v1/auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): BaseResponse<LoginResponse>

    @POST("v1/auth/activate")
    suspend fun activate(@Body request: ActivateRequest): BaseResponse<Unit>

    @GET("v1/students/me")
    suspend fun getCurrentUser(): BaseResponse<UserResponse>

    @GET("v1/users/me")
    suspend fun getGenericUser(): BaseResponse<UserResponse>

    @POST("v1/auth/logout")
    suspend fun logout(): BaseResponse<Unit>

    @POST("v1/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): BaseResponse<Unit>

    @POST("v1/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): BaseResponse<Unit>

    @POST("v1/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): BaseResponse<Unit>
}
