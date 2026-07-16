package com.ktx.dormitory.student.room.domain.usecase

import com.ktx.dormitory.student.room.domain.repository.RoomRepository
import javax.inject.Inject

/**
 * UseCase for student to submit a room transfer request.
 */
class SubmitTransferRequestUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(reason: String, targetRoomId: String?): Result<Unit> {
        if (reason.isBlank()) {
            return Result.failure(Exception("Lý do không được để trống"))
        }
        return repository.submitTransferRequest(reason, targetRoomId)
    }
}
