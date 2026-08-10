package com.ktx.dormitory.student.access.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CurfewRequest(
    val id: String,
    val studentId: String,
    val requestType: CurfewRequestType,
    val reason: String,
    val startDate: String?,
    val expectedArrivalTime: String,
    val note: String?,
    val status: CurfewStatus,
    val createdAt: String?,
    val approvedAt: String? = null,
    val approvedBy: String? = null
) : Parcelable

enum class CurfewStatus {
    PENDING,
    APPROVED,
    REJECTED
}

enum class CurfewRequestType {
    LATE_RETURN,
    ABSENCE
}
