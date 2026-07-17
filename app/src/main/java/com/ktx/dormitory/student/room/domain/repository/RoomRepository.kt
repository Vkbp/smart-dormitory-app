package com.ktx.dormitory.student.room.domain.repository

import com.ktx.dormitory.student.room.domain.model.RoomInfo
import com.ktx.dormitory.student.room.domain.model.RoomTransferHistory

interface RoomRepository {
    suspend fun getRoomInfo(): Result<RoomInfo>
    suspend fun getAvailableRooms(): Result<List<RoomInfo>>
    suspend fun submitTransferRequest(reason: String, targetRoomId: String?): Result<Unit>
    suspend fun getTransferHistory(): Result<List<RoomTransferHistory>>
}
