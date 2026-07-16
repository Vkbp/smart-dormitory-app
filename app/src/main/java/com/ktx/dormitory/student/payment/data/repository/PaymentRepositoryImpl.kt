package com.ktx.dormitory.student.payment.data.repository

import com.ktx.dormitory.core.network.toUserFriendlyMessage
import com.ktx.dormitory.student.payment.data.local.InvoiceDao
import com.ktx.dormitory.student.payment.data.mapper.*
import com.ktx.dormitory.student.payment.data.remote.PaymentRemoteDataSource
import com.ktx.dormitory.student.payment.domain.model.*
import com.ktx.dormitory.student.payment.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val remoteDataSource: PaymentRemoteDataSource,
    private val invoiceDao: InvoiceDao
) : PaymentRepository {

    override suspend fun getInvoices(): Result<List<Invoice>> {
        return try {
            val response = remoteDataSource.getInvoices()
            if (response.success && response.data != null) {
                val invoices = response.data.map { it.toDomain() }
                invoiceDao.insertInvoices(response.data.map { it.toEntity() })
                Result.success(invoices)
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

    override suspend fun verifyPayment(billId: String, amount: Double, paymentMethod: String, transactionCode: String): Result<Unit> {
        return try {
            val response = remoteDataSource.verifyPayment(billId, amount, paymentMethod, transactionCode)
            if (response.success) Result.success(Unit) else Result.failure(Exception(response.message))
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getPaymentHistory(): Result<List<Transaction>> {
        return try {
            val response = remoteDataSource.getPaymentHistory()
            if (response.success && response.data != null) {
                Result.success(response.data.map { it.toDomain() })
            } else {
                Result.success(emptyList()) // Trả về danh sách trống thay vì lỗi nếu server ko có data
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getPaymentInstructions(): Result<PaymentInstruction> {
        return try {
            val response = remoteDataSource.getPaymentInstructions()
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
