# Business Index - SDMS Android

Ánh xạ các quy tắc nghiệp vụ Backend vào việc thực thi trên Mobile.

| Rule ID | Quy tắc | Android Implementation / Guard | Documentation |
| :--- | :--- | :--- | :--- |
| **BR-R02** | Chặn trả phòng khi còn nợ | `CheckoutViewModel` kiểm tra danh sách hóa đơn trước khi cho phép gửi đơn. | [Backend Rules](./smart-dormitory-management-system-main/docs/business/BUSINESS_RULES.md) |
| **BR-A02** | Độ phức tạp mật khẩu | Regex validation trong `ActivateRequest` và `ChangePasswordScreen`. | [Security Guide](./architecture/SECURITY_GUIDE.md) |
| **BR-S01** | Bảo vệ định danh (IDOR) | Client KHÔNG gửi `studentId` trong payload, Backend trích xuất từ JWT. | [API Integration](./architecture/API_INTEGRATION_GUIDE.md) |
| **BR-I01** | Giới hạn đăng ký khuôn mặt | UI ẩn nút đăng ký nếu `FaceProfile` đang `PENDING` hoặc `APPROVED`. | [Backend Rules](./smart-dormitory-management-system-main/docs/business/BUSINESS_RULES.md) |
| **BR-U01** | Kiểm soát định dạng file | `ImageUtil` kiểm tra MIME type trước khi upload avatar/document. | [API Integration](./architecture/API_INTEGRATION_GUIDE.md) |
| **BR-S04** | Chống trùng lặp (Idempotency) | `IdempotencyInterceptor` tự động gắn `eventId` cho các request IoT/Thanh toán. | [API Integration](./architecture/API_INTEGRATION_GUIDE.md) |
| **BR-C01** | Gán thẻ RFID khi nhận phòng | `CheckInViewModel` cho phép Admin quét và gán mã thẻ RFID cho sinh viên | [API Integration](./architecture/API_INTEGRATION_GUIDE.md) |
| **BR-A01** | Ngoại lệ giới nghiêm | Sinh viên bị chặn do giới nghiêm phải gửi yêu cầu qua `CurfewRequestScreen` | [Curfew Docs](./implementation/002_STUDENT_APP_CURFEW_REQUEST.md) |
| **BR-A03** | Quyền mở cửa từ xa | Quản trị viên có quyền mở khóa từ xa cho phòng/cổng khi có yêu cầu hợp lệ | [Smart Access](./architecture/API_INTEGRATION_GUIDE.md) |
| **BR-E01** | Nhật ký khẩn cấp | Mọi tác vụ khẩn cấp (Emergency Override) PHẢI kèm theo lý do để đối soát | [Smart Access](./architecture/API_INTEGRATION_GUIDE.md) |

---
*Nguồn nghiệp vụ gốc: `docs/smart-dormitory-management-system-main/docs/business/BUSINESS_RULES.md`.*
