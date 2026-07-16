package com.ktx.dormitory.student.extension.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.student.extension.data.dto.response.RegistrationPeriodDto
import com.ktx.dormitory.student.extension.data.dto.response.CheckEligibilityResponseDto
import com.ktx.dormitory.student.extension.data.dto.request.CheckEligibilityRequestDto
import com.ktx.dormitory.student.extension.domain.model.ExtensionRequest
import com.ktx.dormitory.student.extension.domain.model.StayExtensionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ExtensionApiService {
    @POST("v1/students/extensions")
    suspend fun submitExtension(
        @Body request: ExtensionRequest,
    ): Response<BaseResponse<StayExtensionResponse>>

    @GET("v1/registrations/active")
    suspend fun getActivePeriod(): Response<BaseResponse<RegistrationPeriodDto>>

    @GET("v1/students/extensions/my-application")
    suspend fun getMyExtensionApplication(): Response<BaseResponse<StayExtensionResponse>>

    @POST("v1/registrations/check-eligibility")
    suspend fun checkEligibility(@Body request: CheckEligibilityRequestDto): Response<BaseResponse<CheckEligibilityResponseDto>>
}

