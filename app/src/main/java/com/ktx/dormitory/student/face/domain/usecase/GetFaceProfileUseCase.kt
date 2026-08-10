package com.ktx.dormitory.student.face.domain.usecase

import com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto
import com.ktx.dormitory.student.face.domain.repository.FaceRepository
import javax.inject.Inject

class GetFaceProfileUseCase @Inject constructor(
    private val repository: FaceRepository
) {
    suspend operator fun invoke(studentId: String): Result<FaceProfileDto?> {
        return repository.getMyFaceProfile(studentId)
    }
}
