package com.ktx.dormitory.shared.notification.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    val id: Long,
    val title: String,
    val message: String,
    val actionUrl: String?,
    val type: String?,
    val isRead: Boolean,
    val createdAt: String
) : Parcelable

@Parcelize
data class IssueReport(
    val id: Long,
    val description: String,
    val status: String,
    val createdAt: String,
    val imageUrl: String? = null
) : Parcelable
