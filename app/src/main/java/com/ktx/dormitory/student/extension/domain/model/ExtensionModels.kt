package com.ktx.dormitory.student.extension.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExtensionRequest(
    val reason: String,
    val description: String
) : Parcelable

@Parcelize
data class StayExtensionResponse(
    val extensionId: String,
    val studentId: String,
    val studentCode: String,
    val fullName: String,
    val reason: String,
    val status: String,
    val currentBedId: String?,
    val currentBedCode: String?,
    val currentRoomCode: String?,
    val pdfUrl: String?,
    val description: String?,
    val rejectReason: String? = null
) : Parcelable

enum class ExtensionReason(val code: String, val label: String) {
    ROOM_LEADER("ROOM_LEADER", "Trưởng/Phó phòng"),
    POLICY_BENEFICIARY("POLICY_BENEFICIARY", "Diện chính sách"),
    ACADEMIC_EXCELLENCE("ACADEMIC_EXCELLENCE", "Thành tích tốt"),
    OTHER("OTHER", "Lý do khác")
}
