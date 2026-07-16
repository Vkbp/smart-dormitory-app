package com.ktx.dormitory.student.face.data.remote

import com.ktx.dormitory.student.face.data.remote.FaceApiService
import com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto
import com.ktx.dormitory.student.face.data.dto.response.VerificationAttemptDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

/**
 * Impl gửi faceImageUrl hoặc File lên Backend qua AccessApiService.
 */
class FaceRemoteDataSourceImpl @Inject constructor(
    private val faceApi: FaceApiService
) : FaceRemoteDataSource {
    override suspend fun registerFaceOnServer(studentId: String, faceImageUrl: String) {
        // Obsolete URL-based registration removed to match Backend
    }

    override suspend fun registerFaceMultipart(
        studentId: String,
        faceImageFile: File
    ) {
        val requestFile = faceImageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", faceImageFile.name, requestFile)
        
        val response = faceApi.registerFace(body)
        if (!response.success) {
            val errorMsg = response.message ?: "Unknown AI error"
            throw Exception(if (errorMsg.contains("face", ignoreCase = true)) "AI_ERROR: $errorMsg" else errorMsg)
        }
    }

    override suspend fun getMyFaceProfile(studentId: String): FaceProfileDto? {
        val response = faceApi.getMyFaceProfile()
        if (response.success && response.data != null) {
            return response.data
        } else {
            // Log lỗi nghiệp vụ nếu cần
            return null
        }
    }

    override suspend fun requestReplacement(studentId: String, faceImageFile: File) {
        val requestFile = faceImageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", faceImageFile.name, requestFile)
        
        val response = faceApi.requestReplacement(body)
        if (!response.success) {
            val errorMsg = response.message ?: "Unknown error"
            val message = if (errorMsg.contains("face", ignoreCase = true)) {
                "AI_ERROR: No face detected or low quality"
            } else {
                errorMsg
            }
            throw Exception(message)
        }
    }

    override suspend fun getMyVerifications(
        studentId: String,
        page: Int,
        size: Int
    ): com.ktx.dormitory.core.common.BaseResponse<com.ktx.dormitory.core.common.PageResponse<VerificationAttemptDto>> {
        return faceApi.getMyVerifications(page, size)
    }
}

