package com.ktx.dormitory.student.checkout.data.mapper

import com.ktx.dormitory.student.checkout.data.dto.request.CreateCheckoutRequestDto
import com.ktx.dormitory.student.checkout.data.dto.response.CheckoutResponseDto
import com.ktx.dormitory.student.checkout.domain.model.CheckoutRequest
import com.ktx.dormitory.student.checkout.domain.model.CheckoutResponse
import com.ktx.dormitory.student.checkout.domain.model.CheckoutStatus

fun CheckoutResponseDto.toDomain(): CheckoutResponse {
    return CheckoutResponse(
        requestId = requestId,
        studentId = studentId,
        studentCode = studentCode,
        fullName = fullName,
        assignmentId = assignmentId,
        roomCode = roomCode,
        bedCode = bedCode,
        intendedCheckoutDate = intendedCheckoutDate,
        reason = reason,
        bankAccountNumber = bankAccountNumber,
        bankName = bankName,
        status = when (status.uppercase()) {
            "PENDING" -> CheckoutStatus.PENDING
            "APPROVED" -> CheckoutStatus.APPROVED
            "COMPLETED" -> CheckoutStatus.COMPLETED
            "REJECTED" -> CheckoutStatus.REJECTED
            else -> CheckoutStatus.UNKNOWN
        },
        checkoutReason = checkoutReason,
        estimatedRefundAmount = estimatedRefundAmount,
        rejectReason = rejectReason,
        createdAt = createdAt
    )
}

fun CheckoutRequest.toDto(): CreateCheckoutRequestDto {
    return CreateCheckoutRequestDto(
        intendedCheckoutDate = intendedCheckoutDate,
        reason = reason,
        bankAccountNumber = bankAccountNumber ?: "",
        bankName = bankName ?: ""
    )
}
