package com.ktx.dormitory.student.room.domain.usecase

import com.ktx.dormitory.student.room.domain.model.RoomTransferHistory
import com.ktx.dormitory.student.room.domain.repository.RoomRepository
import javax.inject.Inject

/**
 * UseCase to get student's room transfer request history.
 */
class GetTransferHistoryUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(): Result<List<RoomTransferHistory>> {
        return repository.getTransferHistory()
    }
}
