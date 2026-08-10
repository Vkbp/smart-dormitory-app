package com.ktx.dormitory.ai.core

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.ktx.dormitory.core.util.toBitmapRotation

/**
 * Interface to decouple the AI Core from the Presentation Layer.
 * Essential for Clean Architecture and unit testing.
 */
interface FaceAnalysisListener {
    /**
     * Called when a frame has been analyzed and a face is detected.
     */
    fun onFrameAnalyzed(face: Face, bitmap: Bitmap)

    /**
     * Determines if the analyzer should perform expensive Bitmap conversion.
     * Optimization: PERF-01 - Avoid creating Bitmap for every frame.
     */
    fun shouldCreateBitmap(): Boolean
}

class FaceAnalyzer(
    private val listener: FaceAnalysisListener,
    private val onFaceDetected: (List<Face>, Int, Int, Bitmap?) -> Unit,
) : ImageAnalysis.Analyzer {

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            
            // ML Kit analysis is performed directly on the mediaImage (efficient)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    val face = faces.firstOrNull()
                    if (face != null) {
                        // Tối ưu hóa PERF-01: Chỉ tạo Bitmap khi Presentation layer thực sự cần (e.g., Liveness Check)
                        if (listener.shouldCreateBitmap()) {
                            val fullBitmap = imageProxy.toBitmapRotation()
                            if (fullBitmap != null) {
                                listener.onFrameAnalyzed(face, fullBitmap)
                                onFaceDetected(faces, image.width, image.height, fullBitmap)
                            } else {
                                onFaceDetected(faces, image.width, image.height, null)
                            }
                        } else {
                            // Không tạo Bitmap nếu đã xong liveness hoặc không cần thiết
                            onFaceDetected(faces, image.width, image.height, null)
                        }
                    } else {
                        onFaceDetected(faces, image.width, image.height, null)
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
