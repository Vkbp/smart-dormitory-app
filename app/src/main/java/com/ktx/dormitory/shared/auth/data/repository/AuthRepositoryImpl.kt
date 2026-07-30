package com.ktx.dormitory.shared.auth.data.repository

import com.ktx.dormitory.core.network.toUserFriendlyMessage
import com.ktx.dormitory.core.security.JwtUtils
import com.ktx.dormitory.shared.auth.data.local.AuthLocalDataSource
import com.ktx.dormitory.shared.auth.data.mapper.toDomain
import com.ktx.dormitory.shared.auth.data.mapper.toEntity
import com.ktx.dormitory.shared.auth.data.remote.AuthRemoteDataSource
import com.ktx.dormitory.shared.auth.data.dto.request.*
import com.ktx.dormitory.shared.auth.data.dto.response.*
import com.ktx.dormitory.shared.profile.data.local.ProfileLocalDataSource
import com.ktx.dormitory.shared.auth.domain.model.UserData
import com.ktx.dormitory.shared.auth.domain.repository.AuthRepository
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource,
    private val profileLocalDataSource: ProfileLocalDataSource
) : AuthRepository {

    override suspend fun login(usernameOrEmail: String, password: String): Result<UserData> {
        return try {
            localDataSource.clearTokens()
            
            val response = remoteDataSource.login(LoginRequest(usernameOrEmail, password))
            if (response.success && response.data != null) {
                // Ưu tiên lấy Role từ JWT Payload (Cách 1)
                val jwtRole = JwtUtils.getRoleFromToken(response.data.accessToken)
                
                // Fallback sang response body nếu JWT decode lỗi
                val rawRole = jwtRole ?: response.data.role ?: "STUDENT"
                
                // Chuẩn hóa Role (Bỏ tiền tố ROLE_ nếu có)
                val role = if (rawRole.startsWith("ROLE_")) 
                    rawRole.substring(5) 
                    else rawRole
                
                localDataSource.saveTokens(response.data.accessToken, response.data.refreshToken, role)
                
                // Lấy Profile chi tiết (Cách 2)
                try {
                    // Nếu là Admin/Staff, gọi users/me thay vì students/me
                    val profileResponse = if (role == "ADMIN" || role == "STAFF") {
                        remoteDataSource.getGenericUser()
                    } else {
                        // Mặc định là STUDENT hoặc vai trò khác, gọi students/me
                        remoteDataSource.getCurrentUser()
                    }
                    
                    if (profileResponse.success && profileResponse.data != null) {
                        val user = profileResponse.data.toDomain()
                        // Lưu lại Profile vào Room để các tính năng khác (như Access History) có studentId
                        if (role == "STUDENT") {
                            profileLocalDataSource.saveProfile(profileResponse.data.toEntity())
                        }

                        // Lưu lại Role thực tế từ profile nếu có
                        localDataSource.saveTokens(response.data.accessToken, response.data.refreshToken, user.role)
                        return Result.success(user)
                    }
                } catch (e: Exception) {
                    android.util.Log.w("AUTH_REPO", "Could not fetch profile, using fallback: ${e.message}")
                }

                // Fallback using login data
                val fallbackUser = UserData(
                    username = usernameOrEmail,
                    role = role
                )
                Result.success(fallbackUser)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun activate(email: String, tempPass: String, newPass: String): Result<Unit> {
        return try {
            val response = remoteDataSource.activate(ActivateRequest(email, tempPass, newPass))
            if (response.success) Result.success(Unit) else Result.failure(Exception(response.message))
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getCurrentUser(): Result<UserData> {
        return try {
            val token = localDataSource.getAccessTokenSync()
            if (token.isNullOrBlank()) {
                return Result.failure(Exception("No local token found"))
            }

            val role = localDataSource.getRoleSync() ?: "STUDENT"

            try {
                val response = if (role == "ADMIN" || role == "STAFF") {
                    remoteDataSource.getGenericUser()
                } else {
                    remoteDataSource.getCurrentUser()
                }
                
                if (response.success && response.data != null) {
                    return Result.success(response.data.toDomain())
                }
            } catch (e: Exception) {
                // Fallback below
            }

            Result.success(UserData(username = "User", role = role))
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun changePassword(oldPass: String, newPass: String): Result<Unit> {
        return try {
            val response = remoteDataSource.changePassword(ChangePasswordRequest(oldPass, newPass))
            if (response.success) Result.success(Unit) else Result.failure(Exception(response.message))
        } catch (e: Exception) { 
            Result.failure(Exception(e.toUserFriendlyMessage())) 
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            val response = remoteDataSource.forgotPassword(ForgotPasswordRequest(email))
            if (response.success) Result.success(Unit) else Result.failure(Exception(response.message))
        } catch (e: Exception) { 
            Result.failure(Exception(e.toUserFriendlyMessage())) 
        }
    }

    override suspend fun resetPassword(token: String, newPass: String): Result<Unit> {
        return try {
            val response = remoteDataSource.resetPassword(ResetPasswordRequest(token, newPass))
            if (response.success) Result.success(Unit) else Result.failure(Exception(response.message))
        } catch (e: Exception) { 
            Result.failure(Exception(e.toUserFriendlyMessage())) 
        }
    }

    override suspend fun logout() {
        android.util.Log.d("AUTH_REPO", "Logging out...")
        try {
            remoteDataSource.logout()
        } catch (e: Exception) {
            android.util.Log.e("AUTH_REPO", "Logout API failed: ${e.message}")
        } finally {
            localDataSource.clearTokens(keepRefreshToken = false)
            localDataSource.saveLoginStatus(false)
            profileLocalDataSource.clearProfile()
            com.ktx.dormitory.core.util.AuthEventBus.emit(com.ktx.dormitory.core.util.AuthEvent.LOGOUT)
            android.util.Log.d("AUTH_REPO", "Local session cleared")
        }
    }

    override suspend fun saveLoginStatus(isLoggedIn: Boolean) {
        localDataSource.saveLoginStatus(isLoggedIn)
    }

    override suspend fun refreshToken(): Result<UserData> {
        return try {
            val refreshToken = localDataSource.getRefreshTokenSync()
            if (refreshToken.isNullOrBlank()) {
                return Result.failure(Exception("No refresh token found"))
            }

            val response = remoteDataSource.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.success && response.data != null) {
                val role = if (response.data.role?.startsWith("ROLE_") == true)
                    response.data.role.substring(5)
                    else response.data.role ?: localDataSource.getRoleSync() ?: "STUDENT"
                
                localDataSource.saveTokens(response.data.accessToken, response.data.refreshToken, role)
                
                // Cố gắng cập nhật profile nhưng không bắt buộc
                getCurrentUser()
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override fun isBiometricEnabled() = localDataSource.isBiometricEnabled()

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        localDataSource.setBiometricEnabled(enabled)
    }

    override fun isBiometricEnabledSync() = localDataSource.isBiometricEnabledSync()

    override fun hasLocalSession(): Boolean {
        return !localDataSource.getRefreshTokenSync().isNullOrBlank()
    }
}
