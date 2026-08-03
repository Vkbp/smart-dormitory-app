package com.ktx.dormitory.student.room.data.remote

import com.google.gson.annotations.SerializedName
import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.student.room.data.dto.request.RoomTransferRequest
import com.ktx.dormitory.student.room.data.dto.response.RoomInfoDto
import com.ktx.dormitory.student.room.data.dto.response.RoomTransferHistoryDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RoomApiService {
    @GET("v1/student/room/current")
    suspend fun getMyRoom(): BaseResponse<RoomInfoDto>

    @GET("v1/student/rooms/available")
    suspend fun getAvailableRooms(): BaseResponse<List<RoomInfoDto>>

    @POST("v1/student/change-room")
    suspend fun submitTransferRequest(
        @Body request: RoomTransferRequest
    ): BaseResponse<Unit>

    @GET("v1/student/change-room")
    suspend fun getTransferHistory(): BaseResponse<List<RoomTransferHistoryDto>>

    @GET("v1/student/room/roommates")
    suspend fun getMyRoommates(): BaseResponse<List<RoommateDto>>
}

data class RoommateDto(
    @SerializedName("studentId") val id: String,
    @SerializedName("studentCode") val studentCode: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("avatarUrl") val avatarUrl: String? = null
)
