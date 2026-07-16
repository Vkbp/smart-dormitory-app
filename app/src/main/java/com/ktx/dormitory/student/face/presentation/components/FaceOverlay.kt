package com.ktx.dormitory.student.face.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp

@Composable
fun FaceOverlay(
    modifier: Modifier = Modifier,
    overlayColor: Color = Color.Black.copy(alpha = 0.6f),
    borderColor: Color = Color.White,
    isFaceDetected: Boolean = false,
    facePositionOk: Boolean = false
) {
    val activeBorderColor = if (isFaceDetected) {
        if (facePositionOk) Color(0xFF4CAF50) else Color(0xFFFFC107)
    } else borderColor

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        
        // Calculate Oval dimensions (occupy about 75% width)
        val ovalWidth = canvasWidth * 0.75f
        val ovalHeight = ovalWidth * 1.4f
        val left = (canvasWidth - ovalWidth) / 2
        val top = (canvasHeight - ovalHeight) / 2.5f 
        
        val ovalSize = Size(ovalWidth, ovalHeight)
        val ovalTopLeft = Offset(left, top)

        val path = Path().apply {
            addOval(Rect(ovalTopLeft, ovalSize))
        }

        // Draw darkened background with a hole
        clipPath(path, clipOp = ClipOp.Difference) {
            drawRect(color = overlayColor)
        }

        // Draw Oval border with glow effect if OK
        drawOval(
            color = activeBorderColor,
            topLeft = ovalTopLeft,
            size = ovalSize,
            style = Stroke(width = 6.dp.toPx())
        )
    }
}
