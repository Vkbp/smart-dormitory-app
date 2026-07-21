# FEATURE: Tích hợp Nghiệp vụ Gia hạn Lưu trú lên Mobile App (Student & Admin)

## 1. VISION (Tầm nhìn)
Đưa quy trình Gia hạn lưu trú (Stay Extension) lên nền tảng Mobile App nhằm:
- Giúp sinh viên dễ dàng thao tác nộp đơn gia hạn mọi lúc mọi nơi ngay trên điện thoại (Student App).
- Giúp Ban quản lý (Trưởng nhà/Admin) nhận được Notification và có thể xem xét, duyệt đơn gia hạn trực tiếp qua điện thoại (Admin App).
- Tối ưu hóa giao diện (UI/UX) trên Mobile sao cho xử lý tinh tế các trường hợp đặc biệt: Gia hạn Hè (không có PDF Hợp đồng) và Tự động duyệt (dành cho Trưởng phòng).

## 2. BUSINESS FLOW (Luồng Nghiệp vụ cốt lõi cần tuân thủ)

### 2.1. Phía Student App (Nộp đơn gia hạn)
- **Kiểm tra nợ đọng:** Nếu sinh viên có hóa đơn điện, nước, phòng chưa thanh toán, Mobile App phải gọi API kiểm tra hoặc chặn ngay từ đầu, hiển thị cảnh báo: *"Không thể gia hạn do còn nợ đọng"*.
- **Tự động điền dữ liệu:** App tự động gọi API lấy thông tin đợt gia hạn đang mở (Active Wave) và hiển thị thời gian bắt đầu/kết thúc cho sinh viên xem.
- **Xử lý hiển thị PDF:** Khi sinh viên xem lại đơn đã nộp (`/api/v1/students/stay-extensions/my-request`), UI phải ẩn nút "Xem Hợp Đồng" và "Xem Cam Kết" nếu Backend trả về giá trị `null` (đặc thù của Đợt gia hạn Hè <= 3 tháng).
- **Trạng thái Trưởng phòng:** Nếu sinh viên là Trưởng/Phó phòng, sau khi bấm nộp đơn, trạng thái sẽ nhảy thẳng sang `APPROVED` (Đã duyệt). UI cần có hiệu ứng chúc mừng hoặc hiển thị tag trạng thái phù hợp.

### 2.2. Phía Admin App (Duyệt gia hạn)
- **Danh sách chờ duyệt:** Hiển thị danh sách các đơn đang ở trạng thái `PENDING`.
- **Thông tin chi tiết:** Cần hiển thị rõ số tháng gia hạn để Admin biết đây là đợt Hè hay đợt Chính khóa.
- **Duyệt/Từ chối:** Khi bấm duyệt, cần hiển thị hộp thoại xác nhận. Nếu từ chối, bắt buộc nhập "Lý do từ chối".

## 3. IMPLEMENTATION ROADMAP (Lộ trình triển khai API)

### 3.1. Các API liên quan
- **Student App:**
  - `POST /api/v1/students/stay-extensions`: Nộp đơn gia hạn.
  - `GET /api/v1/students/stay-extensions/my-request`: Xem trạng thái đơn gia hạn của mình.
- **Admin App:**
  - `GET /api/v1/admin/stay-extensions?page=0&size=10`: Danh sách đơn gia hạn.
  - `PATCH /api/v1/admin/stay-extensions/{id}/approve`: Duyệt / Từ chối đơn gia hạn.

---

## 4. TRIGGER PROMPTS (Sử dụng để giao việc cho Mobile Agent)

Dưới đây là các Prompt chuẩn bị sẵn để bác copy và dán cho Agent phụ trách code Mobile App (Flutter/React Native) để đảm bảo Agent đó làm đúng chuẩn logic.

### 📌 Prompt 1: Dành cho tính năng Nộp Gia Hạn (Student App)
```text
@agent Tôi cần bạn code tính năng "Nộp đơn gia hạn Ký túc xá" (Stay Extension) trên ứng dụng Student App. 
Yêu cầu nghiệp vụ bắt buộc:
1. Giao diện: Thiết kế màn hình nộp đơn đẹp, hiện đại.
2. Xử lý logic Đợt Hè: Sau khi nộp xong, ở màn hình xem lại đơn, bạn phải kiểm tra trường `contractPdfUrl` và `commitmentPdfUrl` trả về từ API `/api/v1/students/stay-extensions/my-request`. Nếu 2 trường này là `null` (Trường hợp gia hạn Hè dưới 3 tháng), bạn không được hiển thị nút "Xem Hợp Đồng" hay "Xem Cam Kết", hoặc hiển thị thông báo "Đợt Hè không yêu cầu Hợp đồng cứng".
3. Auto-Approve: Nếu sinh viên là Trưởng/Phó phòng, API sẽ trả về trạng thái `APPROVED` ngay lập tức. Hãy xử lý UI hiển thị tag "Đã duyệt (Tự động)" thay vì "Chờ duyệt".
4. Tích hợp gọi API bằng Dio/Axios. Bạn hãy viết luôn class service gọi API tương ứng.
Hãy code màn hình này thật mượt mà, áp dụng Skeleton Loading khi tải dữ liệu!
```

### 📌 Prompt 2: Dành cho tính năng Duyệt Gia Hạn (Admin App)
```text
@agent Tôi cần bạn code tính năng "Duyệt đơn gia hạn Ký túc xá" (Stay Extension Management) trên ứng dụng Admin App.
Yêu cầu nghiệp vụ:
1. Màn hình danh sách: Hiển thị các đơn đang chờ duyệt (`PENDING`) sử dụng API `/api/v1/admin/stay-extensions`. Hỗ trợ Pull-to-refresh và Pagination.
2. Màn hình Chi tiết / Duyệt đơn: Khi Admin bấm vào một đơn, hiển thị chi tiết (Tên sinh viên, Phòng hiện tại, Lý do xin gia hạn).
3. Action: Có 2 nút "Duyệt" và "Từ chối". 
   - Nếu bấm "Duyệt", gọi API `PATCH /api/v1/admin/stay-extensions/{id}/approve` với status = APPROVED.
   - Nếu bấm "Từ chối", bắt buộc hiển thị 1 BottomSheet/Dialog yêu cầu nhập lý do từ chối (rejectReason) rồi mới được gọi API.
Thiết kế giao diện Admin phải gọn gàng, sử dụng Card để phân tách từng đơn vị. Bắt đầu code ngay màn hình này nhé.
```
