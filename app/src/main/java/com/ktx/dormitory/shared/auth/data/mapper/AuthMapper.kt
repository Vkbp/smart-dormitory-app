package com.ktx.dormitory.shared.auth.data.mapper

import com.ktx.dormitory.shared.auth.data.dto.response.UserResponse
import com.ktx.dormitory.shared.auth.domain.model.UserData

/**
 * Chuyển đổi DTO thành Domain Model cho Auth
 */
fun UserResponse.toDomain(): UserData {
    val rawRoomRole = this.roomRole ?: "MEMBER"
    
    return UserData(
        id = this.id,
        username = this.studentCode ?: this.email ?: "Unknown",
        role = "STUDENT", // Mặc định role hệ thống là STUDENT cho route này
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
