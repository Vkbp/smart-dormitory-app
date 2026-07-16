package com.ktx.dormitory.student.access.data.dto.response

import com.google.gson.annotations.SerializedName

data class AccessLogDto(
    @SerializedName("id") val id: String,
    @SerializedName("studentId") val studentId: String?,
    @SerializedName("gateId") val gateId: String?,
    @SerializedName("buildingId") val buildingId: String?,
    @SerializedName("operatorId") val operatorId: String?,
    @SerializedName("eventTimestamp") val eventTimestamp: String?,
    @SerializedName("decision") val decision: String?,
    @SerializedName("denialReason") val denialReason: String?,
    @SerializedName("method") val method: String?,
    @SerializedName("createdAt") val createdAt: String?
)
