package com.ktx.dormitory.student.access.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurfewRequestDao {
    @Query("SELECT * FROM curfew_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<CurfewRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<CurfewRequestEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: CurfewRequestEntity)

    @Query("DELETE FROM curfew_requests")
    suspend fun clearAll()
}
