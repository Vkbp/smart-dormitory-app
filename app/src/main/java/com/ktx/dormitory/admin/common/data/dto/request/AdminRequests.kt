package com.ktx.dormitory.admin.common.data.dto.request

import java.util.UUID

data class FaceRejectionRequest(val reason: String)
data class FaceRevocationRequest(val reason: String)

data class CheckoutRequestReviewDto(
    val status: String,
    val rejectReason: String? = null
)

data class StayExtensionReviewRequest(
    val status: String,
    val rejectReason: String? = null
)

data class BroadcastRequest(
    val title: String,
    val message: String,
    val targetAudience: String // STUDENT, ALL, etc.
)
