package com.ktx.dormitory.core.common

import com.ktx.dormitory.BuildConfig

object Constants {
    val BASE_URL: String = BuildConfig.BASE_URL

    /**
     * Timeout 5s là điểm cân bằng giữa Reliability và UX.
     * Hệ thống sẽ tự động retry nên tổng thời gian chờ tối đa khoảng 15s.
     */
    const val NETWORK_TIMEOUT = 5L // giây

    /**
     * DB_PASSPHRASE for SQLCipher.
     * WARNING: This should be managed via KeyStore or retrieved securely in a production app.
     * Hardcoding it here is only for demonstration/academic purposes.
     */
    const val DB_PASSPHRASE = "sdms_secure_passphrase_v1"

    // Tự động lấy Hostname từ BASE_URL để SSL Pinning (nếu có) dùng sau này
    val SERVER_HOSTNAME: String by lazy {
        BASE_URL.replace("https://", "").replace("http://", "").split(":")[0].split("/")[0]
    }
    /**
     * SSL Pinning configuration (SEC-02, SEC-05).
     * IMPORTANT: These are placeholders. In production, use:
     * 'openssl x509 -in cert.crt -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | base64'
     */
    val SERVER_PINS = arrayOf(
        "sha256/7n7Fk0z1/placeholder/actual/hash/required/1=", 
        "sha256/8o8Gl1a2/placeholder/actual/hash/required/2="
    )
}
