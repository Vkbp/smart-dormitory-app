package com.ktx.dormitory.student.room.domain.repository

import com.ktx.dormitory.student.room.domain.model.RoomInfo
import com.ktx.dormitory.student.room.domain.model.RoomTransferHistory
import com.ktx.dormitory.student.room.domain.model.Roommate
import com.ktx.dormitory.student.room.domain.model.UtilityReading

interface RoomRepository {
    suspend fun getRoomInfo(): Result<RoomInfo>
    suspend fun getAvailableRooms(): Result<List<RoomInfo>>
    suspend fun submitTransferRequest(reason: String, targetRoomId: String?): Result<Unit>
    suspend fun getTransferHistory(): Result<List<RoomTransferHistory>>
    suspend fun cancelTransferRequest(id: Long): Result<Unit>
    suspend fun getRoommates(): Result<List<Roommate>>
    suspend fun getRoomUtilities(): Result<List<UtilityReading>>
}
