package com.ktx.dormitory.student.access.data.mapper

import com.ktx.dormitory.student.access.data.dto.response.CurfewRequestDto
import com.ktx.dormitory.student.access.domain.model.CurfewRequest
import com.ktx.dormitory.student.access.domain.model.CurfewRequestType
import com.ktx.dormitory.student.access.domain.model.CurfewStatus
import com.ktx.dormitory.student.access.data.local.AccessLogEntity
import com.ktx.dormitory.student.access.data.local.CurfewRequestEntity
import com.ktx.dormitory.student.access.data.dto.response.AccessLogDto
import com.ktx.dormitory.student.access.domain.model.AccessLog

fun AccessLogDto.toDomain() = AccessLog(
    id = id,
    studentId = studentId,
    studentName = studentName ?: student?.fullName,
    studentCode = studentCode ?: student?.studentCode,
    studentAvatar = student?.avatarUrl,
    gateId = gateId,
    gateName = gateName ?: gate?.name,
    buildingId = buildingId,
    buildingName = buildingName,
    operatorId = operatorId,
    operatorName = operatorName,
    eventTimestamp = eventTimestamp,
    decision = decision,
    denialReason = denialReason,
    method = method,
    direction = direction,
    snapshotUrl = snapshotUrl,
    createdAt = createdAt
)

fun AccessLogEntity.toDomain() = AccessLog(
    id = id,
    studentId = studentId,
    studentName = studentName,
    studentCode = studentCode,
    gateId = gateId,
    gateName = gateName,
    buildingId = buildingId,
    buildingName = buildingName,
    operatorId = operatorId,
    operatorName = operatorName,
    eventTimestamp = eventTimestamp,
    decision = decision,
    denialReason = denialReason,
    method = method,
    createdAt = createdAt
)

fun AccessLogDto.toEntity() = AccessLogEntity(
    id = id,
    studentId = studentId,
    studentName = studentName ?: student?.fullName,
    studentCode = studentCode ?: student?.studentCode,
    gateId = gateId,
    gateName = gateName ?: gate?.name,
    buildingId = buildingId,
    buildingName = buildingName,
    operatorId = operatorId,
    operatorName = operatorName,
    eventTimestamp = eventTimestamp,
    decision = decision,
    denialReason = denialReason,
    method = method,
    createdAt = createdAt
)

fun CurfewRequestDto.toDomain() = CurfewRequest(
    id = id ?: java.util.UUID.randomUUID().toString(),
    studentId = studentId ?: "",
    requestType = try { CurfewRequestType.valueOf(requestType ?: "LATE_RETURN") } catch (e: Exception) { CurfewRequestType.LATE_RETURN },
    reason = reason ?: "N/A",
    startDate = startDate,
    expectedArrivalTime = expectedArrivalTime ?: "",
    note = note,
    status = try { CurfewStatus.valueOf(status ?: "PENDING") } catch (e: Exception) { CurfewStatus.PENDING },
    createdAt = createdAt,
    approvedAt = approvedAt,
    approvedBy = approvedBy
)

fun CurfewRequestEntity.toDomain() = CurfewRequest(
    id = id,
    studentId = studentId,
    requestType = try { CurfewRequestType.valueOf(requestType) } catch (e: Exception) { CurfewRequestType.LATE_RETURN },
    reason = reason,
    startDate = startDate,
    expectedArrivalTime = expectedArrivalTime,
    note = note,
    status = try { CurfewStatus.valueOf(status) } catch (e: Exception) { CurfewStatus.PENDING },
    createdAt = createdAt,
    approvedAt = approvedAt,
    approvedBy = approvedBy
)

fun CurfewRequestDto.toEntity() = CurfewRequestEntity(
    id = id ?: java.util.UUID.randomUUID().toString(),
    studentId = studentId ?: "",
    requestType = requestType ?: "LATE_RETURN",
    reason = reason ?: "N/A",
    startDate = startDate,
    expectedArrivalTime = expectedArrivalTime ?: "",
    note = note,
    status = status ?: "PENDING",
    createdAt = createdAt,
    approvedAt = approvedAt,
    approvedBy = approvedBy
)
