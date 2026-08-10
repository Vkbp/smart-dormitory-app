package com.ktx.dormitory.student.face.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun FaceErrorPopup(
    errorMessage: String,
    onDismiss: () -> Unit
) {
    val isNoFaceDetected = errorMessage.contains("No face detected", ignoreCase = true) ||
                          errorMessage.contains("không thấy mặt", ignoreCase = true)

    val isAiError = isNoFaceDetected ||
                   errorMessage.contains("AI", ignoreCase = true) || 
                   errorMessage.contains("face", ignoreCase = true) ||
                   errorMessage.contains("mặt", ignoreCase = true) ||
                   errorMessage.contains("low quality", ignoreCase = true)

    val displayTitle = if (isAiError) "Không nhận diện được khuôn mặt" else "Lỗi xác thực"
    
    val displayMessage = when {
        isNoFaceDetected -> "Ảnh không đạt chuẩn hoặc không thấy mặt. Vui lòng nhìn thẳng và giữ máy đủ sáng!"
        errorMessage.length > 20 -> errorMessage.replace("AI_ERROR: ", "").replace("Đăng ký khuôn mặt thất bại: ", "").replace("Yêu cầu thay đổi thất bại: ", "")
        isAiError -> "Hệ thống AI không thể nhận diện khuôn mặt của bạn. Vui lòng chụp lại ảnh rõ nét, thẳng mặt và đảm bảo đủ ánh sáng!"
        else -> errorMessage
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = displayTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = displayMessage,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("THỬ LẠI", fontWeight = FontWeight.Bold)
            }
        }
    )
}
