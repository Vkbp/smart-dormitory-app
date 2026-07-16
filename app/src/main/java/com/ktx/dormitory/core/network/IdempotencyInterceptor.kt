package com.ktx.dormitory.core.network

import com.ktx.dormitory.core.util.IdempotencyUtils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor tự động gắn Idempotency Key cho các request ghi dữ liệu (POST, PUT, PATCH, DELETE)
 * Giúp ngăn chặn việc xử lý trùng lặp trên Server khi có Retry hoặc spam click.
 */
class IdempotencyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Chỉ gắn key cho các phương thức có khả năng thay đổi trạng thái dữ liệu
        val isWriteMethod = request.method == "POST" || 
                           request.method == "PUT" || 
                           request.method == "PATCH" || 
                           request.method == "DELETE"

        return if (isWriteMethod && request.header("X-Idempotency-Key") == null) {
            val requestBuilder = request.newBuilder()
            val key = IdempotencyUtils.generateKey()
            requestBuilder.addHeader("X-Idempotency-Key", key)
            chain.proceed(requestBuilder.build())
        } else {
            chain.proceed(request)
        }
    }
}
