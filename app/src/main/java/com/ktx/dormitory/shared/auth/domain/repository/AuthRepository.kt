package com.ktx.dormitory.shared.auth.domain.repository

import com.ktx.dormitory.shared.auth.domain.model.UserData

/**
 * Interface định nghĩa các nghiệp vụ liên quan đến xác thực.
 */
interface AuthRepository {
    suspend fun login(usernameOrEmail: String, password: String): Result<UserData>
    suspend fun activate(email: String, tempPass: String, newPass: String): Result<Unit>
    suspend fun getCurrentUser(): Result<UserData>
    suspend fun changePassword(oldPass: String, newPass: String): Result<Unit>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun resetPassword(email: String, otp: String, newPass: String): Result<Unit>
    suspend fun logout()
    suspend fun saveLoginStatus(isLoggedIn: Boolean)
    
    suspend fun refreshToken(): Result<UserData>
    fun isBiometricEnabled(): kotlinx.coroutines.flow.Flow<Boolean>
    suspend fun setBiometricEnabled(enabled: Boolean)
    fun isBiometricEnabledSync(): Boolean
    fun hasLocalSession(): Boolean
}
