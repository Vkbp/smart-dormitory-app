package com.ktx.dormitory.admin.extension.domain.usecase

import com.ktx.dormitory.admin.smartaccess.domain.repository.AdminRepository
import java.util.UUID
import javax.inject.Inject

class GetStayExtensionsUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(status: String?, page: Int, size: Int) = repository.getStayExtensions(status, page, size)
}

class ReviewStayExtensionUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(id: UUID, status: String, rejectReason: String?) = 
        repository.reviewStayExtension(id, status, rejectReason)
}

class GetDetailedStudentProfileUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(studentId: UUID) = repository.getStudentProfile(studentId)
}
