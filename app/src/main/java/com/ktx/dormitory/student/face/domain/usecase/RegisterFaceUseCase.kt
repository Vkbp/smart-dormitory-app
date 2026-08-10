package com.ktx.dormitory.student.face.domain.usecase

import com.ktx.dormitory.student.face.domain.repository.FaceRepository
import com.ktx.dormitory.core.sync.RegisterFacePayload
import com.ktx.dormitory.data.common.local.PendingSyncDao
import com.ktx.dormitory.data.common.local.PendingSyncEntity
import com.google.gson.Gson
import java.io.File
import javax.inject.Inject

class RegisterFaceUseCase @Inject constructor(
    private val faceRepository: FaceRepository,
    private val pendingSyncDao: PendingSyncDao,
) {
    private val gson = Gson()

    suspend operator fun invoke(studentId: String, name: String, faceImageFile: File): Result<Unit> {
        val result = faceRepository.registerFace(studentId, name, faceImageFile)
        
        if (result.isFailure) {
            // Lưu vào PendingSync nếu thất bại (Offline flow)
            val payload = RegisterFacePayload(studentId, faceImageFile.absolutePath, name)
            pendingSyncDao.insertAction(
                PendingSyncEntity(
                    actionType = "REGISTER_FACE",
                    payload = gson.toJson(payload)
                )
            )
        }
        
        return result
    }
}
