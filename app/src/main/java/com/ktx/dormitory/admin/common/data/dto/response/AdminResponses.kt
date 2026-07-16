package com.ktx.dormitory.admin.common.data.dto.response

import java.util.UUID

data class CheckoutRequestResponseDto(
    val id: UUID,
    val studentId: UUID,
    val studentName: String?,
    val roomCode: String?,
    val intendedCheckoutDate: String?,
    val status: String,
    val rejectReason: String?,
    val createdAt: String?
)

data class StayExtensionResponseDto(
    val id: UUID,
    val studentId: UUID,
    val studentName: String?,
    val roomCode: String?,
    val extensionPeriodId: UUID?,
    val status: String,
    val rejectReason: String?,
    val createdAt: String?
)

data class CheckInSearchResponseDto(
    val assignmentId: UUID,
    val studentId: UUID,
    val studentName: String,
    val studentCode: String,
    val citizenId: String,
    val roomCode: String,
    val bedCode: String,
    val status: String
)

data class BroadcastResponse(
    val broadcastId: UUID,
    val title: String,
    val sentAt: String,
    val recipientCount: Int
)

data class BuildingResponseDto(
    val id: UUID,
    val name: String,
    val code: String,
    val status: String,
    val description: String?
)

data class GateResponseDto(
    val id: UUID,
    val gateName: String,
    val gateCode: String,
    val buildingId: UUID?,
    val status: String
)

data class DashboardStatsResponseDto(
    val pendingApplications: Int,
    val waitingForPayment: Int,
    val pendingCheckIn: Int,
    val occupiedAssignments: Int,
    val totalBuildings: Int,
    val totalFloors: Int,
    val totalRooms: Int,
    val totalBeds: Int
)
