package com.ktx.dormitory.student.extension.domain.repository

import com.ktx.dormitory.student.extension.domain.model.CheckEligibilityResult
import com.ktx.dormitory.student.extension.domain.model.ExtensionRequest
import com.ktx.dormitory.student.extension.domain.model.StayExtensionResponse

interface ExtensionRepository {
    suspend fun submitExtension(request: ExtensionRequest): Result<StayExtensionResponse>
    suspend fun isExtensionPeriodActive(): Result<Boolean>
    suspend fun getMyExtensionApplication(): Result<StayExtensionResponse>
    suspend fun checkEligibility(cccd: String): Result<CheckEligibilityResult>
}
