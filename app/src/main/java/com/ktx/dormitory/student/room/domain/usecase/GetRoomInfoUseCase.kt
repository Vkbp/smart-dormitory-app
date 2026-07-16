package com.ktx.dormitory.student.room.domain.usecase

import com.ktx.dormitory.student.room.domain.model.RoomInfo
import com.ktx.dormitory.student.room.domain.repository.RoomRepository
import javax.inject.Inject

class GetRoomInfoUseCase @Inject constructor(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(): Result<RoomInfo> {
        return roomRepository.getRoomInfo()
    }
}
