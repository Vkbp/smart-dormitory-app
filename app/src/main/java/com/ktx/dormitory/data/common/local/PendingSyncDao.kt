package com.ktx.dormitory.data.common.local

import androidx.room.*

@Dao
interface PendingSyncDao {
    @Query("SELECT * FROM pending_sync WHERE syncStatus != 'COMPLETED' AND syncStatus != 'FAILED' ORDER BY createdAt ASC")
    suspend fun getNotCompletedActions(): List<PendingSyncEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: PendingSyncEntity)

    @Update
    suspend fun updateAction(action: PendingSyncEntity)

    @Delete
    suspend fun deleteAction(action: PendingSyncEntity)
}
