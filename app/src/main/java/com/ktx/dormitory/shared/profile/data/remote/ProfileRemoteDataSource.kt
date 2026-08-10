package com.ktx.dormitory.shared.profile.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.shared.profile.data.dto.FileUploadResponse
import com.ktx.dormitory.shared.profile.data.dto.request.*
import com.ktx.dormitory.shared.profile.data.dto.response.*
import okhttp3.MultipartBody

interface ProfileRemoteDataSource {
    suspend fun getDetailedProfile(): BaseResponse<StudentResponse>
    suspend fun updateProfile(request: UpdateProfileRequest): BaseResponse<StudentResponse>
    suspend fun uploadAvatar(file: MultipartBody.Part): BaseResponse<FileUploadResponse>
}
