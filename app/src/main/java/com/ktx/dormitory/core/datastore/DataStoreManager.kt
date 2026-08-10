package com.ktx.dormitory.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import androidx.datastore.preferences.core.floatPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = "settings")

object DataStoreKeys {
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val IS_PAID = booleanPreferencesKey("is_paid")
    val FACE_THRESHOLD = floatPreferencesKey("face_threshold")
}

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {
    
    val faceThreshold: Flow<Float> = context.dataStore.data.map { 
        it[DataStoreKeys.FACE_THRESHOLD] ?: 1.0f 
    }

    suspend fun saveThreshold(value: Float) {
        context.dataStore.edit { it[DataStoreKeys.FACE_THRESHOLD] = value }
    }
}
