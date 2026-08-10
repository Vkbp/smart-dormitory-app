package com.ktx.dormitory.student.extension.domain.usecase

import com.ktx.dormitory.student.extension.domain.model.CheckEligibilityResult
import com.ktx.dormitory.student.extension.domain.repository.ExtensionRepository
import javax.inject.Inject

class CheckEligibilityUseCase @Inject constructor(
    private val repository: ExtensionRepository
) {
    suspend operator fun invoke(cccd: String): Result<CheckEligibilityResult> {
        return repository.checkEligibility(cccd)
    }
}
