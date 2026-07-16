package com.ktx.dormitory.student.access.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ktx.dormitory.student.access.data.dto.response.AccessLogDto
import com.ktx.dormitory.student.access.data.remote.AccessApiService
import com.ktx.dormitory.student.face.data.dto.response.VerificationAttemptDto
import com.ktx.dormitory.student.face.data.remote.FaceApiService
import com.ktx.dormitory.student.access.domain.model.UnifiedTimelineEvent
import com.ktx.dormitory.student.access.domain.model.UnifiedEventType
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class AccessHistoryPagingSource(
    private val accessApi: AccessApiService,
    private val faceApi: FaceApiService
) : PagingSource<Int, UnifiedTimelineEvent>() {

    override fun getRefreshKey(state: PagingState<Int, UnifiedTimelineEvent>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnifiedTimelineEvent> {
        val page = params.key ?: 0
        val size = params.loadSize

        return try {
            val accessResponse = accessApi.getAccessHistory(page = page, size = size)
            val faceResponse = faceApi.getMyVerifications(page = page, size = size)

            if (accessResponse.success && faceResponse.success) {
                val accessPage = accessResponse.data
                val facePage = faceResponse.data

                if (accessPage != null && facePage != null) {
                    val merged = mergeEvents(
                        facePage.content ?: emptyList(),
                        accessPage.content ?: emptyList()
                    )

                    LoadResult.Page(
                        data = merged,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (accessPage.last) null else page + 1
                    )
                } else {
                    LoadResult.Error(Exception("Dữ liệu trả về trống"))
                }
            } else {
                LoadResult.Error(Exception(accessResponse.message ?: faceResponse.message))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun mergeEvents(
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
                        buildingId = matchingAccess.buildingId,
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
                    buildingId = a.buildingId,
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
}
