package com.ktx.dormitory.student.extension.domain.usecase

import com.ktx.dormitory.student.extension.domain.model.ExtensionRequest
import com.ktx.dormitory.student.extension.domain.model.StayExtensionResponse
import com.ktx.dormitory.student.extension.domain.repository.ExtensionRepository
import javax.inject.Inject

class RequestExtensionUseCase @Inject constructor(
    val repository: ExtensionRepository
) {
    suspend operator fun invoke(reason: String, description: String): Result<StayExtensionResponse> {
        val request = ExtensionRequest(reason = reason, description = description)
        return repository.submitExtension(request)
    }
}
