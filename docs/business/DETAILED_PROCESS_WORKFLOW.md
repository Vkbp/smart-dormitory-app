# Quy trình Nghiệp vụ Chi tiết - SmartDormitory (Mobile App)

Tài liệu này mô tả chi tiết các bước thực hiện và quy tắc nghiệp vụ cho toàn bộ 15+ quy trình chính trên ứng dụng Mobile.

---

## I. NHÓM QUY TRÌNH DÀNH CHO SINH VIÊN (8 Quy trình)

### 1. Quy trình Đăng ký Khuôn mặt (Face Registration)
*   **Mục tiêu:** Cung cấp dữ liệu sinh trắc học để hệ thống IoT nhận diện khi ra vào cổng.
*   **Các bước thực hiện:**
    1.  Sinh viên vào mục "Đăng ký khuôn mặt".
    2.  Hệ thống kiểm tra trạng thái: Nếu đã có ảnh đang chờ duyệt (`PENDING`) hoặc đã duyệt (`APPROVED`), nút đăng ký sẽ bị ẩn.
    3.  Thực hiện quét khuôn mặt qua Camera (Sử dụng CameraX + Face Mesh để kiểm tra Liveness).
    4.  Hệ thống "đóng băng" khung hình đạt chuẩn và gửi lên Cloudinary/Server.
    5.  Gửi yêu cầu lưu vào cơ sở dữ liệu với trạng thái `PENDING`.

### 2. Quy trình Thanh toán Hóa đơn (Smart QR Payment)
*   **Mục tiêu:** Thanh toán các khoản phí lưu trú nhanh chóng và tự động.
*   **Các bước thực hiện:**
    1.  Sinh viên xem danh sách hóa đơn (Tiền phòng, điện, nước).
    2.  Chọn hóa đơn cần thanh toán -> Hiển thị **Smart QR** (Mã SePay định danh).
    3.  Sinh viên quét mã bằng App Ngân hàng hoặc chuyển khoản thủ công.
    4.  Hệ thống thực hiện **Polling (5 giây/lần)** để kiểm tra trạng thái thanh toán từ Backend.

### 3. Quy trình Quản lý chỗ ở & Đổi phòng (Room & Transfer)
*   **Mục tiêu:** Quản lý thông tin cư trú và thay đổi vị trí ở.
*   **Các bước thực hiện:**
    1.  Xem thông tin phòng, giường hiện tại và tiến độ lưu trú.
    2.  Chọn "Yêu cầu đổi phòng" -> Hệ thống tải danh sách phòng còn trống (`Available Rooms`).
    3.  Sinh viên chọn tòa nhà và phòng cụ thể qua **BottomSheet Picker**.
    4.  Nhập lý do đổi phòng và gửi đơn.

### 4. Quy trình Lịch sử ra vào & Giới nghiêm (Access & Curfew)
*   **Mục tiêu:** Theo dõi lịch sử cá nhân và báo cáo ngoại lệ.
*   **Các bước thực hiện:**
    1.  Xem danh sách các lần quét mặt thành công tại cổng (Lấy từ Access History).
    2.  Nếu cần về muộn: Chọn "Báo báo vào muộn" -> Nhập lý do và thời gian dự kiến.
    3.  Dữ liệu được lưu Local (Room) và đồng bộ Server để đối soát tự động khi quét mặt sau giờ giới nghiêm.

### 5. Quy trình Trả phòng sớm (Checkout Request)
*   **Mục tiêu:** Thủ tục hành chính khi kết thúc lưu trú trước hạn.
*   **Các bước thực hiện:**
    1.  Chọn "Trả phòng" -> Hệ thống kiểm tra hóa đơn chưa thanh toán.
    2.  **Chặn nghiệp vụ:** Nếu còn nợ, yêu cầu thanh toán trước khi tiếp tục.
    3.  Nhập lý do và thông tin ngân hàng nhận lại cọc -> Gửi đơn.

### 6. Quy trình Gia hạn lưu trú (Stay Extension)
*   **Mục tiêu:** Tiếp tục cư trú tại KTX cho học kỳ tiếp theo.
*   **Các bước thực hiện:**
    1.  Hệ thống kiểm tra đợt gia hạn đang mở.
    2.  Sinh viên chọn kỳ học và lý do gia hạn.
    3.  Tải về bản Hợp đồng và Bản cam kết (PDF) để ký (nếu là kỳ chính).
    4.  Gửi đơn và theo dõi trạng thái duyệt.

### 7. Quy trình Cập nhật Hồ sơ (Profile Update)
*   **Mục tiêu:** Đảm bảo thông tin liên lạc luôn chính xác.
*   **Các bước thực hiện:**
    1.  Xem thông tin định danh (CCCD, MSSV - Chỉ đọc).
    2.  Cập nhật số điện thoại cá nhân, địa chỉ thường trú.
    3.  Cập nhật thông tin cha/mẹ và số điện thoại liên hệ khẩn cấp.

### 8. Quy trình Thông báo & Báo hỏng (Notify & Issue)
*   **Mục tiêu:** Tương tác với BQL về tin tức và hạ tầng.
*   **Các bước thực hiện:**
    1.  Xem tin tức, thông báo chung từ ký túc xá.
    2.  Gửi báo cáo sự cố (Ví dụ: Hỏng bóng đèn, vòi nước).
    3.  Chọn khu vực (Trong phòng hoặc khu vực chung) và mô tả chi tiết.

---

## II. NHÓM QUY TRÌNH DÀNH CHO QUẢN TRỊ VIÊN (7 Quy trình)

### 1. Quy trình Trung tâm điều khiển (Admin Dashboard)
*   **Mục tiêu:** Giám sát tổng thể hoạt động KTX theo thời gian thực.
*   **Các bước thực hiện:**
    1.  Hệ thống tự động cập nhật số liệu: Đơn chờ duyệt, sinh viên đang nợ, số giường trống.
    2.  Tự động làm mới dữ liệu sau mỗi 30 giây.

### 2. Quy trình Kiểm soát thông minh (Smart Access)
*   **Mục tiêu:** Điều khiển cổng và xử lý sự cố tại chỗ.
*   **Các bước thực hiện:**
    1.  **Mở cổng từ xa:** Chọn cổng -> (Tùy chọn) Tìm kiếm và gắn tên sinh viên đang đứng tại cổng -> Nhấn mở.
    2.  **Khẩn cấp (Emergency):** Mở toàn bộ cửa trong phạm vi tòa nhà hoặc toàn bộ KTX (Yêu cầu nhập lý do).

### 3. Quy trình Duyệt khuôn mặt (Face Audit)
*   **Mục tiêu:** Kiểm soát chất lượng dữ liệu sinh trắc học.
*   **Các bước thực hiện:**
    1.  Xem danh sách ảnh sinh viên vừa đăng ký.
    2.  So sánh với ảnh hồ sơ gốc -> Phê duyệt để cập nhật vào AI Gateway hoặc Từ chối kèm lý do.

### 4. Quy trình Nhận phòng nhanh (Quick Check-in)
*   **Mục tiêu:** Số hóa thủ tục nhận phòng bằng QR và RFID.
*   **Các bước thực hiện:**
    1.  Quét mã QR trên CCCD sinh viên -> Hệ thống tự động tìm hồ sơ phân phòng.
    2.  Xác nhận thông tin sinh viên -> Thực hiện gán thẻ RFID bằng cách quét thẻ vào đầu đọc.

### 5. Quy trình Quản lý đơn Trả phòng (Checkout Management)
*   **Mục tiêu:** Phê duyệt đơn kết thúc lưu trú.
*   **Các bước thực hiện:**
    1.  Xem danh sách đơn `PENDING` -> Kiểm tra thông tin tài khoản ngân hàng của sinh viên.
    2.  Duyệt đơn: Hệ thống sẽ tự động thu hồi quyền ra vào cổng sau ngày trả phòng.

### 6. Quy trình Duyệt gia hạn (Extension Approval)
*   **Mục tiêu:** Xét duyệt danh sách sinh viên ở tiếp học kỳ sau.
*   **Các bước thực hiện:**
    1.  Xem hồ sơ sinh viên và lý do gia hạn.
    2.  Kiểm tra các bản cam kết/hợp đồng đi kèm.
    3.  Phê duyệt để cập nhật thời hạn hợp đồng mới lên hệ thống.

### 7. Quy trình Thông báo toàn hệ thống (Broadcast)
*   **Mục tiêu:** Truyền thông nhanh đến toàn bộ sinh viên.
*   **Các bước thực hiện:**
    1.  Soạn nội dung thông báo (Tiêu đề, nội dung).
    2.  Chọn đối tượng: Toàn bộ sinh viên hoặc theo từng tòa nhà.
    3.  Gửi lệnh Push Notification qua Firebase (FCM).

---

## III. QUY TRÌNH NỀN TẢNG (Shared)

### 1. Quy trình Xác thực (Authentication)
*   Đăng nhập bằng mã sinh viên/email và mật khẩu.
*   Cấp phát và quản lý JWT (Access Token & Refresh Token).
*   Gắn định danh sinh học (Vân tay/FaceID) để đăng nhập nhanh cho các lần sau.

### 2. Quy trình Bảo mật hệ thống (Security)
*   Kiểm tra thiết bị Root/Emulator khi khởi động ứng dụng.
*   TLS Pinning: Đảm bảo kết nối an toàn đến Server.
*   Mã hóa dữ liệu nhạy cảm lưu trong Local Database.

---
*Tài liệu được cập nhật dựa trên phiên bản mã nguồn 6.0.0.*
