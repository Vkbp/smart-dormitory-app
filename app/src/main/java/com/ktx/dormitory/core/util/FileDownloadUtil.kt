package com.ktx.dormitory.core.util

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast

object FileDownloadUtil {

    /**
     * Tải file PDF về máy sử dụng DownloadManager
     * Chiến thuật "Song mã": 
     * 1. Luôn thử tải vào thư mục Public (Downloads) để tiện cho người dùng.
     * 2. Nếu máy báo thiếu quyền (WRITE_EXTERNAL_STORAGE) hoặc lỗi bộ nhớ,
     *    tự động chuyển sang thư mục Private (không cần quyền) để đảm bảo thành công.
     */
    fun downloadPdf(context: Context, url: String, fileName: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setTitle(fileName)
                setDescription("Đang tải tài liệu KTX...")
                setMimeType("application/pdf")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                
                // Chuẩn hóa tên file: Chỉ giữ ký tự an toàn để tránh lỗi hệ thống tập tin
                val safeName = fileName.replace(Regex("[^a-zA-Z0-9]"), "_")
                val finalFileName = if (safeName.endsWith(".pdf", ignoreCase = true)) safeName else "$safeName.pdf"
                
                try {
                    // Chiến thuật 1: Lưu vào thư mục Downloads chung (Tiện cho người dùng nhất)
                    // Trên Android 10+, lệnh này tự động dùng Scoped Storage, không cần xin quyền WRITE.
                    setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, finalFileName)
                } catch (e: Exception) {
                    // Chiến thuật 2 (Dự phòng): Lưu vào thư mục Private của App.
                    // Dùng khi thư mục Downloads chung bị lỗi (vd: thẻ SD hỏng, lỗi mount...)
                    // Cách này KHÔNG bao giờ cần xin quyền và luôn thành công.
                    setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, finalFileName)
                }

                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
            }

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(context, "Bắt đầu tải: $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Nếu vẫn lỗi, thử cách cuối cùng: Mở link ra trình duyệt để người dùng tự tải
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                Toast.makeText(context, "Đang mở trình duyệt để tải file...", Toast.LENGTH_SHORT).show()
            } catch (e2: Exception) {
                Toast.makeText(context, "Lỗi: ${e2.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    /**
     * KHÔNG thêm .pdf vào URL gốc vì link Cloudinary Raw không hỗ trợ
     */
    fun getNormalizedPdfUrl(url: String): String {
        return url
    }
}
