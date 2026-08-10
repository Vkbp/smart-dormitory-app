package com.ktx.dormitory.student.access.data.dto.response

import com.google.gson.annotations.SerializedName

data class AccessLogDto(
    @SerializedName("id") val id: String,
    @SerializedName("studentId") val studentId: String?,
    @SerializedName("studentName") val studentName: String?,
    @SerializedName("studentCode") val studentCode: String?,
    @SerializedName("student") val student: AccessStudentDto?,
    @SerializedName("gateId") val gateId: String?,
    @SerializedName("gateName") val gateName: String?,
    @SerializedName("gate") val gate: AccessGateDto?,
    @SerializedName("buildingId") val buildingId: String?,
    @SerializedName("buildingName") val buildingName: String?,
    @SerializedName("operatorId") val operatorId: String?,
    @SerializedName("operatorName") val operatorName: String?,
    @SerializedName("eventTimestamp") val eventTimestamp: String?,
    @SerializedName("decision") val decision: String?,
    @SerializedName("denialReason") val denialReason: String?,
    @SerializedName("method") val method: String?,
    @SerializedName("direction") val direction: String?,
    @SerializedName("snapshotUrl") val snapshotUrl: String?,
    @SerializedName("createdAt") val createdAt: String?
)

data class AccessStudentDto(
    @SerializedName("studentId") val studentId: String?,
    @SerializedName("studentCode") val studentCode: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("faculty") val faculty: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?
)

data class AccessGateDto(
    @SerializedName("gateId") val gateId: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("gateType") val gateType: String?
)
