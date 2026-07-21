package com.ktx.dormitory.student.checkout.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.student.checkout.domain.model.CheckoutRequest
import com.ktx.dormitory.student.checkout.domain.model.CheckoutResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CheckoutApiService {
    @POST("v1/students/checkout-requests")
    suspend fun submitCheckoutRequest(
        @Body request: CheckoutRequest
    ): Response<BaseResponse<CheckoutResponse>>

    @GET("v1/students/checkout-requests/my-requests")
    suspend fun getCheckoutHistory(): Response<BaseResponse<List<CheckoutResponse>>>
}
