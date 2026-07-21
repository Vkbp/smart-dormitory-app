package com.ktx.dormitory.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.ktx.dormitory.data.common.local.*
import com.ktx.dormitory.shared.profile.data.dto.request.UpdateProfileRequest
import com.ktx.dormitory.shared.profile.domain.repository.*
import com.ktx.dormitory.student.face.domain.repository.FaceRepository
import java.io.File
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val pendingSyncDao: PendingSyncDao,
    private val profileRepository: ProfileRepository,
    private val faceRepository: FaceRepository,
) : CoroutineWorker(appContext, workerParams) {

    private val gson = Gson()

    companion object {
        private val syncMutex = Mutex()
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        syncMutex.withLock {
            val pendingActions = pendingSyncDao.getNotCompletedActions()
                .filter { it.syncStatus == SyncStatus.PENDING }
            
            if (pendingActions.isEmpty()) return@withLock Result.success()

            var hasFailure = false

            for (action in pendingActions) {
                try {
                    pendingSyncDao.updateAction(action.copy(syncStatus = SyncStatus.SYNCING))
                    
                    val success = when (action.actionType) {
                        "UPDATE_PROFILE" -> {
                            val payload = gson.fromJson(action.payload, UpdateProfileRequest::class.java)
                            profileRepository.updateProfile(payload).isSuccess
                        }
                        "REGISTER_FACE" -> {
                            val payload = gson.fromJson(action.payload, RegisterFacePayload::class.java)
                            val imageFile = File(payload.imagePath)
                            if (imageFile.exists()) {
                                faceRepository.registerFace(payload.studentId, payload.name, imageFile).isSuccess
                            } else {
                                // Nếu file không tồn tại, không thể sync, đánh dấu fail để không retry vô tận
                                true 
                            }
                        }
                        else -> true
                    }

                    if (success) {
                        pendingSyncDao.deleteAction(action)
                    } else {
                        val nextRetry = action.retryCount + 1
                        if (nextRetry >= 5) {
                            pendingSyncDao.updateAction(action.copy(syncStatus = SyncStatus.FAILED, retryCount = nextRetry))
                        } else {
                            pendingSyncDao.updateAction(action.copy(syncStatus = SyncStatus.PENDING, retryCount = nextRetry))
                            hasFailure = true
                        }
                    }
                } catch (_: Exception) {
                    hasFailure = true
                    val nextRetry = action.retryCount + 1
                    if (nextRetry >= 5) {
                        pendingSyncDao.updateAction(action.copy(syncStatus = SyncStatus.FAILED, retryCount = nextRetry))
                    } else {
                        pendingSyncDao.updateAction(action.copy(syncStatus = SyncStatus.PENDING, retryCount = nextRetry))
                    }
                }
            }

            if (hasFailure) Result.retry() else Result.success()
        }
    }
}

