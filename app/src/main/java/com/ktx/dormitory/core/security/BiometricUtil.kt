package com.ktx.dormitory.core.security

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Hàm bổ trợ để tìm FragmentActivity từ Context một cách an toàn.
 * Giúp giải quyết lỗi "Không tìm thấy FragmentActivity" trong Jetpack Compose.
 */
fun Context.findFragmentActivity(): FragmentActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is FragmentActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

// Kiểm tra thiết bị có hỗ trợ vân tay không
fun checkBiometricSupport(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    // SỬA: Chấp nhận cả BIOMETRIC_WEAK để demo được trên nhiều dòng máy (như Huawei, Xiaomi, máy cũ)
    val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
    
    val result = biometricManager.canAuthenticate(authenticators)
    return result == BiometricManager.BIOMETRIC_SUCCESS
}

@Composable
fun ShowBiometricPrompt(
    title: String,
    subtitle: String,
    cryptoObject: BiometricPrompt.CryptoObject? = null,
    onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
    onError: (() -> Unit)? = null
) {
    val context = LocalContext.current

    val activity = remember(context) { context.findFragmentActivity() }

    if (activity == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Lỗi hệ thống: Không tìm thấy FragmentActivity", Toast.LENGTH_SHORT).show()
            onError?.invoke()
        }
        return
    }

    val executor = remember(context) { ContextCompat.getMainExecutor(context) }

    LaunchedEffect(Unit) {
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess(result)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        Toast.makeText(context, "Lỗi: $errString", Toast.LENGTH_SHORT).show()
                    }
                    onError?.invoke()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(context, "Xác thực không thành công", Toast.LENGTH_SHORT).show()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setNegativeButtonText("Hủy")
            .build()

        try {
            if (cryptoObject != null) {
                biometricPrompt.authenticate(promptInfo, cryptoObject)
            } else {
                biometricPrompt.authenticate(promptInfo)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onError?.invoke()
        }
    }
}
