package com.ktx.dormitory.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SecurityUtils {
    private const val KEY_ALIAS = "face_embedding_key"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        val key = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (key != null) return key
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val spec = KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    fun encryptEmbedding(embedding: FloatArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val byteBuffer = ByteBuffer.allocate(embedding.size * 4)
        embedding.forEach { byteBuffer.putFloat(it) }
        val encryptedData = cipher.doFinal(byteBuffer.array())
        val iv = cipher.iv
        return ByteBuffer.allocate(iv.size + encryptedData.size).put(iv).put(encryptedData).array()
    }

    fun decryptEmbedding(encrypted: ByteArray): FloatArray {
        val iv = encrypted.sliceArray(0 until 12)
        val data = encrypted.sliceArray(12 until encrypted.size)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
        val decryptedData = cipher.doFinal(data)
        val byteBuffer = ByteBuffer.wrap(decryptedData)
        val result = FloatArray(decryptedData.size / 4)
        for (i in result.indices) { result[i] = byteBuffer.float }
        return result
    }
}
