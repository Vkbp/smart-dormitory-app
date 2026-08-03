# FEATURE F01: MOBILE IN-APP NOTIFICATION BELL (CHUÔNG THÔNG BÁO)

## 1. VISION (TẦM NHÌN)
Xây dựng một trung tâm thông báo (Notification Center) trực quan trên ứng dụng Mobile của sinh viên (Student App). Tính năng này giống như biểu tượng quả chuông trên các mạng xã hội, giúp sinh viên không bị bỏ lỡ bất kỳ sự kiện quan trọng nào từ hệ thống SDMS (duyệt đơn, nhắc nợ, hoàn tất thủ tục nhận phòng, đổi mã PIN). 

## 2. BUSINESS FLOW (LUỒNG NGHIỆP VỤ)
- **Hiển thị Badges:** Trên màn hình Home của app, biểu tượng quả chuông sẽ hiển thị số lượng thông báo chưa đọc (Unread Count).
- **Danh sách thông báo:** Khi bấm vào chuông, hiển thị danh sách các thông báo (đã đọc và chưa đọc), sắp xếp mới nhất lên đầu. Mỗi item sẽ hiển thị: Tiêu đề (Title), Nội dung (Message), Thời gian nhận (Time), và trạng thái chưa đọc (chấm xanh).
- **Điều hướng (Action URL):** Nếu thông báo có chứa `actionUrl` (ví dụ: `/student/bills/123`), khi người dùng bấm vào thông báo, App sẽ tự động chuyển hướng đến màn hình tương ứng (Màn hình chi tiết hóa đơn).
- **Đánh dấu đã đọc:** Sau khi bấm vào, hệ thống gọi API để cập nhật trạng thái `isRead = true` trên Backend và làm mờ thông báo đó đi.

## 3. IMPLEMENTATION ROADMAP (LỘ TRÌNH TRIỂN KHAI)
### A. Backend (`sdms-backend`)
- **Trạng thái:** Đã hoàn thiện.
- Backend đã có sẵn `NotificationEventListener` để bắt sự kiện lưu vào database và `NotificationController` để cung cấp các API:
  - `GET /api/v1/notifications`: Lấy danh sách phân trang.
  - `GET /api/v1/notifications/unread-count`: Đếm số thông báo chưa đọc.
  - `PUT /api/v1/notifications/{id}/read`: Đánh dấu 1 thông báo đã đọc.
  - `PUT /api/v1/notifications/read-all`: Đánh dấu tất cả đã đọc.

### B. Mobile App (`smart-dormitory-app` / `sdms-mobile-app`)
- **Bước 1 (Model/Service):** Định nghĩa `NotificationModel` map với `ApiResponse` của Backend. Tạo `NotificationRepository` để call API bằng Dio/Http.
- **Bước 2 (State Management):** Sử dụng Provider/Bloc/GetX để quản lý danh sách thông báo và số lượng chưa đọc toàn cục (Global State) để gắn lên App Bar.
- **Bước 3 (UI Component):** 
  - Tạo `NotificationBellWidget` gắn ở AppBar trang chủ.
  - Tạo `NotificationListScreen` dùng `ListView.builder`.
  - Thiết kế `NotificationCard` với UI rõ ràng, phân biệt màu sắc giữa "đã đọc" và "chưa đọc".
- **Bước 4 (Navigation Handling):** Xử lý parse cái `actionUrl` từ backend trả về để map với Routes/Navigation của Mobile App.

---

## 4. TRIGGER PROMPT (PROMPT DÀNH CHO AGENT MOBILE)
> Hãy copy toàn bộ đoạn Prompt dưới đây và gửi cho Agent khi bạn mở nó trong workspace của thư mục Mobile App.

```markdown
# TASK: IMPL IN-APP NOTIFICATION BELL cho Student App

Dựa trên hệ thống Backend SDMS đã hoàn thiện việc phát sự kiện (Event-Driven) và cung cấp API Notification, hãy thực hiện phát triển tính năng Chuông Thông Báo cho Mobile App theo các bước sau:

**1. Tích hợp API (Data Layer):**
- Tạo model `NotificationModel` chứa các trường: `id`, `title`, `message`, `actionUrl`, `type`, `isRead`, `createdAt`.
- Tạo Service gọi các endpoint sau với Bearer Token:
  - `GET /api/v1/notifications?page=0&size=20`
  - `GET /api/v1/notifications/unread-count`
  - `PUT /api/v1/notifications/{id}/read`
  - `PUT /api/v1/notifications/read-all`

**2. Quản lý trạng thái (State Management):**
- Tạo controller/provider để fetch dữ liệu từ API. Đảm bảo có hàm fetch unread count mỗi khi user vào Home.
- Xử lý pull-to-refresh danh sách thông báo và load more (infinite scroll).

**3. Phát triển UI (Presentation Layer):**
- **Chuông thông báo (AppBar):** Hiển thị biểu tượng `Icon(Icons.notifications)` trên màn hình Home. Nếu unread count > 0, gắn thêm 1 badge màu đỏ hiển thị con số.
- **Màn hình danh sách (Notification Screen):** Tạo UI cho danh sách thông báo. Những thông báo chưa đọc (`isRead == false`) cần có background nổi bật hơn (ví dụ màu xanh dương nhạt) hoặc 1 chấm tròn nhỏ.
- **Icon hiển thị theo Enum (Quan trọng):** Dựa vào trường `type`, thiết kế hàm mapping trả về Icon và Màu sắc tương ứng. App Sinh viên CHỈ cần quan tâm các loại Enum sau (tuyệt đối không xử lý `IOT_HARDWARE_ERROR` vì đó là của Admin):
  - `APPLICATION`: Liên quan tới đơn từ (Icon văn bản, màu xanh dương).
  - `ROOM`: Liên quan tới phòng ở/mã PIN (Icon chìa khóa/cửa, màu tím/cam).
  - `SYSTEM`: Hệ thống tự động báo (Icon robot/hệ thống, màu xám).
  - `ANNOUNCEMENT`: Loa phát thanh từ Ban quản lý (Icon cái loa, màu xanh lá).
  - `MAINTENANCE`: Xử lý báo hỏng (Icon cờ lê/bảo trì, màu vàng).
  - `PAYMENT`: Đóng tiền THÀNH CÔNG (Icon tick xanh xác nhận, màu xanh lá).
  - `ELECTRIC_FEE`, `ACCOMMODATION_FEE`, `PENALTY_FEE`: Thông báo BÁO NỢ cần đóng tiền (Icon tia sét/cảnh báo, màu đỏ để sinh viên chú ý).
- Text hiển thị cần rõ ràng, Title in đậm, Message hiển thị 2-3 dòng, Time được format thân thiện (vd: 2 giờ trước).

**4. Xử lý tương tác (Action):**
- Khi bấm vào 1 thông báo, ngay lập tức gọi API đánh dấu đã đọc (PUT `/{id}/read`) và cập nhật lại UI state.
- Nếu thông báo có trường `actionUrl` (chứa routing text như `/student/bills`, `/student/room`), hãy viết 1 hàm switch-case điều hướng người dùng tới đúng Màn hình (Screen) tương ứng trong App.

**YÊU CẦU KIỂM THỬ TỰ ĐỘNG (BẮT BUỘC):**
- Sau khi code xong, hãy chạy command build app hoặc test để đảm bảo không lỗi cú pháp.
- Đợi tôi xác nhận từng bước trước khi sang phần tiếp theo.
```
