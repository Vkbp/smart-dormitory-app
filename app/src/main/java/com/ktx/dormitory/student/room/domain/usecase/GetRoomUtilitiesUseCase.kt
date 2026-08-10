package com.ktx.dormitory.student.room.domain.usecase

import com.ktx.dormitory.student.room.domain.model.UtilityReading
import com.ktx.dormitory.student.room.domain.repository.RoomRepository
import javax.inject.Inject

class GetRoomUtilitiesUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(): Result<List<UtilityReading>> {
        return repository.getRoomUtilities()
    }
}
