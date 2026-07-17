# SDMS Project Supreme Rules

Đây là văn bản pháp lý cao nhất của dự án. Mọi thay đổi kiến trúc và quy trình làm việc PHẢI tuân thủ các quy tắc dưới đây.

## 0. Documentation Governance (Hiến pháp tài liệu)
Dự án coi tài liệu là **Công dân hạng nhất** (First-class citizen). Tài liệu không chỉ là hướng dẫn mà là một phần không thể tách rời của Source Code.

1. **Documentation First**: Một tính năng chưa được mô tả trong tài liệu thì chưa được coi là tồn tại chính thức.
2. **SSOT (Single Source of Truth)**: Tài liệu phải luôn phản ánh trạng thái thực tế của Source Code. Nếu có mâu thuẫn, code là sự thật cuối cùng nhưng tài liệu đang bị lỗi (Outdated) và phải được sửa ngay lập tức.
3. **Định nghĩa Hoàn thành (DoD)**: Một task CHỈ được coi là hoàn thành khi toàn bộ tài liệu bị ảnh hưởng đã được đồng bộ hóa.
4. **Audit Documents là Living Documents**: Các tài liệu Audit, Technical Debt, và Decision Log phải được duy trì liên tục, không bao giờ được coi là "đã xong".
5. **Cấm xóa lịch sử**: Không bao giờ xóa các bản ghi lịch sử trong `AUDIT_CHANGELOG.md` hay `REFACTOR_HISTORY.md`. Luôn sử dụng cơ chế Append.
6. **Tính kế thừa**: Các quy tắc trong `PROJECT_RULE.md` và `AGENT.md` là tối cao và được kế thừa bởi mọi module/feature.
7. **Documentation Governance Workflow**: Mọi task thực thi phải tuân thủ quy trình: `Task Identification -> Implementation -> Verification -> Documentation Synchronization -> Completion Signature`.
8. **Failsafe Protocol**: Agent không được phép báo hoàn thành nếu chưa có bảng "AGENT SYNC VERIFICATION" đồng bộ giữa Code và Docs.

## 1. Architectural Rules
- **Clean Architecture**: Bắt buộc phân lớp (Presentation -> Domain <- Data).
- **MVI Pattern**: ViewModels phải sử dụng Contract (State, Event, Effect).
- **Dependency Injection**: 100% sử dụng Hilt.
- **Repository Pattern**: Domain chứa Interface, Data chứa Implementation.
- **Strict Layering**: ViewModel KHÔNG ĐƯỢC phép gọi trực tiếp DataSource hoặc DAO (Phải qua UseCase).
- **Unified Timeline Algorithm**: Việc hợp nhất các sự kiện từ nhiều module (Ví dụ: Face Verification + Access Logs) phải sử dụng fuzzy matching (khoảng trễ 10s) để xử lý độ trễ của thiết bị IoT.
- **Package Integrity**: Mọi thay đổi cấu trúc thư mục phải cập nhật [PACKAGE_INDEX.md](./docs/PACKAGE_INDEX.md).

## 2. API & Integration Rules
- **BaseResponse Wrapper**: Mọi API endpoint phải bọc dữ liệu trong `BaseResponse<T>`.
- **Idempotency**: Các tác vụ ghi (POST/PATCH) quan trọng phải có ID định danh chống trùng lặp.
- **API Documentation**: Mọi thay đổi API phải cập nhật [API_INTEGRATION_GUIDE.md](./docs/architecture/API_INTEGRATION_GUIDE.md).

## 3. Business Rules
- **Debt-Checking First**: Các tác vụ như Checkout/Gia hạn phải kiểm tra nợ hóa đơn (BR-R02).
- **Liveness Required**: Đăng ký khuôn mặt phải qua 4 bước Liveness Detection.
- **Curfew Request Policy**: Sinh viên bị chặn do giới nghiêm phải gửi yêu cầu vào trễ qua App. Việc mở cổng chỉ thực hiện khi có sự phê duyệt của Admin hoặc lệnh Remote Unlock (BR-A01).
- **Business Logic updates**: Mọi thay đổi nghiệp vụ phải cập nhật [BUSINESS_INDEX.md](./docs/BUSINESS_INDEX.md).

## 4. Production Readiness Rules
- **No Cleartext**: `android:usesCleartextTraffic` phải luôn là `false`. Mọi giao tiếp API phải qua HTTPS.
- **TLS Pinning**: Certificate pinning phải được xác thực với server thực tế trước khi release.
- **Log Stripping**: Timber/Logcat phải được strip bỏ hoặc giới hạn trong bản release (Sử dụng ProGuard/R8).
- **Security Audit Requirement**: Mọi PR lớn phải đi kèm một bản Security Audit report mini.

---
*Phê chuẩn bởi Documentation Architect AI.*
