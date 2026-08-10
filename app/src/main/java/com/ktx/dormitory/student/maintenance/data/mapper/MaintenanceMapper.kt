package com.ktx.dormitory.student.maintenance.data.mapper

import com.ktx.dormitory.student.maintenance.data.dto.response.MaintenanceResponseDto
import com.ktx.dormitory.student.maintenance.domain.model.MaintenanceRequest
import com.ktx.dormitory.student.maintenance.domain.model.MaintenanceStatus

fun MaintenanceResponseDto.toDomain(): MaintenanceRequest {
    return MaintenanceRequest(
        id = id,
        roomId = roomId,
        roomCode = roomCode,
        description = description,
        imageUrl = imageUrl,
        status = when (status?.uppercase()) {
            "PENDING" -> MaintenanceStatus.PENDING
            "IN_PROGRESS" -> MaintenanceStatus.IN_PROGRESS
            "DONE" -> MaintenanceStatus.DONE
            "REJECTED" -> MaintenanceStatus.REJECTED
            else -> MaintenanceStatus.UNKNOWN
        },
        createdAt = createdAt
    )
}
