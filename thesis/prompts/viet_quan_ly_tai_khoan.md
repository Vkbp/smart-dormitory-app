# PROMPT: VIẾT MỤC 3.4.1 - QUẢN TRỊ TÀI KHOẢN & PHÂN QUYỀN

**Gửi Writing AI (Vỹ):**

Hãy sử dụng dữ liệu từ file Audit: `thesis/outputs/audit_quan_ly_tai_khoan.md` để viết nội dung chi tiết cho mục **3.4.1: Đặc tả kỹ thuật Module Quản trị Tài khoản**.

**THÔNG TIN KỸ THUẬT BỔ SUNG (Xác thực từ Code):**
1. **Mã hóa:** Sử dụng `BCryptPasswordEncoder` (Spring Security Core).
2. **JWT:** Access Token (15 phút), Refresh Token (7 ngày). Lớp xử lý: `JwtService`.
3. **Cấu hình Security:** `SecurityFilterChain` với `Stateless Session Management`. Middleware kiểm tra token: `JwtAuthenticationFilter`.
4. **Enums:**
   - Role: `STUDENT`, `STAFF`, `ADMIN`.
   - AccountStatus: `PENDING_ACTIVATION`, `ACTIVE`, `LOCKED`.

**YÊU CẦU CỤ THỂ:**

1. **Văn phong:** Chuyên nghiệp, học thuật. Diễn giải các cơ chế kỹ thuật (JWT, SHA-256, Brute-force protection) một cách dễ hiểu nhưng trang trọng.
2. **Sơ đồ (Mermaid):**
    - Vẽ 01 **Sequence Diagram** cho luồng **Đăng nhập (Login)**: Thể hiện việc kiểm tra trạng thái tài khoản, kiểm tra mật khẩu, cập nhật số lần sai, và trả về JWT.
    - Vẽ 01 **Activity Diagram** cho luồng **Kích hoạt tài khoản (Activation)**: Từ lúc nhập mật khẩu tạm thời đến khi đổi mật khẩu và chuyển trạng thái ACTIVE.
3. **Bảng đặc tả:** Sử dụng bảng Đặc tả Use Case 1.1 mà User đã cung cấp làm khung nội dung chính (nhưng thay các nội dung "Khách hàng" bằng "Tài khoản/Người dùng"). Hãy "hàn lâm hóa" các bước thực hiện dựa trên logic code thực tế.
4. **Liên kết văn cảnh:** Sử dụng đoạn cuối của Mục 3.3 đã được cung cấp để viết câu dẫn dắt.
5. **Định dạng:** Tuân thủ quy tắc ghi chú Hình X-Y và Bảng X-Y. Sơ đồ Mermaid sử dụng tone màu Trắng-Đen.

**Xác nhận:** Bạn đã có đủ 100% dữ liệu "Truth" từ mã nguồn. Hãy thực hiện viết Mục 3.4.1 ngay bây giờ!
