package com.ktx.dormitory.data.settings.repository

import com.ktx.dormitory.core.datastore.DataStoreManager
import com.ktx.dormitory.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : SettingsRepository {

    override fun getFaceThreshold(): Flow<Float> = dataStoreManager.faceThreshold

    override suspend fun setFaceThreshold(threshold: Float) {
        dataStoreManager.saveThreshold(threshold)
    }
}
