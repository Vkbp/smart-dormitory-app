package com.ktx.dormitory.student.payment.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey val id: String,
    val billCode: String?,
    val type: String?,
    val amount: Double?,
    val paidAmount: Double?,
    val remainingAmount: Double?,
    val status: String?,
    val dueDate: String?,
    val description: String?,
    val assignmentId: String? = null,
    val billStatus: String? = null,
    val assignmentStatus: String? = null,
    val message: String? = null,
    val isBillOwner: Boolean = false,
    val roomCode: String? = null,
    val bedCode: String? = null
)
