package com.ktx.dormitory.ai.processing.liveness

import com.google.mlkit.vision.face.Face
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs

class FaceLivenessProcessor(initialState: FaceLivenessUiState = FaceLivenessUiState()) {
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private var blinkDetected = false
    private var leftTurnDetected = false
    private var rightTurnDetected = false

    fun process(face: Face) {
        if (_state.value.currentStep == LivenessStep.COMPLETED) return

        when (_state.value.currentStep) {
            LivenessStep.EYE_BLINK -> {
                val leftEye = face.leftEyeOpenProbability ?: 1.0f
                val rightEye = face.rightEyeOpenProbability ?: 1.0f
                if (leftEye < 0.2f && rightEye < 0.2f) {
                    blinkDetected = true
                } else if (blinkDetected && leftEye > 0.6f && rightEye > 0.6f) {
                    moveToNextStep(LivenessStep.TURN_LEFT)
                }
            }
            LivenessStep.TURN_LEFT -> {
                if (face.headEulerAngleY > 25f) {
                    if (!leftTurnDetected) {
                        leftTurnDetected = true
                        _state.update { it.copy(lookBackRequired = true) }
                    }
                } else if (leftTurnDetected && abs(face.headEulerAngleY) < 10f) {
                    moveToNextStep(LivenessStep.TURN_RIGHT)
                }
            }
            LivenessStep.TURN_RIGHT -> {
                if (face.headEulerAngleY < -25f) {
                    if (!rightTurnDetected) {
                        rightTurnDetected = true
                        _state.update { it.copy(lookBackRequired = true) }
                    }
                } else if (rightTurnDetected && abs(face.headEulerAngleY) < 10f) {
                    moveToNextStep(LivenessStep.SMILE)
                }
            }
            LivenessStep.SMILE -> {
                val smileProb = face.smilingProbability ?: 0f
                if (smileProb > 0.7f) {
                    moveToNextStep(LivenessStep.COMPLETED)
                }
            }
            else -> {}
        }
    }

    private fun moveToNextStep(next: LivenessStep) {
        _state.update { it.copy(currentStep = next, progress = calculateProgress(next), lookBackRequired = false) }
    }

    private fun calculateProgress(step: LivenessStep): Float {
        return when (step) {
            LivenessStep.EYE_BLINK -> 0.25f
            LivenessStep.TURN_LEFT -> 0.5f
            LivenessStep.TURN_RIGHT -> 0.75f
            LivenessStep.SMILE -> 0.9f
            LivenessStep.COMPLETED -> 1.0f
        }
    }

    fun reset() {
        _state.value = FaceLivenessUiState()
        blinkDetected = false
        leftTurnDetected = false
        rightTurnDetected = false
    }
}
