package com.ktx.dormitory.student.room.data.repository

import com.ktx.dormitory.core.network.toUserFriendlyMessage
import com.ktx.dormitory.student.room.data.dto.request.RoomTransferRequest
import com.ktx.dormitory.student.room.data.mapper.toDomain
import com.ktx.dormitory.student.room.data.remote.RoomRemoteDataSource
import com.ktx.dormitory.student.room.domain.model.RoomInfo
import com.ktx.dormitory.student.room.domain.model.RoomTransferHistory
import com.ktx.dormitory.student.room.domain.model.Roommate
import com.ktx.dormitory.student.room.domain.repository.RoomRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepositoryImpl @Inject constructor(
    private val remoteDataSource: RoomRemoteDataSource
) : RoomRepository {

    override suspend fun getRoomInfo(): Result<RoomInfo> {
        return try {
            val response = remoteDataSource.getMyRoom()
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getAvailableRooms(): Result<List<RoomInfo>> {
        return try {
            val response = remoteDataSource.getAvailableRooms()
            if (response.success && response.data != null) {
                Result.success(response.data.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun submitTransferRequest(reason: String, targetRoomId: String?): Result<Unit> {
        return try {
            val response = remoteDataSource.submitTransferRequest(RoomTransferRequest(reason, targetRoomId))
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getTransferHistory(): Result<List<RoomTransferHistory>> {
        return try {
            val response = remoteDataSource.getTransferHistory()
            if (response.success && response.data != null) {
                Result.success(response.data.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getRoommates(): Result<List<Roommate>> {
        return try {
            val response = remoteDataSource.getMyRoommates()
            if (response.success && response.data != null) {
                Result.success(response.data.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}
