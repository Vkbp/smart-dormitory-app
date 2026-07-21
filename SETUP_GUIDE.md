# Hướng dẫn Setup Dự án SDMS Android 🚀

Chào bạn! Đây là hướng dẫn chi tiết để bạn có thể chạy được ứng dụng **Smart Dormitory Management System (SDMS)** trên máy của mình.

## 1. Yêu cầu hệ thống (Prerequisites)
- **Android Studio**: Phiên bản **Ladybug (2024.2.1)** hoặc mới hơn.
- **JDK**: Java Development Kit **17**.
- **Thiết bị**: 
    - Khuyên dùng **Máy thật (Physical Device)** chạy Android 7.0 (API 24) trở lên để test các tính năng AI (Nhận diện khuôn mặt) và Camera.
    - Nếu dùng Emulator, đảm bảo đã cài đặt Google Play Services.

## 2. Các bước thiết lập (Setup Steps)

### Bước 1: Clone Project
```bash
git clone https://github.com/Vkbp/smart-dormitory-app.git
```

### Bước 2: Cấu hình API Backend
Ứng dụng cần kết nối với Server để hoạt động. Bạn cần tạo/chỉnh sửa file `local.properties` ở thư mục gốc của dự án:

1. Mở file `local.properties`.
2. Thêm dòng sau:
   ```properties
   BASE_URL=http://<IP_CỦA_BẠN>:8080/api/
   ```
   *   **Nếu dùng Emulator (Android Studio):** Sử dụng IP `http://10.0.2.2:8080/api/` để kết nối với localhost của máy tính.
   *   **Nếu dùng Máy thật (Physical Device):** Bạn phải sử dụng IP LAN của máy tính (ví dụ: `http://192.168.1.5:8080/api/`). Đảm bảo điện thoại và máy tính cùng kết nối một mạng WiFi.
   *   *Lưu ý: Không sử dụng `localhost` hoặc `127.0.0.1` vì Android sẽ hiểu đó là chính nó chứ không phải máy tính của bạn.*

### Bước 3: Cấu hình Network Security (Nếu cần)
Nếu bạn sử dụng một IP lạ không có trong danh sách mặc định, bạn có thể gặp lỗi `Cleartext communication not permitted`.
1. Mở file `app/src/main/res/xml/network_security_config.xml`.
2. Thêm IP của bạn vào danh sách `<domain>`:
   ```xml
   <domain includeSubdomains="true">IP_CỦA_BẠN</domain>
   ```
   *(Mặc định dự án đã hỗ trợ `10.0.2.2` và một số dải IP phổ biến).*

### Bước 3: Build & Sync Gradle
- Mở project bằng Android Studio.
- Đợi Android Studio tải các dependency (Gradle Sync). 
- Nếu gặp lỗi Sync, hãy thử: `File -> Invalidate Caches / Restart`.

### Bước 4: Chạy ứng dụng
- Kết nối điện thoại hoặc mở Emulator.
- Nhấn nút **Run (Tam giác xanh)** trên thanh công cụ.

## 3. Các lưu ý quan trọng (Important Notes)

### 🛡️ Bảo mật & Mạng (Network Security)
- Dự án đã được cấu hình để cho phép truy cập HTTP (Cleartext) cho một số dải IP nội bộ phục vụ việc phát triển. Nếu IP của bạn không nằm trong danh sách, hãy cập nhật tại:
  `app/src/main/res/xml/network_security_config.xml`.

### 👤 Tài khoản Test
- Bạn có thể đăng nhập bằng tài khoản Student hoặc Admin tùy theo dữ liệu có trong Database của Backend.

### 📸 Tính năng Face Recognition
- Khi đăng ký khuôn mặt, hãy đảm bảo ánh sáng tốt.
- Tính năng này sử dụng Google ML Kit, sẽ tự động tải model khi chạy lần đầu (cần có Internet).

## 4. Cấu trúc thư mục chính
- `presentation/`: Giao diện người dùng (Jetpack Compose).
- `domain/`: Business logic, UseCases và Interface.
- `data/`: Triển khai Repository, API (Retrofit), và Database (Room).
- `core/`: Các tiện ích dùng chung (Security, Utils, Constants).

## 5. Xử lý sự cố (Troubleshooting)
- **Lỗi "Cleartext communication not permitted"**: Kiểm tra lại `BASE_URL` trong `local.properties` đã dùng HTTPS chưa, hoặc thêm IP vào `network_security_config.xml`.
- **Lỗi Database**: Nếu app crash sau khi update, hãy thử xóa app và cài lại (do thay đổi Schema của Room Database).

---
Chúc bạn chạy đồ án thành công! Nếu có vấn đề gì cứ nhắn mình nhé. ✌️
