package com.ktx.dormitory.student.access.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "access_logs")
data class AccessLogEntity(
    @PrimaryKey val id: String,
    val studentId: String?,
    val gateId: String?,
    val buildingId: String?,
    val operatorId: String?,
    val eventTimestamp: String?,
    val decision: String?,
    val denialReason: String?,
    val method: String?,
    val createdAt: String?
)
