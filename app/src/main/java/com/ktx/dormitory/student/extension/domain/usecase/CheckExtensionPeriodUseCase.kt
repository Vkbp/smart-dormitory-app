package com.ktx.dormitory.student.extension.domain.usecase

import com.ktx.dormitory.student.extension.domain.repository.ExtensionRepository
import javax.inject.Inject

class CheckExtensionPeriodUseCase @Inject constructor(
    private val repository: ExtensionRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return repository.isExtensionPeriodActive()
    }
}
