package com.ktx.dormitory.student.room.data.mapper

import com.ktx.dormitory.student.room.data.dto.response.RoomInfoDto
import com.ktx.dormitory.student.room.data.dto.response.RoomTransferHistoryDto
import com.ktx.dormitory.student.room.data.dto.response.UtilityReadingDto
import com.ktx.dormitory.student.room.data.remote.RoommateDto
import com.ktx.dormitory.student.room.domain.model.RoomInfo
import com.ktx.dormitory.student.room.domain.model.RoomTransferHistory
import com.ktx.dormitory.student.room.domain.model.Roommate
import com.ktx.dormitory.student.room.domain.model.UtilityReading

fun RoomInfoDto.toDomain(): RoomInfo {
    return RoomInfo(
        roomId = roomId,
        building = building,
        buildingName = buildingName,
        floor = floor,
        roomCode = roomCode,
        bedCode = bedCode,
        status = status,
        checkInAt = checkInAt,
        expectedCheckOutAt = expectedCheckOutAt,
        availableBeds = availableBeds
    )
}

fun RoomTransferHistoryDto.toDomain(): RoomTransferHistory {
    return RoomTransferHistory(
        id = id,
        reason = reason,
        currentRoomName = currentRoomName,
        targetRoomName = targetRoomName,
        status = status,
        adminNote = adminNote,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun RoommateDto.toDomain(): Roommate {
    return Roommate(
        id = id,
        studentCode = studentCode,
        fullName = fullName,
        avatarUrl = avatarUrl,
        bedCode = bedCode,
        roomRole = roomRole
    )
}

fun UtilityReadingDto.toDomain(): UtilityReading {
    return UtilityReading(
        id = id,
        oldReading = oldReading,
        newReading = newReading,
        readingDate = readingDate,
        type = type
    )
}
