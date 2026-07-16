package com.ktx.dormitory.shared.profile.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.shared.profile.data.dto.FileUploadResponse
import com.ktx.dormitory.shared.profile.data.dto.request.*
import com.ktx.dormitory.shared.profile.data.dto.response.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ProfileApiService {
    @GET("v1/students/me")
    suspend fun getDetailedProfile(): BaseResponse<StudentResponse>

    @PATCH("v1/students/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): BaseResponse<StudentResponse>

    @Multipart
    @POST("v1/uploads/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): BaseResponse<FileUploadResponse>
}
