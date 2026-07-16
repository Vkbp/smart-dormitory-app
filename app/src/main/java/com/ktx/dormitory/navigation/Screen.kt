package com.ktx.dormitory.navigation

sealed class Screen(val route: String, val title: String) {
    data object Splash : Screen("splash", "Chào mừng")
    data object Login : Screen("login", "Đăng nhập")
    data object StudentHome : Screen("student_home", "Trang chủ")
    data object AccessHistory : Screen("access_history", "Lịch sử ra vào")
    data object CurfewRequest : Screen("curfew_request", "Yêu cầu vào trễ")
    data object FaceRegistration : Screen("face_registration", "Đăng ký khuôn mặt")
    data object Profile : Screen("profile", "Hồ sơ cá nhân")
    data object Payment : Screen("payment", "Thanh toán")
    data object RoomInfo : Screen("room_info", "Thông tin phòng")
    data object PaymentHistory : Screen("payment_history", "Lịch sử thanh toán")
    data object ChangePassword : Screen("change_password", "Đổi mật khẩu")
    data object QuickExtend : Screen("quick_extend", "Gia hạn lưu trú")
    data object Checkout : Screen("checkout", "Trả phòng sớm")
    data object Notifications : Screen("notifications", "Thông báo")
    data object ForgotPassword : Screen("forgot_password", "Quên mật khẩu")
    data object FaceStatus : Screen("face_status", "Quản lý khuôn mặt")
    data object FaceVerificationHistory : Screen("face_verification_history", "Lịch sử xác thực")
    data object PaymentInstruction : Screen("payment_instruction", "Hướng dẫn chuyển khoản")
    data object RoomTransfer : Screen("room_transfer", "Đổi phòng")
    data object IssueHistory : Screen("issue_history", "Lịch sử báo hỏng")

    // --- ADMIN ROUTES ---
    data object AdminDashboard : Screen("admin_dashboard", "Hệ thống")
    data object AdminAccounts : Screen("admin_accounts", "Tài khoản")
    data object AdminReports : Screen("admin_reports", "Báo cáo")
    data object AdminSettings : Screen("admin_settings", "Cài đặt")

    // --- ADMIN UTILITIES ---
    data object AdminSmartAccess : Screen("admin_smart_access", "Điều khiển cửa")
    data object AdminFaceApproval : Screen("admin_face_approval", "Duyệt khuôn mặt")
    data object AdminCheckoutApproval : Screen("admin_checkout_approval", "Duyệt trả phòng")
    data object AdminExtensionApproval : Screen("admin_extension_approval", "Duyệt gia hạn")
    data object AdminCheckIn : Screen("admin_check_in", "Thủ tục nhận phòng")
    data object AdminNotificationBroadcast : Screen("admin_notification_broadcast", "Gửi thông báo")
}
