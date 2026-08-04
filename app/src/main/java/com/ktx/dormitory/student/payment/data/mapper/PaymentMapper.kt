package com.ktx.dormitory.student.payment.data.mapper

import com.ktx.dormitory.student.payment.data.dto.response.*
import com.ktx.dormitory.student.payment.data.local.InvoiceEntity
import com.ktx.dormitory.student.payment.domain.model.*

fun PaymentInstructionDto.toDomain(): PaymentInstruction {
    return PaymentInstruction(
        bankName = bankName,
        accountNumber = accountNumber,
        accountHolder = accountHolder,
        qrCodeUrl = qrCodeUrl,
        contentPrefix = content,
        amount = amount
    )
}

fun BillDto.toDomain(): Bill {
    return Bill(
        id = id,
        billCode = billCode,
        type = when (type?.uppercase()) {
            "ACCOMMODATION_FEE" -> BillType.ACCOMMODATION_FEE
            "ELECTRIC_FEE" -> BillType.ELECTRIC_FEE
            "PENALTY_FEE" -> BillType.PENALTY_FEE
            else -> null
        },
        amount = amount,
        paidAmount = paidAmount,
        remainingAmount = remainingAmount,
        status = when (status?.uppercase()) {
            "UNPAID" -> BillStatus.UNPAID
            "PARTIALLY_PAID" -> BillStatus.PARTIALLY_PAID
            "PAID" -> BillStatus.PAID
            "OVERDUE" -> BillStatus.OVERDUE
            "CANCELLED" -> BillStatus.CANCELLED
            else -> BillStatus.UNPAID
        },
        dueDate = dueDate,
        description = description,
        assignmentId = assignmentId,
        billStatus = billStatus,
        assignmentStatus = assignmentStatus,
        message = message,
        isBillOwner = isBillOwner ?: false,
        roomCode = roomCode,
        bedCode = bedCode,
        requiresRefund = requiresRefund ?: false,
        isSplittable = isSplittable ?: false,
        reportedStudentIds = reportedStudentIds ?: emptyList()
    )
}

fun PaymentResponseDto.toDomain(): PaymentResult {
    return PaymentResult(
        paymentId = paymentId,
        billId = billId,
        billCode = billCode,
        transactionCode = transactionCode,
        amount = amount,
        paymentMethod = when (paymentMethod?.uppercase()) {
            "BANK_TRANSFER" -> PaymentMethod.BANK_TRANSFER
            "CASH" -> PaymentMethod.CASH
            else -> PaymentMethod.BANK_TRANSFER
        },
        paymentStatus = paymentStatus,
        paymentUrl = paymentUrl,
        paidAt = paidAt,
        billStatus = billStatus,
        assignmentStatus = assignmentStatus,
        paidAmount = paidAmount,
        message = message
    )
}

fun BillDto.toEntity(): InvoiceEntity {
    return InvoiceEntity(
        id = id,
        billCode = billCode,
        type = type,
        amount = amount?.toDouble(),
        paidAmount = paidAmount?.toDouble(),
        remainingAmount = remainingAmount?.toDouble(),
        status = status,
        dueDate = dueDate,
        description = description,
        assignmentId = assignmentId,
        billStatus = billStatus,
        assignmentStatus = assignmentStatus,
        message = message,
        isBillOwner = isBillOwner ?: false,
        roomCode = roomCode,
        bedCode = bedCode,
        requiresRefund = requiresRefund ?: false,
        isSplittable = isSplittable ?: false
    )
}

fun InvoiceEntity.toDomain(): Bill {
    return Bill(
        id = id,
        billCode = billCode,
        type = when (type?.uppercase()) {
            "ACCOMMODATION_FEE" -> BillType.ACCOMMODATION_FEE
            "ELECTRIC_FEE" -> BillType.ELECTRIC_FEE
            "PENALTY_FEE" -> BillType.PENALTY_FEE
            else -> null
        },
        amount = amount?.toBigDecimal(),
        paidAmount = paidAmount?.toBigDecimal(),
        remainingAmount = remainingAmount?.toBigDecimal(),
        status = when (status?.uppercase()) {
            "UNPAID" -> BillStatus.UNPAID
            "PARTIALLY_PAID" -> BillStatus.PARTIALLY_PAID
            "PAID" -> BillStatus.PAID
            "OVERDUE" -> BillStatus.OVERDUE
            "CANCELLED" -> BillStatus.CANCELLED
            else -> BillStatus.UNPAID
        },
        dueDate = dueDate,
        description = description,
        assignmentId = assignmentId,
        billStatus = billStatus,
        assignmentStatus = assignmentStatus,
        message = message,
        isBillOwner = isBillOwner,
        roomCode = roomCode,
        bedCode = bedCode,
        requiresRefund = requiresRefund,
        isSplittable = isSplittable
    )
}
