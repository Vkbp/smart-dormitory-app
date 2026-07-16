package com.ktx.dormitory.shared.auth.domain.usecase

import com.ktx.dormitory.shared.auth.domain.repository.AuthRepository
import javax.inject.Inject

class CheckSessionUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    fun hasLocalSession(): Boolean = repository.hasLocalSession()
}
