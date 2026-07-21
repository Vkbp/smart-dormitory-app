package com.ktx.dormitory.admin.common.data.mapper

import com.ktx.dormitory.admin.common.data.dto.response.CheckInSearchResponseDto
import com.ktx.dormitory.admin.common.data.dto.response.DashboardStatsResponseDto
import com.ktx.dormitory.admin.common.domain.model.CheckInStudent
import com.ktx.dormitory.admin.common.domain.model.DashboardStats
import com.ktx.dormitory.admin.common.domain.model.FaceProfile
import com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto
import java.util.UUID

fun CheckInSearchResponseDto.toDomain() = CheckInStudent(
    assignmentId = assignmentId ?: UUID.randomUUID(), // Should not be null if search was successful
    studentId = studentId,
    studentName = studentName ?: "N/A",
    studentCode = studentCode ?: "N/A",
    cccd = citizenId ?: "N/A",
    portraitUrl = portraitUrl,
    gender = gender ?: "MALE",
    buildingName = buildingName,
    roomName = roomName,
    bedName = bedName
)

fun FaceProfileDto.toDomain() = FaceProfile(
    profileId = profileId ?: UUID.randomUUID(),
    studentId = studentId ?: UUID.randomUUID(),
    studentCode = studentCode,
    fullName = fullName,
    faceImageUrl = faceImageUrl,
    status = status,
    rejectionReason = rejectionReason,
    createdAt = createdAt
)

fun DashboardStatsResponseDto.toDomain() = DashboardStats(
    pendingApplications = pendingApplications,
    waitingForPayment = waitingForPayment,
    pendingCheckIn = pendingCheckIn,
    occupiedAssignments = occupiedAssignments,
    totalBuildings = totalBuildings,
    totalFloors = totalFloors,
    totalRooms = totalRooms,
    totalBeds = totalBeds
)
