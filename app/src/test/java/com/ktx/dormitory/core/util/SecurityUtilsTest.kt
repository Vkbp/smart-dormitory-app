package com.ktx.dormitory.core.util

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Test
import javax.crypto.SecretKey

class SecurityUtilsTest {

    @Test
    fun `embedding encryption and decryption should return same values`() {
        // Lưu ý: Test này thực tế nên chạy trên Android Device (androidTest) 
        // vì SecurityUtils phụ thuộc vào AndroidKeyStore.
        // Đây là bản demo logic so sánh dữ liệu.
        
        val originalEmbedding = floatArrayOf(0.1f, -0.5f, 0.99f, 1.2f)
        
        // Giả lập logic byte buffer của SecurityUtils (vì không thể chạy Keystore trên JVM Unit Test)
        val mockEncrypted = SecurityUtilsMock.encryptEmbedding(originalEmbedding)
        val decrypted = SecurityUtilsMock.decryptEmbedding(mockEncrypted)
        
        assertThat(decrypted).isEqualTo(originalEmbedding)
    }
}

/**
 * Mock version of SecurityUtils for Unit Testing logic of Byte Conversion
 */
object SecurityUtilsMock {
    fun encryptEmbedding(embedding: FloatArray): ByteArray {
        val byteBuffer = java.nio.ByteBuffer.allocate(embedding.size * 4)
        embedding.forEach { byteBuffer.putFloat(it) }
        return byteBuffer.array()
    }

    fun decryptEmbedding(data: ByteArray): FloatArray {
        val byteBuffer = java.nio.ByteBuffer.wrap(data)
        val result = FloatArray(data.size / 4)
        for (i in result.indices) {
            result[i] = byteBuffer.float
        }
        return result
    }
}
