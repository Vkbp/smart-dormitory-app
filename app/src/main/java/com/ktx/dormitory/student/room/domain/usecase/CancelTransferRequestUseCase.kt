package com.ktx.dormitory.student.room.domain.usecase

import com.ktx.dormitory.student.room.domain.repository.RoomRepository
import javax.inject.Inject

class CancelTransferRequestUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return repository.cancelTransferRequest(id)
    }
}
