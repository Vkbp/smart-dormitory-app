# BÁO CÁO AUDIT KỸ THUẬT: UC 1.2 QUẢN LÝ HỒ SƠ SINH VIÊN

## 1. THÀNH PHẦN KIẾN TRÚC (ARCHITECTURAL COMPONENTS)
- **Controller:** `StudentController.java` (Xử lý các HTTP requests từ Client).
- **Service:** `StudentService.java` (Chứa logic nghiệp vụ xử lý hồ sơ và sự kiện).
- **Repository:** `StudentRepository.java` (Giao tiếp CSDL thông qua Spring Data JPA).
- **Database Table:** `students` (Lưu trữ thông tin chi tiết sinh viên, hồ sơ hành chính, mã RFID).

## 2. CHI TIẾT LOGIC NGHIỆP VỤ (DETAILED BUSINESS LOGIC)

### 2.1. Quản lý Hồ sơ Cá nhân (Self-Service)
- **Xem hồ sơ:** Hệ thống truy xuất thông tin từ `SecurityContextHolder` để xác định `UserAccount` hiện hành, sau đó lấy thông tin `Student` liên kết.
- **Bảo mật (IDOR Prevention):** Sinh viên không truyền `studentId` từ Client. Backend tự động nhận diện ID từ JWT Token, ngăn chặn việc truy cập trái phép hồ sơ người khác.
- **Cập nhật hồ sơ:** Cho phép sinh viên cập nhật các thông tin liên lạc và thông tin gia đình. Sử dụng cơ chế cập nhật từng phần (Partial Update).

### 2.2. Quản trị Hồ sơ (Admin Management)
- **Truy vấn danh sách:** Sử dụng `StudentSpecification` để thực hiện tìm kiếm nâng cao (Dynamic Query) theo tên, mã sinh viên hoặc trạng thái, kết hợp phân trang (`Pageable`).
- **Cập nhật chuyên sâu:** Admin có quyền chỉnh sửa các thông tin hành chính như Khoa, Niên khóa, Mã sinh viên và Số CCCD.
- **Ràng buộc:** Mã sinh viên và Số CCCD phải là duy nhất trên toàn hệ thống.

### 2.3. Định danh RFID (IoT Integration)
- **Gán mã thẻ:** Admin cập nhật mã UUID của thẻ RFID vào hồ sơ sinh viên.
- **Cơ chế Event-Driven:** Sau khi lưu vào CSDL, `StudentService` phát đi sự kiện `StudentRfidAssignedEvent`.
- **Đồng bộ hóa:** Sự kiện này kích hoạt các Listener để đồng bộ dữ liệu mã thẻ mới xuống các thiết bị IoT tại các cổng ra vào ngay lập tức.

## 3. THÔNG TIN KỸ THUẬT (TECHNICAL DATA)
- **API Endpoints:**
    - `GET /api/v1/students/me`: Xem hồ sơ cá nhân.
    - `PATCH /api/v1/students/me`: Cập nhật hồ sơ cá nhân.
    - `GET /api/v1/students`: Lấy danh sách sinh viên (Admin).
    - `GET /api/v1/students/{id}`: Xem chi tiết một sinh viên (Admin).
    - `PATCH /api/v1/students/{id}`: Cập nhật thông tin sinh viên (Admin).
    - `POST /api/v1/students/{id}/rfid`: Gán mã thẻ RFID (Admin).
- **Entities:**
    - `Student`: `studentId`, `studentCode`, `fullName`, `cccd`, `email`, `phone`, `faculty`, `academicYear`, `rfidCode`, `status`.

## 4. GHI CHÚ CHO WRITING AI
- Nhấn mạnh vào giải pháp bảo mật chống **IDOR** bằng cách sử dụng `SecurityContext`.
- Giải thích về cơ chế **Event-Driven** khi gắn thẻ RFID giúp kết nối giữa phần mềm quản lý và phần cứng IoT.
- Đề cập đến tính linh hoạt của `JPA Specification` trong việc tìm kiếm hồ sơ.
- Luồng **Sequence Diagram** nên tập trung vào: "Cập nhật Profile (Sinh viên)" và "Gán thẻ RFID (Admin)".
