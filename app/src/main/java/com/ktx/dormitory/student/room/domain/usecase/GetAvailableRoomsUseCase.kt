package com.ktx.dormitory.student.room.domain.usecase

import com.ktx.dormitory.student.room.domain.model.RoomInfo
import com.ktx.dormitory.student.room.domain.repository.RoomRepository
import javax.inject.Inject

class GetAvailableRoomsUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(): Result<List<RoomInfo>> = repository.getAvailableRooms()
}
