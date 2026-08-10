package com.ktx.dormitory.shared.profile.domain.repository

import com.ktx.dormitory.shared.profile.data.dto.request.UpdateProfileRequest
import com.ktx.dormitory.shared.profile.domain.model.UserProfile

interface ProfileRepository {
    suspend fun getProfile(): Result<UserProfile>
    suspend fun updateProfile(request: UpdateProfileRequest): Result<Unit>
    suspend fun uploadAvatar(filePath: String): Result<String>
}

