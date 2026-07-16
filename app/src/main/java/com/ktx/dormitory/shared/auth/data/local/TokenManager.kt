package com.ktx.dormitory.shared.auth.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) : AuthLocalDataSource {

    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")

    override suspend fun saveTokens(accessToken: String, refreshToken: String, role: String?) {
        sharedPreferences.edit().apply {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            if (role != null) {
                putString("user_role", role)
            }
            apply()
        }
        saveLoginStatus(true)
    }

    override suspend fun clearTokens(keepRefreshToken: Boolean) {
        sharedPreferences.edit().apply {
            remove("access_token")
            remove("user_role")
            if (!keepRefreshToken) {
                remove("refresh_token")
            }
            apply()
        }
        if (!keepRefreshToken) {
            saveLoginStatus(false)
        }
    }

    override fun getAccessTokenSync(): String? = sharedPreferences.getString("access_token", null)

    override fun getRefreshTokenSync(): String? = sharedPreferences.getString("refresh_token", null)

    override fun getRoleSync(): String? = sharedPreferences.getString("user_role", null)

    override suspend fun saveLoginStatus(isLoggedIn: Boolean) {
        context.dataStore.edit { it[IS_LOGGED_IN] = isLoggedIn }
    }

    override fun isLoggedIn(): Flow<Boolean> = context.dataStore.data.map {
        it[IS_LOGGED_IN] ?: false
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("biometric_enabled", enabled).apply()
        context.dataStore.edit { it[BIOMETRIC_ENABLED] = enabled }
    }

    override fun isBiometricEnabled(): Flow<Boolean> = context.dataStore.data.map {
        it[BIOMETRIC_ENABLED] ?: false
    }

    override fun isBiometricEnabledSync(): Boolean = sharedPreferences.getBoolean("biometric_enabled", false)
}
