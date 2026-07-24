package com.ktx.dormitory.admin.common.data.dto.response

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class CheckoutRequestResponseDto(
    @SerializedName("requestId", alternate = ["id", "request_id"]) val id: UUID?,
    val studentId: UUID?,
    @SerializedName("studentCode", alternate = ["student_code", "studentNumber"]) val studentCode: String?,
    @SerializedName("fullName", alternate = ["full_name", "studentName"]) val fullName: String?,
    val roomCode: String?,
    val bedCode: String?,
    val intendedCheckoutDate: String?,
    val reason: String?,
    val bankName: String?,
    val bankAccountNumber: String?,
    val status: String,
    val rejectReason: String?,
    val createdAt: String?
)

data class StayExtensionResponseDto(
    @SerializedName("extensionId", alternate = ["id", "extension_id"]) val id: UUID?,
    val studentId: UUID?,
    @SerializedName("studentCode", alternate = ["student_code", "studentNumber"]) val studentCode: String?,
    @SerializedName("fullName", alternate = ["full_name", "studentName"]) val fullName: String?,
    @SerializedName("currentRoomCode", alternate = ["roomCode", "current_room_code"]) val roomCode: String?,
    val extensionPeriodId: UUID?,
    val status: String,
    val reason: String?,
    val rejectReason: String?,
    val contractPdfUrl: String?,
    val commitmentPdfUrl: String?,
    val createdAt: String?
)

data class CheckInSearchResponseDto(
    val assignmentId: UUID?,
    @SerializedName("studentId", alternate = ["student_id"]) val studentId: UUID?,
    val studentName: String?,
    val studentCode: String?,
    @SerializedName("cccd") val citizenId: String?,
    val gender: String?,
    val portraitUrl: String?,
    val buildingName: String?,
    val floorName: String?,
    val roomName: String?,
    val bedName: String?
)

data class BroadcastResponse(
    val broadcastId: UUID,
    val title: String,
    val sentAt: String,
    val recipientCount: Int
)

data class BuildingResponseDto(
    @SerializedName("buildingId", alternate = ["id"]) val id: UUID?,
    val name: String,
    val code: String,
    val status: String,
    val description: String?
)

data class GateResponseDto(
    @SerializedName("gateId", alternate = ["id"]) val id: UUID?,
    @SerializedName("name", alternate = ["gateName"]) val gateName: String,
    val gateCode: String?,
    val buildingId: UUID?,
    @SerializedName("active") val isActive: Boolean
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
