package com.ktx.dormitory.student.room.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UtilityReading(
    val id: String?,
    val oldReading: Double?,
    val newReading: Double?,
    val readingDate: String?,
    val type: String?
) : Parcelable
