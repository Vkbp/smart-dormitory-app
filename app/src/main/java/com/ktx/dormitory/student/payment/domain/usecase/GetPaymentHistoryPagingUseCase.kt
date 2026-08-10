package com.ktx.dormitory.student.payment.domain.usecase

import androidx.paging.PagingData
import com.ktx.dormitory.student.payment.domain.model.Bill
import com.ktx.dormitory.student.payment.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPaymentHistoryPagingUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    operator fun invoke(): Flow<PagingData<Bill>> {
        return repository.getPaymentHistoryPaging()
    }
}
