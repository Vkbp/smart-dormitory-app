package com.ktx.dormitory.student.extension.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckEligibilityResult(
    val eligible: Boolean,
    val periodName: String?,
    val fullName: String?,
    val message: String?
) : Parcelable
