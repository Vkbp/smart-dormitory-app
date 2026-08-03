package com.ktx.dormitory.student.payment.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ktx.dormitory.core.network.toUserFriendlyMessage
import com.ktx.dormitory.student.payment.data.dto.request.OnlinePaymentRequestDto
import com.ktx.dormitory.student.payment.data.local.InvoiceDao
import com.ktx.dormitory.student.payment.data.mapper.*
import com.ktx.dormitory.student.payment.data.paging.PaymentHistoryPagingSource
import com.ktx.dormitory.student.payment.data.remote.PaymentApiService
import com.ktx.dormitory.student.payment.domain.model.*
import com.ktx.dormitory.student.payment.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val apiService: PaymentApiService,
    private val invoiceDao: InvoiceDao
) : PaymentRepository {

    override suspend fun getBillByApplication(applicationId: String): Result<Bill> {
        return try {
            val response = apiService.getBillByApplication(applicationId)
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getInvoices(): Result<List<Bill>> {
        return try {
            val response = apiService.getInvoices()
            if (response.success && response.data != null) {
                val bills = response.data.map { it.toDomain() }
                invoiceDao.insertInvoices(response.data.map { it.toEntity() })
                Result.success(bills)
            } else {
                val cached = invoiceDao.getAllInvoices().first()
                Result.success(cached.map { it.toDomain() })
            }
        } catch (e: Exception) {
            val cached = invoiceDao.getAllInvoices().first()
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.toDomain() })
            } else {
                Result.failure(Exception(e.toUserFriendlyMessage()))
            }
        }
    }

    override suspend fun createSmartQR(billId: String, amount: BigDecimal): Result<PaymentResult> {
        return try {
            // Quy chuẩn Mã Giao Dịch: SDMS + 8 ký tự đầu của billId (viết hoa)
            val transactionCode = "SDMS${billId.take(8).uppercase()}"
            
            val request = OnlinePaymentRequestDto(
                billId = billId,
                amount = amount,
                paymentMethod = "BANK_TRANSFER",
                transactionCode = transactionCode
            )
            val response = apiService.createSmartQR(request)
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override fun getPaymentHistoryPaging(): Flow<PagingData<Bill>> {
        return Pager(
            config = PagingConfig(pageSize = 15, enablePlaceholders = false),
            pagingSourceFactory = { PaymentHistoryPagingSource(apiService) }
        ).flow
    }

    override suspend fun getPaymentInstructions(): Result<PaymentInstruction> {
        return try {
            val response = apiService.getPaymentInstructions()
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}
