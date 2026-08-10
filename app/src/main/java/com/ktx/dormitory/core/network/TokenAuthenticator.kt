package com.ktx.dormitory.core.network

import com.ktx.dormitory.shared.auth.data.local.TokenManager
import com.ktx.dormitory.shared.auth.data.remote.AuthApiService
import com.ktx.dormitory.shared.auth.data.dto.request.RefreshTokenRequest
import com.ktx.dormitory.shared.auth.domain.repository.AuthRepository
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApiProvider: Provider<AuthApiService>,
    private val authRepositoryLazy: Lazy<AuthRepository>
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Log để debug (sẽ thấy trong Logcat)
        android.util.Log.d("TOKEN_AUTH", "401 detected for: ${response.request.url.encodedPath}")

        // Tránh loop vô tận
        if (response.countPriorResponse() >= 3) {
            android.util.Log.e("TOKEN_AUTH", "Too many retries, giving up.")
            handleAuthFailure()
            return null
        }
        
        // Không refresh nếu chính api refresh-token trả về 401
        if (response.request.url.encodedPath.contains("/auth/refresh-token")) {
            android.util.Log.e("TOKEN_AUTH", "Refresh token itself is expired.")
            handleAuthFailure()
            return null
        }

        val refreshToken = tokenManager.getRefreshTokenSync() ?: return null

        return runBlocking {
            mutex.withLock {
                val currentToken = tokenManager.getAccessTokenSync()
                val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

                // Nếu token trong request khác với token hiện tại (nghĩa là đã được refresh bởi thread khác)
                if (requestToken != currentToken && currentToken != null) {
                    android.util.Log.d("TOKEN_AUTH", "Token was already refreshed by another thread.")
                    return@withLock response.request.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                }

                android.util.Log.d("TOKEN_AUTH", "Starting refresh token process...")
                try {
                    val api = authApiProvider.get()
                    val refreshResponse = api.refreshToken(RefreshTokenRequest(refreshToken))
                    
                    if (refreshResponse.success && refreshResponse.data != null) {
                        val newData = refreshResponse.data
                        android.util.Log.i("TOKEN_AUTH", "Token refreshed successfully.")
                        
                        // Quan trọng: Phải lấy role hiện tại để không bị mất Role Admin
                        val currentRole = tokenManager.getRoleSync()
                        tokenManager.saveTokens(newData.accessToken, newData.refreshToken, currentRole)
                        
                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${newData.accessToken}")
                            .build()
                    } else {
                        android.util.Log.e("TOKEN_AUTH", "Refresh API returned failure: ${refreshResponse.message}")
                        handleAuthFailure()
                        null
                    }
                } catch (e: Exception) {
                    android.util.Log.e("TOKEN_AUTH", "Refresh token exception: ${e.message}")
                    // Nếu là lỗi 400 và có errorCode là REFRESH_TOKEN_REVOKED hoặc REFRESH_TOKEN_EXPIRED
                    if (e is retrofit2.HttpException) {
                        try {
                            val errorBody = e.response()?.errorBody()?.string()
                            val apiResponse = com.google.gson.Gson().fromJson(errorBody, com.ktx.dormitory.core.common.BaseResponse::class.java)
                            if (apiResponse.errorCode?.contains("REFRESH", ignoreCase = true) == true) {
                                android.util.Log.e("TOKEN_AUTH", "Refresh Token is completely dead.")
                                handleAuthFailure()
                                return@withLock null
                            }
                        } catch (ex: Exception) { /* ignore */ }
                    }
                    handleAuthFailure()
                    null
                }
            }
        }
    }

    private fun handleAuthFailure() {
        android.util.Log.e("TOKEN_AUTH", "Auth failure handled: Clearing session and logging out.")
        runBlocking {
            tokenManager.clearTokens(keepRefreshToken = false)
            tokenManager.saveLoginStatus(false)
            com.ktx.dormitory.core.util.AuthEventBus.emit(com.ktx.dormitory.core.util.AuthEvent.LOGOUT)
        }
    }

    private fun Response.countPriorResponse(): Int {
        var result = 0
        var prior = priorResponse
        while (result < 5 && prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
