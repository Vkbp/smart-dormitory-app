package com.ktx.dormitory.student.checkout.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.student.checkout.data.dto.request.CreateCheckoutRequestDto
import com.ktx.dormitory.student.checkout.data.dto.response.CheckoutResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CheckoutApiService {
    @POST("v1/students/checkout-requests")
    suspend fun submitCheckoutRequest(
        @Body request: CreateCheckoutRequestDto
    ): Response<BaseResponse<CheckoutResponseDto>>

    @GET("v1/students/checkout-requests")
    suspend fun getCheckoutHistory(): Response<BaseResponse<List<CheckoutResponseDto>>>
}
