package com.ktx.dormitory.student.face.domain.repository

import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto
import com.ktx.dormitory.student.face.data.dto.response.VerificationAttemptDto
import java.io.File

interface FaceRepository {
    suspend fun registerFace(studentId: String, name: String, faceImageFile: File): Result<Unit>
    
    suspend fun getMyFaceProfile(studentId: String): Result<FaceProfileDto?>
    suspend fun requestReplacement(studentId: String, faceImageFile: File): Result<Unit>
    suspend fun getMyVerifications(studentId: String, page: Int, size: Int): Result<PageResponse<VerificationAttemptDto>>
}
