package com.ktx.dormitory.shared.profile.domain.usecase

import com.ktx.dormitory.shared.profile.domain.model.UserProfile
import com.ktx.dormitory.shared.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(): Result<UserProfile> {
        return profileRepository.getProfile()
    }
}
