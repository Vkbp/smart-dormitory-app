package com.ktx.dormitory.student.access.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "access_logs")
data class AccessLogEntity(
    @PrimaryKey val id: String,
    val studentId: String?,
    val studentName: String?,
    val studentCode: String?,
    val gateId: String?,
    val gateName: String?,
    val buildingId: String?,
    val buildingName: String?,
    val operatorId: String?,
    val operatorName: String?,
    val eventTimestamp: String?,
    val decision: String?,
    val denialReason: String?,
    val method: String?,
    val createdAt: String?
)
