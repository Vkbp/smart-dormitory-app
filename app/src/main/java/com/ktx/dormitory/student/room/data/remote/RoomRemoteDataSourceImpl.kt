package com.ktx.dormitory.student.room.data.remote

import com.ktx.dormitory.student.room.data.dto.request.RoomTransferRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRemoteDataSourceImpl @Inject constructor(
    private val api: RoomApiService
) : RoomRemoteDataSource {
    override suspend fun getMyRoom() = api.getMyRoom()

    override suspend fun submitTransferRequest(request: RoomTransferRequest) = 
        api.submitTransferRequest(request)

    override suspend fun getTransferHistory() = api.getTransferHistory()
}
