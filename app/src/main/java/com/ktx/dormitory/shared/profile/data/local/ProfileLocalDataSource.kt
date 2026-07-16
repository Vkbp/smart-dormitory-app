package com.ktx.dormitory.shared.profile.data.local

import kotlinx.coroutines.flow.Flow

interface ProfileLocalDataSource {
    fun getProfile(): Flow<UserProfileEntity?>
    suspend fun saveProfile(profile: UserProfileEntity)
    suspend fun clearProfile()
}
