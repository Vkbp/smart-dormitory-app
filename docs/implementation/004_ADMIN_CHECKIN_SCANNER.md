# Tài liệu triển khai: Admin Check-in Scanner (QR CCCD)

## 1. Tổng quan
Tính năng này hỗ trợ cán bộ quản lý thực hiện thủ tục nhận phòng cho sinh viên thông qua việc quét mã QR trên CCCD chip Việt Nam. Tính năng giúp tăng tốc độ xử lý thủ tục và đảm bảo tính chính xác của dữ liệu.

## 2. Kiến trúc & Công nghệ
- **Công nghệ quét**: Sử dụng **CameraX** kết hợp với **Google ML Kit Barcode Scanning**.
- **Xử lý dữ liệu AI**: Tách chuỗi dữ liệu thô từ mã QR CCCD (định dạng `cccd|old_id|name|...`) để lấy số định danh 12 số.
- **Mô hình xử lý (MVI)**:
    - **State**: Quản lý trạng thái Camera, thông tin sinh viên tìm thấy, và trạng thái hội thoại (Bottom Sheet).
    - **Event**: `SearchStudent`, `ConfirmCheckIn`, `AssignRfid`.
    - **Effect**: Thông báo Toast, điều hướng UI.

## 3. Đặc tả Backend Sync (Dành cho Backend Agent)

> [!IMPORTANT]
> **Yêu cầu sửa lỗi Endpoint: `GET /api/v1/admin/check-in/search`**
> Hiện tại API đang thiếu trường `studentId` trong Response Data, dẫn đến việc không thể thực hiện tính năng gán thẻ RFID (`studentId` là khóa ngoại bắt buộc).

### Prompt dành cho Backend Agent:
```text
Hãy cập nhật DTO và Controller của Endpoint `GET /api/v1/admin/check-in/search`.
Yêu cầu:
1. Đảm bảo Response Data (CheckInSearchResponse) bao gồm đầy đủ các trường:
   - assignmentId (UUID)
   - studentId (UUID) - BẮT BUỘC BỔ SUNG
   - studentName (String)
   - studentCode (String)
   - citizenId (String)
   - gender (String)
   - portraitUrl (String)
   - buildingName, floorName, roomName, bedName (String)
2. Kiểm tra SQL Query/JPA Mapping để lấy đúng `student_id` từ bảng `student` liên kết với `assignment`.
```

## 4. Rationale (Giải trình khoa học)
- **Tại sao dùng ML Kit?**: Hiệu năng xử lý trên thiết bị (On-device) cực nhanh, không phụ thuộc vào Internet cho việc giải mã QR, tăng tính bảo mật dữ liệu cá nhân.
- **Thiết kế Split-screen**: Cân bằng giữa tính hiện đại (Camera) và tính tin cậy (Manual input) giúp ứng dụng hoạt động tốt trong mọi điều kiện ánh sáng.

---
*Phê duyệt bởi AI Governance Agent - SDMS Android Project.*
