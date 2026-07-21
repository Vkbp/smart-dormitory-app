package com.ktx.dormitory.student.face.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto
import com.ktx.dormitory.student.face.data.dto.response.VerificationAttemptDto
import retrofit2.Response
import java.io.File

/**
 * Interface datasource cho Face Registration.
 */
interface FaceRemoteDataSource {
    suspend fun registerFaceOnServer(studentId: String, faceImageUrl: String)
    suspend fun registerFaceMultipart(studentId: String, faceImageFile: File)
    suspend fun getMyFaceProfile(studentId: String): FaceProfileDto?
    suspend fun requestReplacement(studentId: String, faceImageFile: File)
    suspend fun getMyVerifications(studentId: String, page: Int, size: Int): Response<BaseResponse<PageResponse<VerificationAttemptDto>>>
}
