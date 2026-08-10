package com.ktx.dormitory.student.access.domain.usecase

import com.ktx.dormitory.student.access.domain.model.CurfewRequest
import com.ktx.dormitory.student.access.domain.repository.AccessRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCurfewRequestsUseCase @Inject constructor(
    private val repository: AccessRepository
) {
    operator fun invoke(): Flow<List<CurfewRequest>> = repository.curfewRequests
}
