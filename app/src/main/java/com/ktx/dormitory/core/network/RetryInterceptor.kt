package com.ktx.dormitory.core.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor tự động thử lại request khi gặp lỗi mạng (IOException) hoặc Server (5xx)
 */
class RetryInterceptor(
    private val maxRetry: Int = 2
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.encodedPath
        
        var response: Response? = null
        var lastException: IOException? = null
        
        try {
            response = chain.proceed(request)
        } catch (e: IOException) {
            lastException = e
            Log.e("RETRY", "Initial request failed for $url: ${e.message}")
        }

        var tryCount = 0
        while (tryCount < maxRetry && (response == null || response.code >= 500)) {
            tryCount++
            
            // Đợi trước khi thử lại
            val waitTime = Math.pow(2.0, (tryCount - 1).toDouble()).toLong() * 1000
            Log.d("RETRY", "Retrying ($tryCount/$maxRetry) for $url in ${waitTime}ms...")
            
            try {
                Thread.sleep(waitTime)
            } catch (e: InterruptedException) {
                break
            }

            response?.close()
            try {
                response = chain.proceed(request)
                lastException = null
            } catch (e: IOException) {
                response = null
                lastException = e
                Log.e("RETRY", "Retry $tryCount failed for $url: ${e.message}")
            }
        }

        if (response != null) return response
        if (lastException != null) throw lastException
        
        // Trường hợp bất khả kháng, gọi lại chain lần cuối để ném exception chuẩn của OkHttp
        return chain.proceed(request)
    }
}
