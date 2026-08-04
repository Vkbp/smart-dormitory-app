# HƯỚNG DẪN CẬP NHẬT MOBILE APP - TÍNH NĂNG THANH TOÁN (PAYMENT) VÀ CHIA TIỀN ĐIỆN NƯỚC

**Gửi Mobile App Agent / Developer:**
Backend đã được cập nhật đáng kể về luồng thanh toán và quản lý tiền điện nước (Utility Bills). Dưới đây là các yêu cầu và tài liệu API để bạn cập nhật Mobile App tương ứng.

## 1. Yêu Cầu Cập Nhật Tính Năng Thanh Toán (Payment Instruction)
- **Thay đổi API:** API `GET /api/v1/payments/instruction/{billId}` hiện đã trả về thêm trường `"amount": <số tiền>` trong payload JSON.
- **Nhiệm vụ Mobile App:**
  - Trên màn hình hiển thị Hướng dẫn thanh toán (Chuyển khoản thủ công), bạn phải hiển thị rõ **Số tiền (Amount)** (vnd).
  - Cần có nút **"Copy" (Sao chép)** cho 3 trường quan trọng nhất:
    1. **Số tài khoản ngân hàng** (bankAccount)
    2. **Số tiền cần thanh toán** (amount) -> *Rất quan trọng, người dùng không được tự nhập sai số này!*
    3. **Nội dung chuyển khoản** (content) -> *Phải copy chính xác mã SDMS...*
  - Điều này giúp giảm thiểu việc sinh viên chuyển thiếu hoặc thừa tiền, hoặc ghi sai mã giao dịch.

## 2. Tính Năng Xem Hóa Đơn (Danh Sách Hóa Đơn Tiền Điện Của Phòng)
- Hóa đơn tiền điện (ELECTRIC_FEE) là **hóa đơn dùng chung của cả phòng** nhưng do **Trưởng Phòng (Room Leader)** đứng tên.
- **Trạng thái MỚI - "OVERDUE" (Quá hạn):**
  - Nếu tiền điện chưa được đóng qua ngày hết hạn, nó sẽ chuyển sang trạng thái `OVERDUE`.
  - Khác với tiền phòng (không đóng sẽ bị đuổi - EVICTED), tiền điện quá hạn **chỉ bị ghi nhận là nợ xấu**, chờ giải quyết.
  - Mobile App cần thêm giao diện UI Badge/Label (màu đỏ cam) để hiển thị trạng thái `OVERDUE` cho các Bill này.

## 3. TÍNH NĂNG MỚI: CHIA BILL TIỀN ĐIỆN (Split Utility Bill)
**Ngữ cảnh:** Trưởng phòng đóng tiền điện, nhưng có thành viên trong phòng (roommate) không chịu đóng góp. Trưởng phòng có quyền "Chia bill" đẩy khoản nợ (tiền phạt/phần chia) cho người đó.

**Nhiệm vụ Mobile App:**
- Giao diện chi tiết Hóa Đơn Tiền Điện (của Trưởng phòng) cần có nút **"Chia Bill" / "Báo Cáo Thành Viên Khất Tiền"**.
- Khi bấm vào, hiển thị Pop-up/Bottom Sheet:
  - **Danh sách sinh viên trong phòng** (hiển thị Avatar, Tên, Mã SV - lấy từ API chi tiết phòng `GET /api/v1/rooms/{roomId}/students`).
  - Trưởng phòng sẽ **chọn (checkbox)** các sinh viên chây ì không chịu trả.
  - Nhập **số tiền** phạt/chia cho mỗi người (ví dụ: mỗi người 50.000 VNĐ).
- **Gọi API Split Bill:**
  - **Endpoint:** `POST /api/v1/bills/{billId}/split`
  - **Payload:**
    ```json
    {
      "nonPayingStudentIds": [
        "uuid-of-student-1",
        "uuid-of-student-2"
      ],
      "amountPerStudent": 50000.00
    }
    ```
  - **Phản hồi (Response):** API trả về cục Hóa đơn gốc (của Trưởng phòng) với tổng tiền `amount` đã được **giảm trừ** tương ứng. 
  - Mobile App cần tải lại danh sách hóa đơn sau khi gọi thành công.

## 4. Xử Lý Giao Dịch Chuyển Dư / Thiếu (Partially Paid / Requires Refund)
- Trong trường hợp sinh viên chuyển thiếu, trạng thái Bill sẽ là `PARTIALLY_PAID`. Giao diện cần hiển thị số tiền ĐÃ ĐÓNG (`paidAmount`) và SỐ TIỀN CÒN LẠI (`amount - paidAmount`).
- Nếu chuyển dư, cờ `requiresRefund = true`. App cần hiển thị nhãn "Chờ hoàn tiền" bên cạnh Hóa đơn để báo cho sinh viên biết BQL đang xử lý hoàn trả khoản dư.

---
**Tóm tắt Checklist cho Mobile Agent:**
- [ ] Bổ sung trường `amount` vào màn hình Thanh toán chuyển khoản + Nút Sao chép.
- [ ] Xử lý UI Badge cho trạng thái `OVERDUE`.
- [ ] Cập nhật màn hình Chi tiết Hóa đơn (Chỉ áp dụng cho Hóa đơn điện - Trưởng phòng xem): Thêm luồng "Chia Bill".
- [ ] Hiện danh sách thành viên cùng phòng (từ Room ID) để Trưởng phòng chọn người gánh nợ.
- [ ] Call POST `/api/v1/bills/{billId}/split` và handle UI reload.
