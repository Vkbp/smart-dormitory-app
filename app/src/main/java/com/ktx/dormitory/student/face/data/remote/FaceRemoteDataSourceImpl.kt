package com.ktx.dormitory.student.face.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto
import com.ktx.dormitory.student.face.data.dto.response.VerificationAttemptDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

/**
 * Impl gửi faceImageUrl hoặc File lên Backend qua FaceApiService.
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
        val baseResponse = response.body()
        if (response.isSuccessful && baseResponse != null) {
            if (!baseResponse.success) {
                val errorMsg = baseResponse.message ?: "Unknown AI error"
                throw Exception(if (errorMsg.contains("face", ignoreCase = true)) "AI_ERROR: $errorMsg" else errorMsg)
            }
        } else {
            throw Exception("Lỗi kết nối server: ${response.code()}")
        }
    }

    override suspend fun getMyFaceProfile(studentId: String): FaceProfileDto? {
        val response = faceApi.getMyFaceProfile()
        val baseResponse = response.body()
        if (response.isSuccessful && baseResponse != null && baseResponse.success) {
            return baseResponse.data
        }
        return null
    }

    override suspend fun requestReplacement(studentId: String, faceImageFile: File) {
        val requestFile = faceImageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", faceImageFile.name, requestFile)
        
        val response = faceApi.requestReplacement(body)
        val baseResponse = response.body()
        if (response.isSuccessful && baseResponse != null) {
            if (!baseResponse.success) {
                val errorMsg = baseResponse.message ?: "Unknown error"
                val message = if (errorMsg.contains("face", ignoreCase = true)) {
                    "AI_ERROR: No face detected or low quality"
                } else {
                    errorMsg
                }
                throw Exception(message)
            }
        } else {
            throw Exception("Lỗi kết nối server: ${response.code()}")
        }
    }

    override suspend fun getMyVerifications(
        studentId: String,
        page: Int,
        size: Int
    ): Response<BaseResponse<PageResponse<VerificationAttemptDto>>> {
        return faceApi.getMyVerifications(page, size)
    }
}
