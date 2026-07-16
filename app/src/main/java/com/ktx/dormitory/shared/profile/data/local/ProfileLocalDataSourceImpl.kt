package com.ktx.dormitory.shared.profile.data.local

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileLocalDataSourceImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
) : ProfileLocalDataSource {

    override fun getProfile(): Flow<UserProfileEntity?> = userProfileDao.getProfile()

    override suspend fun saveProfile(profile: UserProfileEntity) {
        userProfileDao.insertProfile(profile)
    }

    override suspend fun clearProfile() {
        userProfileDao.clearProfile()
    }
}
