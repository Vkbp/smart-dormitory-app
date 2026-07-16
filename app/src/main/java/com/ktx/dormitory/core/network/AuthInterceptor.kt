package com.ktx.dormitory.core.network

import com.ktx.dormitory.shared.auth.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        
        // Không thêm token cho các endpoint Auth công khai
        val path = chain.request().url.encodedPath
        val isPublicEndpoint = path.contains("/auth/login") || 
                              path.contains("/auth/refresh-token") || 
                              path.contains("/auth/activate") ||
                              path.contains("/auth/forgot-password") ||
                              path.contains("/auth/reset-password") ||
                              path.contains("/v1/public/")

        if (!isPublicEndpoint) {
            val token = tokenManager.getAccessTokenSync()
            if (!token.isNullOrBlank()) {
                android.util.Log.d("AUTH_INTERCEPTOR", "Adding token for: $path")
                requestBuilder.addHeader("Authorization", "Bearer $token")
            } else {
                android.util.Log.w("AUTH_INTERCEPTOR", "No token found for private endpoint: $path")
            }
        } else {
            android.util.Log.d("AUTH_INTERCEPTOR", "Public endpoint (no token): $path")
        }

        val response = chain.proceed(requestBuilder.build())

        // Logic bổ sung: Nếu gặp 403 trên các route Admin/Access, có thể do token lỗi
        // Hoặc nếu Backend trả về 200 nhưng message chứa "hết hạn" (Fallback case)
        if (response.code == 403 && (path.contains("/v1/admin/") || path.contains("/v1/access/"))) {
            android.util.Log.w("AUTH_INTERCEPTOR", "403 detected on Admin route, converting to 401 to trigger refresh.")
            return response.newBuilder().code(401).build()
        }

        return response
    }
}
