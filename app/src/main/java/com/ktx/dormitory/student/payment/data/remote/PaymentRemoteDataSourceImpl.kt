package com.ktx.dormitory.student.payment.data.remote

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRemoteDataSourceImpl @Inject constructor(
    private val api: PaymentApiService
) : PaymentRemoteDataSource {
    override suspend fun getInvoices() = api.getInvoices()

    override suspend fun verifyPayment(billId: String, amount: Double, paymentMethod: String, transactionCode: String) =
        api.verifyPayment(
            hashMapOf(
                "billId" to billId,
                "amount" to amount,
                "paymentMethod" to paymentMethod,
                "transactionCode" to transactionCode
            )
        )

    override suspend fun getPaymentHistory() = api.getPaymentHistory()

    override suspend fun getPaymentInstructions() = api.getPaymentInstructions()
}
