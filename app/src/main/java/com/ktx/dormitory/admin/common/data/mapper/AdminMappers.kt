package com.ktx.dormitory.admin.common.data.mapper

import com.ktx.dormitory.admin.common.data.dto.response.CheckInSearchResponseDto
import com.ktx.dormitory.admin.common.data.dto.response.DashboardStatsResponseDto
import com.ktx.dormitory.admin.common.domain.model.CheckInStudent
import com.ktx.dormitory.admin.common.domain.model.DashboardStats
import com.ktx.dormitory.admin.common.domain.model.FaceProfile
import com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto

fun CheckInSearchResponseDto.toDomain() = CheckInStudent(
    assignmentId = assignmentId,
    studentId = studentId,
    studentName = studentName,
    studentCode = studentCode,
    cccd = citizenId,
    portraitUrl = null, // Backend doesn't provide it in this DTO yet
    gender = "MALE", // Default, as backend doesn't provide it
    buildingName = null,
    roomName = roomCode,
    bedName = bedCode
)

fun FaceProfileDto.toDomain() = FaceProfile(
    profileId = profileId,
    studentId = studentId,
    studentName = null,
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
