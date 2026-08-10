package com.ktx.dormitory.student.face.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FaceVerificationResult(
    val isMatched: Boolean,
    val studentName: String = "",
    val confidence: Int = 0
) : Parcelable
