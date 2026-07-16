package com.ktx.dormitory.student.payment.data.mapper

import com.ktx.dormitory.student.payment.data.local.InvoiceEntity
import com.ktx.dormitory.student.payment.data.dto.response.InvoiceDto
import com.ktx.dormitory.student.payment.data.dto.response.PaymentInstructionDto
import com.ktx.dormitory.student.payment.data.dto.response.TransactionDto
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

fun InvoiceDto.toDomain(): Invoice {
    return Invoice(
        id = id,
        type = when (type?.uppercase()) {
            "ACCOMMODATION_FEE", "ROOM" -> InvoiceType.ROOM
            "ELECTRIC_FEE", "ELECTRICITY" -> InvoiceType.ELECTRICITY
            "WATER_FEE", "WATER" -> InvoiceType.WATER
            "SERVICE_FEE", "SERVICE" -> InvoiceType.SERVICE
            "APPLICATION_FEE" -> InvoiceType.APPLICATION
            "PENALTY_FEE" -> InvoiceType.PENALTY
            "DEPOSIT_FEE" -> InvoiceType.DEPOSIT
            else -> null
        },
        amount = amount,
        paidAmount = paidAmount,
        remainingAmount = remainingAmount,
        status = when (status?.uppercase()) {
            "UNPAID", "PENDING", "NEW" -> PaymentStatus.UNPAID
            "PARTIALLY_PAID" -> PaymentStatus.PARTIALLY_PAID
            "PAID" -> PaymentStatus.PAID
            "OVERDUE" -> PaymentStatus.OVERDUE
            "CANCELLED" -> PaymentStatus.CANCELLED
            else -> PaymentStatus.UNPAID // Mặc định là chưa thanh toán nếu có status lạ
        },
        dueDate = dueDate,
        description = description,
        roomCode = roomCode,
        bedCode = bedCode
    )
}

fun InvoiceEntity.toDomain(): Invoice {
    return Invoice(
        id = id,
        type = when (type?.uppercase()) {
            "ACCOMMODATION_FEE", "ROOM" -> InvoiceType.ROOM
            "ELECTRICITY" -> InvoiceType.ELECTRICITY
            "WATER" -> InvoiceType.WATER
            "SERVICE_FEE" -> InvoiceType.SERVICE
            else -> null
        },
        amount = amount,
        paidAmount = paidAmount,
        remainingAmount = remainingAmount,
        status = when (status?.uppercase()) {
            "UNPAID" -> PaymentStatus.UNPAID
            "PARTIALLY_PAID" -> PaymentStatus.PARTIALLY_PAID
            "PAID" -> PaymentStatus.PAID
            "OVERDUE" -> PaymentStatus.OVERDUE
            else -> null
        },
        dueDate = dueDate,
        description = description,
        roomCode = roomCode,
        bedCode = bedCode
    )
}

fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        transactionId = transactionId,
        amount = amount,
        method = when (type?.uppercase()) {
            "ACCOMMODATION_FEE", "ROOM" -> "Phí phòng"
            "ELECTRIC_FEE", "ELECTRICITY" -> "Tiền điện"
            "WATER_FEE", "WATER" -> "Tiền nước"
            "SERVICE_FEE", "SERVICE" -> "Phí dịch vụ"
            "APPLICATION_FEE" -> "Lệ phí đơn"
            "PENALTY_FEE" -> "Tiền phạt"
            "DEPOSIT_FEE" -> "Tiền cọc"
            else -> type ?: "Hóa đơn"
        },
        status = status,
        createdAt = createdAt,
        type = type,
        message = message,
        transactionCode = transactionCode
    )
}

fun InvoiceDto.toEntity(): InvoiceEntity {
    return InvoiceEntity(
        id = id,
        type = type,
        amount = amount,
        paidAmount = paidAmount,
        remainingAmount = remainingAmount,
        status = status,
        dueDate = dueDate,
        description = description,
        roomCode = roomCode,
        bedCode = bedCode
    )
}
