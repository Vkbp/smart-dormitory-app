package com.ktx.dormitory.shared.profile.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model hồ sơ người dùng trong Domain Layer
 */
@Parcelize
data class UserProfile(
    val id: String? = null,
    val studentCode: String? = null,
    val fullName: String? = null,
    val citizenId: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val faculty: String? = null,
    val academicYear: String? = null,
    val fatherName: String? = null,
    val fatherPhone: String? = null,
    val motherName: String? = null,
    val motherPhone: String? = null,
    val emergencyContact: String? = null,
    val permanentAddress: String? = null,
    val avatarUrl: String? = null,
    val status: String? = null,
    val rfidCode: String? = null,
    val gender: String? = null,
    val birthDate: String? = null,
    val course: String? = null,
    val roomRole: String? = null
) : Parcelable

/**
 * Model yêu cầu cập nhật hồ sơ
 */
@Parcelize
data class UpdateProfileRequest(
    val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val fatherName: String? = null,
    val fatherPhone: String? = null,
    val motherName: String? = null,
    val motherPhone: String? = null,
    val emergencyContact: String? = null,
    val permanentAddress: String? = null,
    val avatarUrl: String? = null
) : Parcelable
