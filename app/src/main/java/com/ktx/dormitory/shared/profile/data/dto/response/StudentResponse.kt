package com.ktx.dormitory.shared.profile.data.dto.response

import com.google.gson.annotations.SerializedName

/**
 * DTO cho thông tin chi tiết sinh viên.
 * Tương thích với StudentProfileResponse.java từ Backend.
 */
data class StudentResponse(
    @SerializedName("studentId") val id: String? = null,
    @SerializedName("studentCode") val studentCode: String? = null,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("cccd") val citizenId: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("faculty") val faculty: String? = null,
    @SerializedName("academicYear") val academicYear: String? = null,
    @SerializedName("fatherName") val fatherName: String? = null,
    @SerializedName("fatherPhone") val fatherPhone: String? = null,
    @SerializedName("motherName") val motherName: String? = null,
    @SerializedName("motherPhone") val motherPhone: String? = null,
    @SerializedName("emergencyContact") val emergencyContact: String? = null,
    @SerializedName("permanentAddress") val permanentAddress: String? = null,
    @SerializedName("avatarUrl") val avatarUrl: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("rfidCode") val rfidCode: String? = null,
    
    // Các trường dưới đây có thể không có trong StudentProfileResponse của Backend
    // nhưng được giữ lại để tương thích với UI và tránh lỗi biên dịch.
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("birthDate") val birthDate: String? = null,
    @SerializedName("course") val course: String? = null,
    @SerializedName("role") val role: String? = null
)
