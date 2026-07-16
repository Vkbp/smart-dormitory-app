package com.ktx.dormitory.student.access.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccessLog(
    val id: String,
    val studentId: String?,
    val gateId: String?,
    val buildingId: String?,
    val operatorId: String?,
    val eventTimestamp: String?,
    val decision: String?,   // GRANTED / DENIED
    val denialReason: String?,
    val method: String?,      // QR / FACE / RFID
    val createdAt: String?
) : Parcelable
