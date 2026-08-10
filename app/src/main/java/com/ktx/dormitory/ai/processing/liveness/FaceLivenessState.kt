package com.ktx.dormitory.ai.processing.liveness

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class LivenessStep : Parcelable {
    EYE_BLINK,
    TURN_LEFT,
    TURN_RIGHT,
    SMILE,
    COMPLETED
}

@Parcelize
data class FaceLivenessUiState(
    val currentStep: LivenessStep = LivenessStep.EYE_BLINK,
    val progress: Float = 0f,
    val isQualityMet: Boolean = false,
    val qualityMessage: String = "Vui lòng nhìn thẳng vào camera",
    val isLivenessPassed: Boolean = false,
    val lookBackRequired: Boolean = false
) : Parcelable

@Parcelize
data class FaceQualityResult(
    val isGood: Boolean,
    val message: String = "",
    val brightness: Float = 0f,
    val isBlurry: Boolean = false,
    val faceSizeOk: Boolean = false
) : Parcelable
