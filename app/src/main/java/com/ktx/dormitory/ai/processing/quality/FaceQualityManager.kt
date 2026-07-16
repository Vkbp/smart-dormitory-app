package com.ktx.dormitory.ai.processing.quality

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import com.google.mlkit.vision.face.Face
import com.ktx.dormitory.ai.processing.liveness.FaceQualityResult
import kotlin.math.abs

object FaceQualityManager {

    private const val MIN_BRIGHTNESS = 40f
    private const val MAX_BRIGHTNESS = 230f
    private const val MIN_FACE_SIZE_PERCENT = 0.25f
    private const val MAX_HEAD_ANGLE = 15f

    fun checkQuality(
        face: Face,
        bitmap: Bitmap,
        isBlinkingStep: Boolean = false,
        isTurningStep: Boolean = false
    ): FaceQualityResult {
        val faceArea = face.boundingBox.width() * face.boundingBox.height()
        val imageArea = bitmap.width * bitmap.height
        val faceSizeRatio = faceArea.toFloat() / imageArea

        if (faceSizeRatio < MIN_FACE_SIZE_PERCENT) {
            return FaceQualityResult(false, "Vui lòng đưa mặt lại gần hơn", faceSizeOk = false)
        }

        if (!isTurningStep && (abs(face.headEulerAngleY) > MAX_HEAD_ANGLE || abs(face.headEulerAngleZ) > MAX_HEAD_ANGLE)) {
            return FaceQualityResult(false, "Vui lòng nhìn thẳng vào camera")
        }

        val brightness = calculateBrightness(bitmap, face.boundingBox)
        if (brightness < MIN_BRIGHTNESS) {
            return FaceQualityResult(false, "Ánh sáng quá tối", brightness = brightness)
        }
        if (brightness > MAX_BRIGHTNESS) {
            return FaceQualityResult(false, "Ánh sáng quá chói", brightness = brightness)
        }

        val leftEyeOpen = face.leftEyeOpenProbability ?: 1.0f
        val rightEyeOpen = face.rightEyeOpenProbability ?: 1.0f
        if (!isBlinkingStep && !isTurningStep && (leftEyeOpen < 0.4f || rightEyeOpen < 0.4f)) {
            return FaceQualityResult(false, "Vui lòng mở mắt")
        }

        return FaceQualityResult(
            true,
            "Chất lượng đạt chuẩn",
            brightness = brightness,
            faceSizeOk = true
        )
    }

    private fun calculateBrightness(bitmap: Bitmap, rect: Rect): Float {
        var totalLuminance = 0L
        var count = 0
        val left = rect.left.coerceAtLeast(0)
        val top = rect.top.coerceAtLeast(0)
        val right = rect.right.coerceAtMost(bitmap.width - 1)
        val bottom = rect.bottom.coerceAtMost(bitmap.height - 1)

        for (y in top until bottom step 10) {
            for (x in left until right step 10) {
                val pixel = bitmap.getPixel(x, y)
                totalLuminance += (0.299 * Color.red(pixel) + 0.587 * Color.green(pixel) + 0.114 * Color.blue(pixel)).toLong()
                count++
            }
        }
        return if (count > 0) totalLuminance.toFloat() / count else 0f
    }
}
