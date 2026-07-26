# TỔNG HỢP QUY TẮC VÀ LỖI CẦN TRÁNH KHI SINH SƠ ĐỒ DRAW.IO (XML)

Tài liệu này ghi lại các bài học kinh nghiệm và quy tắc tối cao để đảm bảo AI sinh mã XML cho draw.io luôn hiển thị đúng, đẹp và khớp nghiệp vụ.

## 1. CÁC LỖI KỸ THUẬT TỬ HUYỆT (TECHNICAL ERRORS)

### Lỗi 1: Mất dây nối và nhãn (Missing Render)
- **Hiện tượng:** Mở draw.io chỉ thấy các khối elip, không thấy mũi tên nối giữa chúng.
- **Nguyên nhân:** Thiếu thẻ `<mxGeometry relative="1" as="geometry" />` bên trong `mxCell` của dây nối.
- **Khắc phục:** Mọi `mxCell` có `edge="1"` bắt buộc phải có thẻ con `mxGeometry`.

### Lỗi 2: Nhãn biến thành hình thoi ◇ (HTML Parsing Error)
- **Hiện tượng:** Chữ `<<Include>>` hoặc `<<Extend>>` bị biến mất hoặc hiện ký hiệu lạ.
- **Nguyên nhân:** Thuộc tính `html=1` khiến draw.io hiểu nhầm cặp dấu `<< >>` là thẻ HTML không hợp lệ.
- **Khắc phục:** Luôn đặt `html=0` cho các ô nhãn (`edgeLabel`) hoặc bản thân dây nối.

### Lỗi 3: Lỗi "Bad Character" (XML Comment Error)
- **Hiện tượng:** Hệ thống báo lỗi không thể lưu file XML.
- **Nguyên nhân:** Sử dụng chuỗi gạch ngang kép `--` bên trong thẻ comment XML (Ví dụ: `<!-- SUB--FUNCTION -->`). XML cấm điều này.
- **Khắc phục:** Sử dụng gạch ngang đơn hoặc khoảng trắng trong comment (Ví dụ: `<!-- SUB FUNCTION -->`).

---

## 2. QUY TẮC NGHIỆP VỤ (BUSINESS RULES)

### Quy tắc 1: Ánh xạ nghiêm ngặt (Strict Mapping)
- Phải đối soát 100% với file `FEATURE_MAP.md`.
- Mỗi dòng ID trong Feature Map phải là một hình elip riêng biệt (Không gộp chung nếu không có yêu cầu).

### Quy tắc 2: Kết nối Actor (Actor-to-UC Links)
- **KHÔNG** nối Actor chung chung vào khối quản lý trung tâm.
- **PHẢI** nối Actor trực tiếp vào từng chức năng lẻ mà họ có quyền thực hiện (dựa trên cột Actor của Feature Map).

### Quy tắc 3: Quan hệ Extend và Include (Relationship Logic)
- **Chức năng con --(<<Extend>>)--> Quản lý trung tâm:** Thể hiện chức năng con là phần mở rộng chi tiết của phân hệ.
- **Chức năng con --(<<Include>>)--> Đăng nhập:** Thể hiện việc bắt buộc phải thông qua xác thực để thực hiện chức năng đó.
- **Quản lý trung tâm --(<<Extend>>)--> Đăng nhập:** Đường nối chính thể hiện sự phụ thuộc của toàn phân hệ vào hệ thống xác thực.

---

## 3. CẤU TRÚC XML CHUẨN (TEMPLATE)

Mẫu một đoạn mã nối dây an toàn:
```xml
<mxCell id="id_day_noi" value="" style="endArrow=open;dashed=1;html=1;rounded=0;" edge="1" parent="1" source="source_id" target="target_id">
  <mxGeometry relative="1" as="geometry" />
</mxCell>
<mxCell id="id_nhan" value="&lt;&lt;Include&gt;&gt;" style="edgeLabel;html=0;align=center;verticalAlign=middle;resizable=0;points=[];labelBackgroundColor=#ffffff;" vertex="1" connectable="0" parent="id_day_noi">
  <mxGeometry relative="1" as="geometry"><mxPoint as="offset" /></mxGeometry>
</mxCell>
```

---
*Ghi chú: AI bắt buộc phải đọc file này trước khi thực hiện chỉnh sửa bất kỳ file .xml nào trong folder diagrams.*
