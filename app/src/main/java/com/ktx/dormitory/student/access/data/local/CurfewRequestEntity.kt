package com.ktx.dormitory.student.access.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "curfew_requests")
data class CurfewRequestEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val reason: String,
    val expectedArrivalTime: String,
    val note: String?,
    val status: String, // PENDING, APPROVED, REJECTED
    val createdAt: String?,
    val approvedAt: String?,
    val approvedBy: String?
)
