package com.ktx.dormitory.student.face.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto
import com.ktx.dormitory.student.face.data.dto.response.VerificationAttemptDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface FaceApiService {
    /**
     * Đăng ký khuôn mặt bằng file ảnh (Multipart)
     * Khớp với FaceStudentController trong Backend.
     */
    @Multipart
    @POST("v1/students/me/face")
    suspend fun registerFace(
        @Part file: MultipartBody.Part
    ): Response<BaseResponse<UUID>>

    /**
     * Lấy hồ sơ khuôn mặt hiện tại
     */
    @GET("v1/students/me/face")
    suspend fun getMyFaceProfile(): Response<BaseResponse<FaceProfileDto>>

    /**
     * Gửi yêu cầu thay đổi khuôn mặt
     */
    @Multipart
    @POST("v1/students/me/face/replacements")
    suspend fun requestReplacement(
        @Part file: MultipartBody.Part
    ): Response<BaseResponse<Unit>>

    /**
     * Xem lịch sử các lần xác thực khuôn mặt
     */
    @GET("v1/students/me/face/verifications")
    suspend fun getMyVerifications(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<BaseResponse<PageResponse<VerificationAttemptDto>>>
}
