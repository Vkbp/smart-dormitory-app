# BÁO CÁO AUDIT KỸ THUẬT: UC 1.1 QUẢN TRỊ TÀI KHOẢN & PHÂN QUYỀN

## 1. THÀNH PHẦN KIẾN TRÚC (ARCHITECTURAL COMPONENTS)
- **Controller:**
    - `AuthController.java`: Xử lý Đăng nhập, Đăng xuất, Kích hoạt, Quên/Đổi mật khẩu.
    - `AdminAccountController.java`: Xử lý Danh sách, Khóa/Mở khóa, Tạo cán bộ.
- **Service:**
    - `AuthService.java`: Logic xác thực JWT, bảo mật mật khẩu, cơ chế Reset Password.
    - `UserService.java`: Quản lý trạng thái tài khoản, phân quyền (Role-based).
- **Repository:** `UserAccountRepository.java` (Sử dụng Spring Data JPA).
- **Database Table:** `user_accounts` (Lưu trữ credentials, status, roles, token).

## 2. CHI TIẾT LOGIC NGHIỆP VỤ (DETAILED BUSINESS LOGIC)

### 2.1. Đăng nhập & Bảo mật (Authentication)
- **Cơ chế:** Sử dụng JWT (Access Token & Refresh Token xoay vòng).
- **Thuật toán băm mật khẩu:** `BCryptPasswordEncoder` (tự động tạo salt).
- **JWT Provider:** `JwtService.java`.
- **Thời hạn Token:**
    - Access Token: 15 phút (`900,000` ms).
    - Refresh Token: 7 ngày (`604,800,000` ms).
- **Middleware bảo mật:** `JwtAuthenticationFilter` tích hợp trong `SecurityFilterChain` (kiến trúc Stateless).
- **Xác thực:** Hỗ trợ đăng nhập bằng `username` (Mã SV) hoặc `email`.
- **Ràng buộc an toàn:**
    - Sai mật khẩu 5 lần liên tiếp -> Khóa tạm thời 15 phút (`failed_login_attempts`, `lock_time`).
    - Chỉ tài khoản có trạng thái `ACTIVE` mới được đăng nhập.
    - Tài khoản `PENDING_ACTIVATION` bị từ chối đăng nhập (phải qua bước kích hoạt).

### 2.2. Kích hoạt tài khoản (Activation)
- **Trạng thái khởi tạo:** Tài khoản sinh viên được tạo tự động sau khi duyệt đơn đăng ký với trạng thái `PENDING_ACTIVATION`.
- **Logic:** Sinh viên dùng `tempPassword` (Số CCCD/Mã SV) để thiết lập `newPassword`. Sau khi thành công, trạng thái chuyển sang `ACTIVE`.
- **Enums liên quan:**
    - **Role:** `STUDENT`, `STAFF`, `ADMIN`.
    - **AccountStatus:** `PENDING_ACTIVATION`, `ACTIVE`, `LOCKED`.

### 2.3. Khôi phục mật khẩu (Password Reset)
- **Cơ chế:** Gửi Email chứa Link khôi phục (Secure Token).
- **Bảo mật Token:**
    - Raw Token (32 bytes) được gửi qua Email.
    - Hashed Token (SHA-256) được lưu trong CSDL (`reset_password_token`).
    - Thời gian hết hạn: 15 phút.

### 2.4. Quản trị hệ thống (Admin Tasks)
- **Xem danh sách:** Hỗ trợ tìm kiếm theo từ khóa, lọc theo `Role` (ADMIN, STAFF, STUDENT) và `Status`.
- **Khóa/Mở khóa:** 
    - Chuyển trạng thái `ACTIVE` <-> `LOCKED`.
    - Thu hồi `refreshToken` ngay lập tức khi khóa tài khoản.
    - **Chống lỗi hệ thống:** Không cho phép Admin tự khóa chính mình; Không được khóa tài khoản ADMIN khác; Không được thao tác trên tài khoản `PENDING_ACTIVATION`.
- **Tạo cán bộ:** Admin tạo tài khoản STAFF trực tiếp.

## 3. THÔNG TIN KỸ THUẬT (TECHNICAL DATA)
- **API Endpoints:**
    - `POST /api/v1/auth/login`
    - `POST /api/v1/auth/activate`
    - `POST /api/v1/auth/forgot-password`
    - `POST /api/v1/auth/reset-password`
    - `PATCH /api/v1/admin/accounts/{id}/toggle-lock` (Admin only)
    - `POST /api/v1/admin/accounts/staff` (Admin only)
- **Entities:**
    - `UserAccount`: `accountId`, `username`, `email`, `password`, `role`, `status`, `lastLogin`, `failedLoginAttempts`.

## 4. GHI CHÚ CHO WRITING AI
- Đây là Module nền tảng của hệ thống, cần nhấn mạnh vào tính bảo mật (mã hóa mật khẩu, JWT, cơ chế chống Brute-force).
- Logic Kích hoạt tài khoản (Activation) là điểm quan trọng trong trải nghiệm sinh viên mới.
- Các sơ đồ Tuần tự (Sequence) nên tập trung vào luồng "Đăng nhập" và "Kích hoạt".
