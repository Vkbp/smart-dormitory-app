package com.ktx.dormitory.student.payment.domain.repository

import androidx.paging.PagingData
import com.ktx.dormitory.student.payment.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface PaymentRepository {
    /**
     * Lấy thông tin hóa đơn theo Application ID.
     */
    suspend fun getBillByApplication(applicationId: String): Result<Bill>

    /**
     * Lấy danh sách hóa đơn của tôi.
     */
    suspend fun getInvoices(): Result<List<Bill>>

    /**
     * Khởi tạo thanh toán Online (Smart QR).
     */
    suspend fun createSmartQR(billId: String, amount: BigDecimal): Result<PaymentResult>

    /**
     * Lấy lịch sử thanh toán phân trang.
     */
    fun getPaymentHistoryPaging(): Flow<PagingData<Bill>>

    /**
     * Lấy hướng dẫn thanh toán.
     */
    suspend fun getPaymentInstructions(): Result<PaymentInstruction>
}
