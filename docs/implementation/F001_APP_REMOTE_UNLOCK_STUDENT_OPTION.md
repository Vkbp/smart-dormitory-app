# F001 - Bổ sung tùy chọn gắn mã sinh viên khi mở cổng từ xa (Mobile App - Admin)

## 1. Vision & Mục tiêu
- **Mục tiêu:** Cập nhật tính năng "Mở cổng từ xa" trên giao diện Admin của Mobile App (`smart-dormitory-app`) để cho phép chọn sinh viên (tùy chọn) khi thực hiện mở cổng.
- **Lợi ích:** Đảm bảo tính đồng bộ với Web Admin, giúp hệ thống lưu trữ được lịch sử ra/vào `access_history` chính xác với định danh sinh viên được mở cửa hộ, phục vụ công tác đối soát và an ninh (phát hiện phạt nguội, điểm danh).

## 2. Business Flow
1. **Người dùng (Admin/Bảo vệ):** Đăng nhập vào Mobile App với tài khoản có quyền mở cổng từ xa (Admin/Security).
2. **Thao tác mở cổng:** Chọn cổng cần mở. Lúc này form xác nhận mở cổng không chỉ có nút xác nhận mà sẽ hiển thị thêm một trường tìm kiếm sinh viên (Search box/Dropdown).
3. **Tìm kiếm (Tùy chọn):** 
   - Nếu để trống: Hành động mở cổng được ghi nhận là do Admin mở không gắn với sinh viên cụ thể.
   - Nếu nhập/tìm kiếm sinh viên: Hệ thống gọi API tìm kiếm sinh viên và hiển thị danh sách (Avatar, Mã SV, Tên SV). Admin chọn một sinh viên.
4. **Thực thi:** Gọi API POST `/api/v1/access/gates/{gateId}/unlock?buildingId=...&studentId=...` với thông tin `studentId` được gắn vào (nếu có chọn).

## 3. Implementation Roadmap

### 🔴 Backend (`sdms-backend`)
- **Trạng thái:** ĐÃ HOÀN THÀNH. 
- API `RemoteUnlockController` đã hỗ trợ tham số `@RequestParam(required = false) UUID studentId`.
- **Hành động:** Không cần cập nhật thêm logic backend.

### 🔵 Web Frontend (`sdms-frontend`)
- **Trạng thái:** ĐÃ HOÀN THÀNH.
- Tại `SmartAccessManagement.tsx`, tính năng `Autocomplete` tìm kiếm sinh viên khi mở cổng từ xa đã được tích hợp và hoạt động tốt.
- **Hành động:** Không cần cập nhật.

### 📱 Mobile App (`smart-dormitory-app`)
- **Trạng thái:** ĐÃ HOÀN THÀNH.
- **Công việc:**
  1. Cập nhật UI Modal/Screen "Remote Gate Unlock" dành cho Admin: Đã thêm `Student Search` vào `RemoteUnlockDialog`.
  2. Thêm component SearchBar hoặc Dropdown hỗ trợ debounce để tìm kiếm sinh viên: Đã triển khai trong `SmartAccessViewModel` và `SmartAccessScreen`.
  3. Xử lý logic chọn sinh viên, lưu `studentId` vào state: Đã thêm vào `SmartAccessUiState`.
  4. Cập nhật lời gọi hàm gọi API Remote Unlock truyền thêm `studentId` query param: Đã cập nhật `AdminApiService`, `AdminRepository`, và `RemoteUnlockUseCase`.

## 4. Trigger Prompt (Dành cho AI Agent bên phía Mobile App)
Hãy copy prompt dưới đây và dán vào AI/Cursor đang làm việc với Mobile App:

```text
Yêu cầu: Bổ sung trường tìm kiếm và chọn Sinh viên (Tùy chọn) vào tính năng "Mở cổng từ xa" ở phần Admin của Mobile App.

Bối cảnh: Backend API `POST /api/v1/access/gates/{gateId}/unlock` đã hỗ trợ query param `studentId` (optional). Hiện tại trên Mobile App tính năng này chỉ mở cổng mà chưa cho phép gắn định danh sinh viên được mở hộ (Web Admin đã có tính năng này).

Công việc cần làm:
1. Xác định UI/Screen của tính năng "Mở khóa cổng từ xa" dành cho Admin/Bảo vệ.
2. Thêm một component tìm kiếm sinh viên (hỗ trợ nhập text, debounce, và gọi API lấy danh sách sinh viên). Nên hiển thị danh sách gồm Mã sinh viên + Họ tên + (Avatar nếu có).
3. Đây là trường tùy chọn (Optional). Nghĩa là admin có thể không chọn sinh viên nào và vẫn mở được cổng.
4. Cập nhật hàm xử lý việc gọi API remote unlock để truyền thêm `studentId` (nếu có) xuống backend.

Hãy lập kế hoạch các file cần sửa trước khi bắt tay vào code!
```
