package com.ktx.dormitory.student.face.domain.usecase

import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.student.face.data.dto.response.VerificationAttemptDto
import com.ktx.dormitory.student.face.domain.repository.FaceRepository
import javax.inject.Inject

class GetFaceVerificationsUseCase @Inject constructor(
    private val repository: FaceRepository
) {
    suspend operator fun invoke(studentId: String, page: Int, size: Int): Result<PageResponse<VerificationAttemptDto>> {
        return repository.getMyVerifications(studentId, page, size)
    }
}
