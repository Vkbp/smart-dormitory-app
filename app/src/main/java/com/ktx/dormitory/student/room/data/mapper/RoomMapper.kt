package com.ktx.dormitory.student.room.data.mapper

import com.ktx.dormitory.student.room.data.dto.response.RoomInfoDto
import com.ktx.dormitory.student.room.data.dto.response.RoomTransferHistoryDto
import com.ktx.dormitory.student.room.domain.model.RoomInfo
import com.ktx.dormitory.student.room.domain.model.RoomTransferHistory

fun RoomInfoDto.toDomain(): RoomInfo {
    return RoomInfo(
        building = building,
        floor = floor,
        roomCode = roomCode,
        bedCode = bedCode,
        status = status,
        checkInAt = checkInAt,
        expectedCheckOutAt = expectedCheckOutAt
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
