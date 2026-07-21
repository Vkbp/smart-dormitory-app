package com.ktx.dormitory.student.payment.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey val id: String,
    val type: String?,
    val amount: Double?,
    val paidAmount: Double?,
    val remainingAmount: Double?,
    val status: String?,
    val dueDate: String?,
    val description: String?,
    val assignmentId: String? = null,
    val roomCode: String? = null,
    val bedCode: String? = null
)
