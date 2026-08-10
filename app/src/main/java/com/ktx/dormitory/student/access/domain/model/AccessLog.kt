package com.ktx.dormitory.student.access.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccessLog(
    val id: String,
    val studentId: String?,
    val studentName: String? = null,
    val studentCode: String? = null,
    val studentAvatar: String? = null,
    val gateId: String?,
    val gateName: String? = null,
    val buildingId: String?,
    val buildingName: String? = null,
    val operatorId: String?,
    val operatorName: String? = null,
    val eventTimestamp: String?,
    val decision: String?,   // GRANTED / DENIED
    val denialReason: String?,
    val method: String?,      // QR / FACE / RFID / REMOTE_UNLOCK / MANUAL_OVERRIDE
    val direction: String? = null,
    val snapshotUrl: String? = null,
    val createdAt: String?
) : Parcelable
