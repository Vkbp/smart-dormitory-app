package com.ktx.dormitory.student.face.domain.usecase

import com.ktx.dormitory.student.face.domain.repository.FaceRepository
import java.io.File
import javax.inject.Inject

class RequestFaceReplacementUseCase @Inject constructor(
    private val repository: FaceRepository
) {
    suspend operator fun invoke(studentId: String, faceImageFile: File): Result<Unit> {
        return repository.requestReplacement(studentId, faceImageFile)
    }
}
