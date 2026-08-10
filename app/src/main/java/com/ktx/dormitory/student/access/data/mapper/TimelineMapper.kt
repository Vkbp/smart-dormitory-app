package com.ktx.dormitory.student.access.data.mapper

import com.ktx.dormitory.student.access.data.dto.response.AccessLogDto
import com.ktx.dormitory.student.face.data.dto.response.VerificationAttemptDto
import com.ktx.dormitory.student.access.domain.model.UnifiedTimelineEvent
import com.ktx.dormitory.student.access.domain.model.UnifiedEventType
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * Thuật toán hợp nhất (Unified Timeline Algorithm)
 * Gom nhóm các sự kiện Verification và Access lệch nhau < 10 giây.
 */
fun mergeTimelineEvents(
    verifications: List<VerificationAttemptDto>,
    accessLogs: List<AccessLogDto>
): List<UnifiedTimelineEvent> {
    val unifiedEvents = mutableListOf<UnifiedTimelineEvent>()
    val usedAccessIds = mutableSetOf<String>()

    for (v in verifications) {
        val vTime = parseTimestamp(v.attemptedAt)
        val matchingAccess = accessLogs.find { a ->
            val aTime = parseTimestamp(a.eventTimestamp ?: "")
            abs(vTime - aTime) < 10000 && a.id !in usedAccessIds
        }

        if (matchingAccess != null) {
            usedAccessIds.add(matchingAccess.id)
            val type = when {
                v.status == "SUCCESS" && matchingAccess.decision == "GRANTED" -> UnifiedEventType.SUCCESS
                v.status == "SUCCESS" && matchingAccess.decision == "DENIED" -> UnifiedEventType.ACCESS_DENIED
                else -> UnifiedEventType.UNKNOWN
            }
            unifiedEvents.add(
                UnifiedTimelineEvent(
                    id = matchingAccess.id,
                    timestamp = matchingAccess.eventTimestamp ?: v.attemptedAt,
                    type = type,
                    gateId = matchingAccess.gateId,
                    gateName = matchingAccess.gateName ?: matchingAccess.gate?.name,
                    buildingId = matchingAccess.buildingId,
                    buildingName = matchingAccess.buildingName,
                    operatorName = matchingAccess.operatorName,
                    method = matchingAccess.method,
                    confidenceScore = v.confidenceScore,
                    denialReason = mapDenialReason(matchingAccess.denialReason),
                    verificationStatus = v.status,
                    accessDecision = matchingAccess.decision
                )
            )
        } else {
            unifiedEvents.add(
                UnifiedTimelineEvent(
                    id = v.attemptId.toString(),
                    timestamp = v.attemptedAt,
                    type = if (v.status == "SUCCESS") UnifiedEventType.SUCCESS else UnifiedEventType.VERIFY_FAIL,
                    gateId = v.gateDeviceId,
                    gateName = v.gateName,
                    buildingId = null,
                    method = "FACE_AI",
                    confidenceScore = v.confidenceScore,
                    verificationStatus = v.status,
                    denialReason = if (v.status == "SUCCESS") "Đang xử lý mở cửa..." else null
                )
            )
        }
    }

    accessLogs.filter { it.id !in usedAccessIds }.forEach { a ->
        unifiedEvents.add(
            UnifiedTimelineEvent(
                id = a.id,
                timestamp = a.eventTimestamp ?: "",
                type = if (a.decision == "GRANTED") UnifiedEventType.SUCCESS else UnifiedEventType.ACCESS_DENIED,
                gateId = a.gateId,
                gateName = a.gateName ?: a.gate?.name,
                buildingId = a.buildingId,
                buildingName = a.buildingName,
                operatorName = a.operatorName,
                method = a.method,
                denialReason = mapDenialReason(a.denialReason),
                accessDecision = a.decision
            )
        )
    }

    return unifiedEvents.sortedByDescending { it.timestamp ?: "" }
}

private fun parseTimestamp(ts: String): Long {
    if (ts.isBlank()) return 0L
    return try {
        val cleanTs = if (ts.contains(".")) ts.substringBefore(".") else if (ts.endsWith("Z")) ts.substringBefore("Z") else ts
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        format.parse(cleanTs)?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}

private fun mapDenialReason(reason: String?): String? {
    return when (reason) {
        "CURFEW_VIOLATION" -> "Vi phạm giờ giới nghiêm (Quá 23:00)"
        "OUTSIDE_TIME_WINDOW" -> "Chưa đến khung giờ được phép ra vào"
        "UNAUTHORIZED_OR_INACTIVE" -> "Tài khoản bị đình chỉ hoặc chưa kích hoạt"
        else -> reason
    }
}
