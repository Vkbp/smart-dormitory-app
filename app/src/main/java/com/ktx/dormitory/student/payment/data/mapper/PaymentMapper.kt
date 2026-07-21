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
        contentPrefix = contentPrefix
    )
}

fun BillDto.toDomain(): Bill {
    return Bill(
        id = id,
        type = when (type?.uppercase()) {
            "APPLICATION_FEE" -> BillType.APPLICATION_FEE
            "ACCOMMODATION_FEE" -> BillType.ACCOMMODATION_FEE
            "ELECTRIC_FEE" -> BillType.ELECTRIC_FEE
            "WATER_FEE" -> BillType.WATER_FEE
            "PENALTY_FEE" -> BillType.PENALTY_FEE
            "DEPOSIT_FEE" -> BillType.DEPOSIT_FEE
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
        roomCode = roomCode,
        bedCode = bedCode
    )
}

fun PaymentResponseDto.toDomain(): PaymentResult {
    return PaymentResult(
        paymentId = paymentId,
        billId = billId,
        transactionCode = transactionCode,
        amount = amount,
        paymentMethod = when (paymentMethod?.uppercase()) {
            "BANK_TRANSFER" -> PaymentMethod.BANK_TRANSFER
            "CASH" -> PaymentMethod.CASH
            else -> PaymentMethod.BANK_TRANSFER
        },
        paymentStatus = paymentStatus,
        paymentUrl = paymentUrl,
        paidAt = paidAt
    )
}

fun BillDto.toEntity(): InvoiceEntity {
    return InvoiceEntity(
        id = id,
        type = type,
        amount = amount,
        paidAmount = paidAmount,
        remainingAmount = remainingAmount,
        status = status,
        dueDate = dueDate,
        description = description,
        assignmentId = assignmentId,
        roomCode = roomCode,
        bedCode = bedCode
    )
}

fun InvoiceEntity.toDomain(): Bill {
    return Bill(
        id = id,
        type = when (type?.uppercase()) {
            "APPLICATION_FEE" -> BillType.APPLICATION_FEE
            "ACCOMMODATION_FEE" -> BillType.ACCOMMODATION_FEE
            "ELECTRIC_FEE" -> BillType.ELECTRIC_FEE
            "WATER_FEE" -> BillType.WATER_FEE
            "PENALTY_FEE" -> BillType.PENALTY_FEE
            "DEPOSIT_FEE" -> BillType.DEPOSIT_FEE
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
        roomCode = roomCode,
        bedCode = bedCode
    )
}
