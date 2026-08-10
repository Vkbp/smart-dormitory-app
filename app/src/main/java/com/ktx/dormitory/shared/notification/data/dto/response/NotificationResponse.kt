package com.ktx.dormitory.shared.notification.data.dto.response

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("eventId") val eventId: String? = null,
    @SerializedName("actionUrl") val actionUrl: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("isRead") val isRead: Boolean,
    @SerializedName("createdAt") val createdAt: String
)
