package com.ktx.dormitory.core.util

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast

object FileDownloadUtil {

    /**
     * Tải file PDF về máy.
     * Cải tiến: Thêm fallback mở trình duyệt ngay lập tức nếu là link HTTP hoặc link Local
     * để tránh lỗi "Download unsuccessful" của hệ thống.
     */
    fun downloadPdf(context: Context, url: String, fileName: String) {
        // Nếu là link HTTP (không có S) hoặc chạy local IP, ưu tiên mở trình duyệt để tải
        // vì trình duyệt xử lý HTTP và mạng nội bộ tốt hơn DownloadManager của hệ thống.
        if (url.startsWith("http://") || url.contains("10.0.2.2") || url.contains("192.168.")) {
            openInBrowser(context, url)
            return
        }

        try {
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setTitle(fileName)
                setDescription("Đang tải tài liệu KTX...")
                setMimeType("application/pdf")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                
                val safeName = fileName.replace(Regex("[^a-zA-Z0-9]"), "_")
                val finalFileName = if (safeName.endsWith(".pdf", ignoreCase = true)) safeName else "$safeName.pdf"
                
                try {
                    setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, finalFileName)
                } catch (e: Exception) {
                    setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, finalFileName)
                }

                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
            }

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Toast.makeText(context, "Bắt đầu tải: $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            openInBrowser(context, url)
        }
    }

    private fun openInBrowser(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            Toast.makeText(context, "Đang mở trình duyệt để tải file...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Không thể mở trình duyệt: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    /**
     * KHÔNG thêm .pdf vào URL gốc vì link Cloudinary Raw không hỗ trợ
     */
    fun getNormalizedPdfUrl(url: String): String {
        return url
    }
}
