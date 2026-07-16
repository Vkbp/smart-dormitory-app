package com.ktx.dormitory.shared.auth.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model đại diện cho thông tin người dùng trong ứng dụng
 */
@Parcelize
data class UserData(
    val id: String? = null,
    val username: String,
    val role: String? = null,
    val fullName: String? = null
) : Parcelable

/**
 * Các lỗi liên quan đến xác thực
 */
sealed class AuthError : Exception() {
    data object InvalidCredentials : AuthError()
    data object NetworkError : AuthError()
    data object SessionExpired : AuthError()
    data class UnknownError(override val message: String) : AuthError()
}
