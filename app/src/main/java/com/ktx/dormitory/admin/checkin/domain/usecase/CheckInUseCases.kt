package com.ktx.dormitory.admin.checkin.domain.usecase

import com.ktx.dormitory.admin.smartaccess.domain.repository.AdminRepository
import java.util.UUID
import javax.inject.Inject

class SearchStudentForCheckInUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(cccd: String) = repository.searchStudentForCheckIn(cccd)
}

class ConfirmCheckInUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(assignmentId: UUID) = repository.confirmCheckIn(assignmentId)
}

class AssignRfidUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(studentId: UUID, rfidCode: String) = 
        repository.assignRfid(studentId, rfidCode)
}
