package com.ktx.dormitory.student.payment.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.student.payment.data.dto.response.InvoiceDto
import com.ktx.dormitory.student.payment.data.dto.response.TransactionDto
import com.ktx.dormitory.student.payment.data.dto.response.PaymentInstructionDto

interface PaymentRemoteDataSource {
    suspend fun getInvoices(): BaseResponse<List<InvoiceDto>>
    suspend fun verifyPayment(billId: String, amount: Double, paymentMethod: String, transactionCode: String): BaseResponse<Unit>
    suspend fun getPaymentHistory(): BaseResponse<List<TransactionDto>>
    suspend fun getPaymentInstructions(): PaymentInstructionDto
}

