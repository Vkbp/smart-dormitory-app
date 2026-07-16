package com.ktx.dormitory.shared.profile.data.mapper

import com.ktx.dormitory.shared.profile.data.local.UserProfileEntity
import com.ktx.dormitory.shared.profile.data.dto.response.StudentResponse
import com.ktx.dormitory.shared.profile.data.dto.request.UpdateProfileRequest as UpdateProfileDto
import com.ktx.dormitory.shared.profile.data.dto.request.UpdateProfileRequest
import com.ktx.dormitory.shared.profile.domain.model.UserProfile

fun StudentResponse.toDomain(): UserProfile {
    return UserProfile(
        id = id,
        studentCode = studentCode,
        fullName = fullName,
        citizenId = citizenId,
        email = email,
        phone = phone,
        faculty = faculty,
        academicYear = academicYear,
        fatherName = fatherName,
        fatherPhone = fatherPhone,
        motherName = motherName,
        motherPhone = motherPhone,
        emergencyContact = emergencyContact,
        permanentAddress = permanentAddress,
        avatarUrl = avatarUrl,
        status = status,
        rfidCode = rfidCode,
        gender = gender,
        birthDate = birthDate,
        course = course
    )
}

fun StudentResponse.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        id = id ?: "",
        studentCode = studentCode ?: "",
        fullName = fullName ?: "",
        cccd = citizenId ?: "",
        email = email ?: "",
        phone = phone ?: "",
        faculty = faculty ?: "",
        academicYear = academicYear ?: "",
        permanentAddress = permanentAddress ?: "",
        avatarUrl = avatarUrl,
        fatherName = fatherName,
        fatherPhone = fatherPhone,
        motherName = motherName,
        motherPhone = motherPhone,
        emergencyContact = emergencyContact,
        status = status,
        rfidCode = rfidCode
    )
}

fun UserProfileEntity.toDomain(): UserProfile {
    return UserProfile(
        id = id,
        studentCode = studentCode,
        fullName = fullName,
        citizenId = cccd,
        email = email,
        phone = phone,
        faculty = faculty,
        academicYear = academicYear,
        permanentAddress = permanentAddress,
        avatarUrl = avatarUrl,
        fatherName = fatherName,
        fatherPhone = fatherPhone,
        motherName = motherName,
        motherPhone = motherPhone,
        emergencyContact = emergencyContact,
        status = status,
        rfidCode = rfidCode
    )
}

fun UpdateProfileRequest.toDto(): UpdateProfileDto {
    return UpdateProfileDto(
        email = email,
        phone = phone,
        fatherName = fatherName,
        fatherPhone = fatherPhone,
        motherName = motherName,
        motherPhone = motherPhone,
        emergencyContact = emergencyContact,
        permanentAddress = permanentAddress,
        avatarUrl = avatarUrl
    )
}

