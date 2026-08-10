package com.ktx.dormitory.domain.settings.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getFaceThreshold(): Flow<Float>
    suspend fun setFaceThreshold(threshold: Float)
}
