package com.ktx.dormitory.student.room.data.remote

import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.student.room.data.dto.request.RoomTransferRequest
import com.ktx.dormitory.student.room.data.dto.response.RoomInfoDto
import com.ktx.dormitory.student.room.data.dto.response.RoomTransferHistoryDto

interface RoomRemoteDataSource {
    suspend fun getMyRoom(): BaseResponse<RoomInfoDto>
    suspend fun submitTransferRequest(request: RoomTransferRequest): BaseResponse<Unit>
    suspend fun getTransferHistory(): BaseResponse<List<RoomTransferHistoryDto>>
}
