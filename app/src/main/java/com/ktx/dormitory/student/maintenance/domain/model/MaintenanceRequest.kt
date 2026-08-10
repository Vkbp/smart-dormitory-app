package com.ktx.dormitory.student.maintenance.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MaintenanceRequest(
    val id: String?,
    val roomId: String?,
    val roomCode: String?,
    val description: String?,
    val imageUrl: String?,
    val status: MaintenanceStatus,
    val createdAt: String?
) : Parcelable

enum class MaintenanceStatus {
    PENDING,
    IN_PROGRESS,
    DONE,
    REJECTED,
    UNKNOWN
}
