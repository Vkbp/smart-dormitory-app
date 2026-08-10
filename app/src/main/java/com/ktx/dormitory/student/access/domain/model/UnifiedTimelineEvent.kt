package com.ktx.dormitory.student.access.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnifiedTimelineEvent(
    val id: String,
    val timestamp: String? = "",
    val type: UnifiedEventType,
    val gateId: String?,
    val gateName: String? = null,
    val buildingId: String?,
    val buildingName: String? = null,
    val operatorName: String? = null,
    val method: String?,
    val confidenceScore: Double? = null,
    val denialReason: String? = null,
    val verificationStatus: String? = null,
    val accessDecision: String? = null
) : Parcelable

enum class UnifiedEventType {
    SUCCESS,        // Verification = SUCCESS && Access = GRANTED
    ACCESS_DENIED,  // Verification = SUCCESS && Access = DENIED
    VERIFY_FAIL,    // Verification = FAIL
    UNKNOWN
}
