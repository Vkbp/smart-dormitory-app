package com.ktx.dormitory.shared.auth.data.local

import kotlinx.coroutines.flow.Flow

interface AuthLocalDataSource {
    suspend fun saveTokens(accessToken: String, refreshToken: String, role: String? = null)
    suspend fun clearTokens(keepRefreshToken: Boolean = false)
    fun getAccessTokenSync(): String?
    fun getRefreshTokenSync(): String?
    fun getRoleSync(): String?

    suspend fun saveLoginStatus(isLoggedIn: Boolean)
    fun isLoggedIn(): Flow<Boolean>

    suspend fun setBiometricEnabled(enabled: Boolean)
    fun isBiometricEnabled(): Flow<Boolean>
    fun isBiometricEnabledSync(): Boolean
}
