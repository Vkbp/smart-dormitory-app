package com.ktx.dormitory.student.payment.domain.repository

import com.ktx.dormitory.student.payment.domain.model.Invoice
import com.ktx.dormitory.student.payment.domain.model.PaymentInstruction
import com.ktx.dormitory.student.payment.domain.model.Transaction

interface PaymentRepository {
    suspend fun getInvoices(): Result<List<Invoice>>
    suspend fun verifyPayment(billId: String, amount: Double, paymentMethod: String, transactionCode: String): Result<Unit>
    suspend fun getPaymentHistory(): Result<List<Transaction>>
    suspend fun getPaymentInstructions(): Result<PaymentInstruction>
}
