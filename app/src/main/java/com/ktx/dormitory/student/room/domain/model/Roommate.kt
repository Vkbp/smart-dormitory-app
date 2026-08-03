package com.ktx.dormitory.student.room.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Roommate(
    val id: String,
    val studentCode: String,
    val fullName: String,
    val avatarUrl: String? = null
) : Parcelable
