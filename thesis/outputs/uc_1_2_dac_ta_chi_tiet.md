# ĐẶC TẢ CHI TIẾT USE CASE UC 1.2: QUẢN LÝ HỒ SƠ SINH VIÊN

### **Bảng 3.x. Đặc tả chi tiết Use Case Quản lý Hồ sơ Sinh viên**

| Tiêu chí | Nội dung |
| :--- | :--- |
| **Tên Use case** | **UC 1.2: Quản lý Hồ sơ Sinh viên** |
| **Actor** | Quản trị viên (Admin), Sinh viên. |
| **Mô tả** | Quản lý thông tin chi tiết của sinh viên nội trú, bao gồm việc sinh viên tự xem/cập nhật hồ sơ cá nhân và Admin thực hiện các thao tác quản trị hồ sơ, định danh thẻ RFID. |
| **Pre-conditions** | - Actor phải được xác thực thành công vào hệ thống.<br>- Đối với các chức năng của Admin: Phải có quyền `ROLE_ADMIN`. |
| **Post-conditions** | - **Thành công:** Thông tin hồ sơ được cập nhật vào CSDL; Mã thẻ RFID được gán chính xác cho sinh viên.<br>- **Thất bại:** Hệ thống báo lỗi (400, 404), giữ nguyên trạng thái dữ liệu cũ. |
| **Luồng sự kiện chính** | 1. Actor truy cập module Hồ sơ cá nhân hoặc Quản lý sinh viên.<br>2. Hệ thống kiểm tra quyền hạn và truy vấn dữ liệu sinh viên tương ứng.<br>3. Thực hiện các luồng mở rộng tùy theo vai trò Actor. |
| **Luồng sự kiện phụ** | - Actor hủy bỏ thao tác: Hệ thống đóng form và không lưu thay đổi. |
| **<Extend Use Case>** | **Xem hồ sơ cá nhân (Sinh viên)**<br>1. Sinh viên truy cập menu Hồ sơ.<br>2. Hệ thống lấy thông tin định danh `accountId` từ JWT Token.<br>3. `StudentService` truy vấn bảng `students` dựa trên liên kết `UserAccount`.<br>4. Trả về thông tin: Mã SV, Họ tên, CCCD, Email, SĐT, Khoa, Niên khóa và ảnh chân dung. |
| **<Extend Use Case>** | **Cập nhật hồ sơ cá nhân (Sinh viên)**<br>1. Tại trang cá nhân, Sinh viên thay đổi thông tin (SĐT, Địa chỉ liên lạc, Thông tin gia đình).<br>2. Hệ thống kiểm tra tính hợp lệ của dữ liệu đầu vào.<br>3. Cập nhật các trường thông tin thay đổi vào CSDL.<br>4. Trả về thông báo cập nhật hồ sơ thành công.<br><br>**Rẽ nhánh (Exception Flows):**<br>**3.1. Dữ liệu không hợp lệ:** Hệ thống báo lỗi các trường chưa đúng định dạng. |
| **<Extend Use Case>** | **Xem danh sách và Chi tiết sinh viên (Admin)**<br>1. Admin truy cập menu Quản lý sinh viên.<br>2. Hệ thống thực hiện tìm kiếm và phân trang (`Pageable`) dựa trên từ khóa hoặc trạng thái.<br>3. Admin chọn một sinh viên để xem chi tiết.<br>4. `StudentService` truy vấn toàn bộ dữ liệu hồ sơ theo `studentId`. |
| **<Extend Use Case>** | **Cập nhật thông tin sinh viên (Admin Only)**<br>1. Admin chỉnh sửa các thông tin hành chính (Mã SV, Khoa, Lớp, Trạng thái nội trú).<br>2. Hệ thống kiểm tra ràng buộc duy nhất (Unique) cho Mã SV và CCCD.<br>3. Lưu thay đổi vào CSDL.<br><br>**Rẽ nhánh (Exception Flows):**<br>**2.1. Trùng lặp thông tin định danh:** Báo lỗi "Mã sinh viên hoặc Số CCCD đã tồn tại". |
| **<Extend Use Case>** | **Gắn thẻ RFID (Admin Only)**<br>1. Admin chọn chức năng gán thẻ cho một sinh viên.<br>2. Nhập hoặc quét mã thẻ RFID UUID thông qua thiết bị đọc thẻ.<br>3. Hệ thống kiểm tra mã thẻ chưa được liên kết với hồ sơ nào khác.<br>4. Lưu mã thẻ vào cột `rfid_code` trong bảng `students`.<br>5. Hệ thống phát đi sự kiện `StudentRfidAssignedEvent` để đồng bộ dữ liệu IoT.<br><br>**Rẽ nhánh (Exception Flows):**<br>**3.1. Thẻ đã được sử dụng:** Thông báo lỗi "Mã thẻ RFID đã được gán cho một sinh viên khác". |

---

### **Ghi chú Kỹ thuật (Audit Note):**
*   **Bảo mật:** Luồng `getMyProfile` được bảo vệ nghiêm ngặt để tránh lỗi **IDOR** (Insecure Direct Object Reference) bằng cách lấy `studentId` trực tiếp từ JWT của người dùng đang đăng nhập, thay vì nhận ID từ client.
*   **Performance:** Sử dụng `StudentSpecification` để tối ưu hóa truy vấn tìm kiếm nâng cao với nhiều tiêu chí kết hợp.
*   **Real-time sync:** Việc gắn thẻ RFID kích hoạt cơ chế Event-driven để đảm bảo thiết bị IoT nhận biết được thẻ mới ngay lập tức.
