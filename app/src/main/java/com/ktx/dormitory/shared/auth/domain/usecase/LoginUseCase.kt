package com.ktx.dormitory.shared.auth.domain.usecase

import com.ktx.dormitory.shared.auth.domain.model.UserData
import com.ktx.dormitory.shared.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(usernameOrEmail: String, password: String): Result<UserData> {
        val result = authRepository.login(usernameOrEmail, password)
        if (result.isSuccess) {
            authRepository.saveLoginStatus(true)
        }
        return result
    }
}
