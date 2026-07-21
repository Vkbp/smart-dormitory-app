package com.ktx.dormitory.shared.profile.domain.usecase

import com.ktx.dormitory.shared.profile.data.dto.request.UpdateProfileRequest
import com.ktx.dormitory.shared.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(
        phone: String,
        permanentAddress: String,
        emergencyContact: String,
        fatherName: String,
        fatherPhone: String,
        motherName: String,
        motherPhone: String
    ): Result<Unit> {
        return profileRepository.updateProfile(
            UpdateProfileRequest(
                phone = phone,
                permanentAddress = permanentAddress,
                emergencyContact = emergencyContact,
                fatherName = fatherName,
                fatherPhone = fatherPhone,
                motherName = motherName,
                motherPhone = motherPhone
            )
        )
    }
}
