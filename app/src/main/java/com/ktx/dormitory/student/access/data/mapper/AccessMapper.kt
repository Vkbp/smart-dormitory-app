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
    gateId = gateId,
    buildingId = buildingId,
    operatorId = operatorId,
    eventTimestamp = eventTimestamp,
    decision = decision,
    denialReason = denialReason,
    method = method,
    createdAt = createdAt
)

fun AccessLogEntity.toDomain() = AccessLog(
    id = id,
    studentId = studentId,
    gateId = gateId,
    buildingId = buildingId,
    operatorId = operatorId,
    eventTimestamp = eventTimestamp,
    decision = decision,
    denialReason = denialReason,
    method = method,
    createdAt = createdAt
)

fun AccessLogDto.toEntity() = AccessLogEntity(
    id = id,
    studentId = studentId,
    gateId = gateId,
    buildingId = buildingId,
    operatorId = operatorId,
    eventTimestamp = eventTimestamp,
    decision = decision,
    denialReason = denialReason,
    method = method,
    createdAt = createdAt
)

fun CurfewRequestDto.toDomain() = CurfewRequest(
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
    id = id,
    studentId = studentId,
    requestType = requestType,
    reason = reason,
    startDate = startDate,
    expectedArrivalTime = expectedArrivalTime,
    note = note,
    status = status,
    createdAt = createdAt,
    approvedAt = approvedAt,
    approvedBy = approvedBy
)
