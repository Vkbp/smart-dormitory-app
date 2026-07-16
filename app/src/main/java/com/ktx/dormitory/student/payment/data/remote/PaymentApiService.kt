package com.ktx.dormitory.student.payment.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.student.payment.data.dto.response.InvoiceDto
import com.ktx.dormitory.student.payment.data.dto.response.TransactionDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PaymentApiService {
    @GET("v1/bills/me")
    suspend fun getInvoices(): BaseResponse<List<InvoiceDto>>

    @POST("v1/payments/online")
    suspend fun verifyPayment(@Body request: HashMap<String, Any>): BaseResponse<Unit>

    @GET("v1/bills/me")
    suspend fun getPaymentHistory(): BaseResponse<List<TransactionDto>>

    @GET("v1/public/payment-instructions")
    suspend fun getPaymentInstructions(): com.ktx.dormitory.student.payment.data.dto.response.PaymentInstructionDto
}

