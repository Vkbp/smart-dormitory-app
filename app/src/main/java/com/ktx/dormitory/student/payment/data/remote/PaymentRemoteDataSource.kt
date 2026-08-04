package com.ktx.dormitory.student.payment.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.student.payment.data.dto.response.BillDto
import com.ktx.dormitory.student.payment.data.dto.response.PaymentResponseDto
import com.ktx.dormitory.student.payment.data.dto.response.PaymentInstructionDto
import java.math.BigDecimal

interface PaymentRemoteDataSource {
    suspend fun getBillByApplication(applicationId: String): BaseResponse<BillDto>
    suspend fun getInvoices(): BaseResponse<List<BillDto>>
    suspend fun createSmartQR(billId: String, amount: BigDecimal, paymentMethod: String, transactionCode: String?): BaseResponse<PaymentResponseDto>
    suspend fun getPaymentHistoryPaged(page: Int, size: Int): BaseResponse<PageResponse<BillDto>>
    suspend fun getPaymentInstructions(billId: String?): BaseResponse<PaymentInstructionDto>
    suspend fun splitElectricBill(billId: String, nonPayingStudentIds: List<String>, amountPerStudent: BigDecimal): BaseResponse<Unit>
}
