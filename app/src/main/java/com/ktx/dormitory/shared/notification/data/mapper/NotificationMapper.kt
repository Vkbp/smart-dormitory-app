package com.ktx.dormitory.shared.notification.data.mapper

import com.ktx.dormitory.shared.notification.data.dto.response.NotificationResponse
import com.ktx.dormitory.shared.notification.domain.model.Notification

fun NotificationResponse.toDomain(): Notification {
    return Notification(
        id = id,
        title = title,
        message = message,
        actionUrl = actionUrl,
        type = type,
        isRead = isRead,
        createdAt = createdAt
    )
}

fun com.ktx.dormitory.shared.notification.data.remote.IssueReportResponse.toDomain(): com.ktx.dormitory.shared.notification.domain.model.IssueReport {
    return com.ktx.dormitory.shared.notification.domain.model.IssueReport(
        id = id,
        description = description,
        status = status,
        createdAt = createdAt,
        imageUrl = imageUrl
    )
}
