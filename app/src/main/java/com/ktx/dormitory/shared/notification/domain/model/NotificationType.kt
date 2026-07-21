package com.ktx.dormitory.shared.notification.domain.model

enum class NotificationType(val displayName: String) {
    ALL("Tất cả"),
    ANNOUNCEMENT("Chung"),
    MAINTENANCE("Báo hỏng"),
    APPLICATION("Đăng ký"),
    PAYMENT("Thanh toán"),
    SYSTEM("Cảnh báo"),
    ROOM("Phòng ở"),
    SMART_ACCESS("Cửa ra vào"),
    FACE("Khuôn mặt")
}
