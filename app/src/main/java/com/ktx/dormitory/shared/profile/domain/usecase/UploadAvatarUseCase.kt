package com.ktx.dormitory.shared.profile.domain.usecase

import com.ktx.dormitory.shared.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UploadAvatarUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(filePath: String): Result<String> {
        return profileRepository.uploadAvatar(filePath)
    }
}
