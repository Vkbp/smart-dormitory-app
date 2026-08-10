package com.ktx.dormitory.student.room.domain.usecase

import com.ktx.dormitory.student.room.domain.model.RoomInfo
import com.ktx.dormitory.student.room.domain.repository.RoomRepository
import javax.inject.Inject

data class GroupedAvailableRooms(
    val allRooms: List<RoomInfo>,
    val groupedByBuilding: Map<String, List<RoomInfo>>
)

class GetGroupedAvailableRoomsUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(): Result<GroupedAvailableRooms> {
        return repository.getAvailableRooms().map { rooms ->
            val grouped = rooms.groupBy { it.buildingName ?: "Khác" }
            GroupedAvailableRooms(rooms, grouped)
        }
    }
}
