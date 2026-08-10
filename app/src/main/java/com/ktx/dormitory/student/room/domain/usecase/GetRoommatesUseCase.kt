package com.ktx.dormitory.student.room.domain.usecase

import com.ktx.dormitory.student.room.domain.model.Roommate
import com.ktx.dormitory.student.room.domain.repository.RoomRepository
import javax.inject.Inject

class GetRoommatesUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(): Result<List<Roommate>> {
        return repository.getRoommates()
    }
}
