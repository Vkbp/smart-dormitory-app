package com.ktx.dormitory.shared.notification.domain.usecase

import com.ktx.dormitory.shared.notification.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * UseCase for reporting a maintenance issue via In-App Notification.
 */
class ReportIssueUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(description: String, isCommonArea: Boolean): Result<String> {
        return repository.reportIssue(description, isCommonArea)
    }
}
