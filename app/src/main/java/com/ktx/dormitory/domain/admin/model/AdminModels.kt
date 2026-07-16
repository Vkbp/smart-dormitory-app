package com.ktx.dormitory.domain.admin.model

import java.util.UUID

data class CheckInStudent(
    val assignmentId: UUID,
    val studentId: UUID,
    val studentName: String,
    val studentCode: String,
    val cccd: String,
    val portraitUrl: String?,
    val gender: String,
    val buildingName: String?,
    val roomName: String?,
    val bedName: String?
)

data class FaceProfile(
    val profileId: UUID,
    val studentId: UUID,
    val studentName: String?,
    val faceImageUrl: String?,
    val status: String,
    val rejectionReason: String?,
    val createdAt: String?
)
