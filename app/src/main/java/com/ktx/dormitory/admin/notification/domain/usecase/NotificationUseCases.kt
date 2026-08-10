package com.ktx.dormitory.admin.notification.domain.usecase

import com.ktx.dormitory.admin.smartaccess.domain.repository.AdminRepository
import javax.inject.Inject

class BroadcastNotificationUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(title: String, message: String, targetAudience: String) = 
        repository.broadcastNotification(title, message, targetAudience)
}
