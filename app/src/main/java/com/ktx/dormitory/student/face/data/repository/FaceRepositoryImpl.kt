package com.ktx.dormitory.student.face.data.repository

import com.ktx.dormitory.core.network.toUserFriendlyMessage
import com.ktx.dormitory.student.face.data.remote.FaceRemoteDataSource
import com.ktx.dormitory.student.face.domain.repository.FaceRepository
import com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto
import com.ktx.dormitory.student.face.data.dto.response.VerificationAttemptDto
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FaceRepositoryImpl - Quản lý đăng ký và xác thực khuôn mặt.
 */
@Singleton
class FaceRepositoryImpl @Inject constructor(
    private val remoteDataSource: FaceRemoteDataSource
) : FaceRepository {

    override suspend fun registerFace(
        studentId: String,
        name: String,
        faceImageFile: File
    ): Result<Unit> {
        return try {
            // Theo backend mới: studentId gửi qua header (xử lý ở DataSource)
            // và file gửi qua Multipart. Name không cần thiết ở endpoint này.
            remoteDataSource.registerFaceMultipart(studentId, faceImageFile)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getMyFaceProfile(studentId: String): Result<FaceProfileDto?> {
        return try {
            Result.success(remoteDataSource.getMyFaceProfile(studentId))
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun requestReplacement(studentId: String, faceImageFile: File): Result<Unit> {
        return try {
            remoteDataSource.requestReplacement(studentId, faceImageFile)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getMyVerifications(
        studentId: String,
        page: Int,
        size: Int
    ): Result<com.ktx.dormitory.core.common.PageResponse<VerificationAttemptDto>> {
        return try {
            val response = remoteDataSource.getMyVerifications(studentId, page, size)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Lỗi tải lịch sử"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}

