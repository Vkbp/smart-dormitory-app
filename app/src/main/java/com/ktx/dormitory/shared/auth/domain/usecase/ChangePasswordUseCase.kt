package com.ktx.dormitory.shared.auth.domain.usecase

import com.ktx.dormitory.shared.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(oldPass: String, newPass: String): Result<Unit> {
        return authRepository.changePassword(oldPass, newPass)
    }
}
