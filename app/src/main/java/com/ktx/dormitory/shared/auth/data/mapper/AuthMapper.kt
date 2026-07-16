package com.ktx.dormitory.shared.auth.data.mapper

import com.ktx.dormitory.shared.auth.data.dto.response.UserResponse
import com.ktx.dormitory.shared.auth.domain.model.UserData

/**
 * Chuyển đổi DTO thành Domain Model cho Auth
 */
fun UserResponse.toDomain(): UserData {
    val rawRole = this.role ?: "STUDENT"
    // Loại bỏ tiền tố ROLE_ nếu có (ví dụ: ROLE_STUDENT -> STUDENT)
    val cleanRole = if (rawRole.startsWith("ROLE_")) rawRole.substring(5) else rawRole
    
    return UserData(
        id = this.id,
        username = this.studentCode ?: this.email ?: "Unknown",
        role = cleanRole,
        fullName = this.fullName,
    )
}

fun UserResponse.toEntity() = com.ktx.dormitory.shared.profile.data.local.UserProfileEntity(
    id = id ?: "",
    studentCode = studentCode ?: "",
    fullName = fullName ?: "",
    cccd = cccd ?: "",
    email = email ?: "",
    phone = phone ?: "",
    faculty = faculty ?: "",
    academicYear = academicYear ?: "",
    permanentAddress = "",
    avatarUrl = null
)
