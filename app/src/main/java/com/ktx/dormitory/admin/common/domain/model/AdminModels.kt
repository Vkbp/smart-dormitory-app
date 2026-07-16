package com.ktx.dormitory.admin.common.domain.model

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

data class DashboardStats(
    val pendingApplications: Int,
    val waitingForPayment: Int,
    val pendingCheckIn: Int,
    val occupiedAssignments: Int,
    val totalBuildings: Int,
    val totalFloors: Int,
    val totalRooms: Int,
    val totalBeds: Int
)
