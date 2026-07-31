package com.ktx.dormitory.shared.profile.domain.usecase

import com.ktx.dormitory.shared.profile.data.dto.request.UpdateProfileRequest
import com.ktx.dormitory.shared.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(
        phone: String? = null,
        permanentAddress: String? = null,
        emergencyContact: String? = null,
        fatherName: String? = null,
        fatherPhone: String? = null,
        motherName: String? = null,
        motherPhone: String? = null,
        avatarUrl: String? = null
    ): Result<Unit> {
        return profileRepository.updateProfile(
            UpdateProfileRequest(
                phone = phone,
                permanentAddress = permanentAddress,
                emergencyContact = emergencyContact,
                fatherName = fatherName,
                fatherPhone = fatherPhone,
                motherName = motherName,
                motherPhone = motherPhone,
                avatarUrl = avatarUrl
            )
        )
    }
}
