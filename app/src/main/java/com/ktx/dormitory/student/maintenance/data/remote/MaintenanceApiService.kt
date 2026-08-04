package com.ktx.dormitory.student.maintenance.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.shared.profile.data.dto.FileUploadResponse
import com.ktx.dormitory.student.maintenance.data.dto.request.CreateMaintenanceRequest
import com.ktx.dormitory.student.maintenance.data.dto.response.MaintenanceResponseDto
import okhttp3.MultipartBody
import retrofit2.http.*

interface MaintenanceApiService {
    @POST("v1/student/maintenance")
    suspend fun submitMaintenanceRequest(
        @Body request: CreateMaintenanceRequest
    ): BaseResponse<Unit>

    @GET("v1/student/maintenance/me")
    suspend fun getMaintenanceHistory(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): BaseResponse<PageResponse<MaintenanceResponseDto>>

    @Multipart
    @POST("v1/uploads/image")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Query("folder") folder: String = "sdms/maintenance"
    ): BaseResponse<FileUploadResponse>
}
