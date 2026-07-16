package com.ktx.dormitory.shared.profile.domain.usecase

import com.ktx.dormitory.shared.profile.data.dto.request.UpdateProfileRequest
import com.ktx.dormitory.shared.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(phone: String, email: String): Result<Unit> {
        return profileRepository.updateProfile(
            UpdateProfileRequest(
                phone = phone,
                email = email
            )
        )
    }
}

