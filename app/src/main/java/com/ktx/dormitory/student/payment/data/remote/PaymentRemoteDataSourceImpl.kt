package com.ktx.dormitory.student.payment.data.remote

import com.ktx.dormitory.student.payment.data.dto.request.OnlinePaymentRequestDto
import com.ktx.dormitory.student.payment.data.dto.response.BillDto
import com.ktx.dormitory.student.payment.data.dto.response.PaymentResponseDto
import com.ktx.dormitory.student.payment.data.dto.response.PaymentInstructionDto
import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.core.common.PageResponse
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRemoteDataSourceImpl @Inject constructor(
    private val api: PaymentApiService
) : PaymentRemoteDataSource {
    
    override suspend fun getBillByApplication(applicationId: String): BaseResponse<BillDto> {
        return api.getBillByApplication(applicationId)
    }

    override suspend fun getInvoices(): BaseResponse<List<BillDto>> {
        return api.getInvoices()
    }

    override suspend fun createSmartQR(billId: String, amount: BigDecimal, paymentMethod: String, transactionCode: String?): BaseResponse<PaymentResponseDto> {
        return api.createSmartQR(
            OnlinePaymentRequestDto(
                billId = billId,
                amount = amount,
                paymentMethod = paymentMethod,
                transactionCode = transactionCode
            )
        )
    }

    override suspend fun getPaymentHistoryPaged(page: Int, size: Int): BaseResponse<PageResponse<BillDto>> {
        return api.getPaymentHistoryPaged(page, size)
    }

    override suspend fun getPaymentInstructions(billId: String?): BaseResponse<PaymentInstructionDto> {
        return api.getPaymentInstructions(billId)
    }

    override suspend fun splitElectricBill(
        billId: String,
        nonPayingStudentIds: List<String>,
        amountPerStudent: BigDecimal
    ): BaseResponse<Unit> {
        return api.splitElectricBill(billId, SplitBillRequest(nonPayingStudentIds, amountPerStudent))
    }
}
