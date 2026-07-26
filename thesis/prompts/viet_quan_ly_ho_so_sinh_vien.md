# PROMPT: VIẾT MỤC 3.4.2 - QUẢN LÝ HỒ SƠ SINH VIÊN

**Gửi Writing AI (Vỹ):**

Hãy sử dụng dữ liệu từ file Audit: `thesis/outputs/audit_quan_ly_ho_so_sinh_vien.md` và file đặc tả chi tiết: `thesis/outputs/uc_1_2_dac_ta_chi_tiet.md` để viết nội dung cho mục **3.4.2: Đặc tả kỹ thuật Module Quản lý Hồ sơ Sinh viên**.

**YÊU CẦU CỤ THỂ:**

1. **Văn phong:** Học thuật, trang trọng. Phân tích sâu về cơ chế bảo mật (IDOR) và cơ chế đồng bộ IoT (Event-driven).
2. **Sơ đồ (Mermaid):**
    - Vẽ 01 **Sequence Diagram** cho luồng **Cập nhật hồ sơ cá nhân**: Thể hiện việc Backend lấy ID từ SecurityContext thay vì từ Request Body.
    - Vẽ 01 **Sequence Diagram** cho luồng **Gán thẻ RFID**: Thể hiện việc `StudentService` phát đi `StudentRfidAssignedEvent` sau khi lưu vào CSDL.
3. **Bảng đặc tả:** Sử dụng nội dung từ file `thesis/outputs/uc_1_2_dac_ta_chi_tiet.md`.
4. **Liên kết văn cảnh:** Viết câu dẫn dắt mượt mà từ kết thúc của Mục 3.4.1 (Quản trị tài khoản) sang mục này.
5. **Định dạng:** Tuân thủ quy tắc ghi chú Hình X-Y và Bảng X-Y. Sơ đồ Mermaid sử dụng tone màu Trắng-Đen.

**Xác nhận:** Hãy yêu cầu tôi cung cấp đoạn cuối của mục 3.4.1 để bạn bắt đầu viết!
