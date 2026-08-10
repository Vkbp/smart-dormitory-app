package com.ktx.dormitory.shared.auth.data.remote

import com.ktx.dormitory.shared.auth.data.dto.request.*
import com.ktx.dormitory.shared.auth.data.dto.response.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRemoteDataSourceImpl @Inject constructor(
    private val api: AuthApiService
) : AuthRemoteDataSource {
    override suspend fun login(request: LoginRequest) = api.login(request)
    override suspend fun refreshToken(request: RefreshTokenRequest) = api.refreshToken(request)
    override suspend fun activate(request: ActivateRequest) = api.activate(request)
    override suspend fun getCurrentUser() = api.getCurrentUser()
    override suspend fun getGenericUser() = api.getGenericUser()
    override suspend fun logout() = api.logout()
    override suspend fun changePassword(request: ChangePasswordRequest) = api.changePassword(request)
    override suspend fun forgotPassword(request: ForgotPasswordRequest) = api.forgotPassword(request)
    override suspend fun resetPassword(request: ResetPasswordRequest) = api.resetPassword(request)
}
