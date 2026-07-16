package com.ktx.dormitory.shared.profile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val studentCode: String,
    val fullName: String,
    val cccd: String,
    val faculty: String,
    val academicYear: String,
    val phone: String,
    val email: String,
    val permanentAddress: String,
    val avatarUrl: String?,
    val fatherName: String? = null,
    val fatherPhone: String? = null,
    val motherName: String? = null,
    val motherPhone: String? = null,
    val emergencyContact: String? = null,
    val status: String? = null,
    val rfidCode: String? = null
)
