package com.ktx.dormitory.core.util

import android.content.Context
import android.graphics.*
import android.net.Uri
import androidx.camera.core.ImageProxy
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Chuyển đổi ImageProxy sang Bitmap và xoay đúng hướng.
 */
fun ImageProxy.toBitmapRotation(): Bitmap? {
    val bitmap = this.toBitmap() ?: return null
    
    return if (this.imageInfo.rotationDegrees != 0) {
        val matrix = Matrix()
        matrix.postRotate(this.imageInfo.rotationDegrees.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotatedBitmap != bitmap) {
            bitmap.recycle()
        }
        rotatedBitmap
    } else {
        bitmap
    }
}

/**
 * Cắt khuôn mặt từ ảnh gốc dựa trên Bounding Box của ML Kit với lề (margin)
 */
fun Bitmap.cropFace(boundingBox: Rect, marginPercent: Float = 0.15f): Bitmap {
    val marginW = (boundingBox.width() * marginPercent).toInt()
    val marginH = (boundingBox.height() * marginPercent).toInt()
    
    val left = (boundingBox.left - marginW).coerceAtLeast(0)
    val top = (boundingBox.top - marginH).coerceAtLeast(0)
    val right = (boundingBox.right + marginW).coerceAtMost(this.width)
    val bottom = (boundingBox.bottom + marginH).coerceAtMost(this.height)
    
    val width = right - left
    val height = bottom - top

    if (width <= 0 || height <= 0) return this
    
    return Bitmap.createBitmap(this, left, top, width, height)
}

/**
 * Resize bitmap to a target size and compress it
 */
fun Bitmap.resizeAndCompress(targetSize: Int = 720): Bitmap {
    val width = this.width
    val height = this.height
    
    val size = if (width < height) width else height
    val left = (width - size) / 2
    val top = (height - size) / 2
    
    val squareBitmap = Bitmap.createBitmap(this, left, top, size, size)
    
    val scaledBitmap = Bitmap.createScaledBitmap(squareBitmap, targetSize, targetSize, true)
    if (scaledBitmap != squareBitmap) {
        squareBitmap.recycle()
    }
    return scaledBitmap
}

/**
 * Lưu Bitmap vào tệp cache
 */
fun Bitmap.saveToFile(context: Context, fileName: String, quality: Int = 90): String? {
    return try {
        val file = File(context.cacheDir, fileName)
        FileOutputStream(file).use { out ->
            this.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

object ImageUtil {
    /**
     * Chuyển đổi Uri sang File (Dùng cho ProfileScreen)
     */
    fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
