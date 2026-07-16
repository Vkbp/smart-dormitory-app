# ĐỀ XUẤT CHỨC NĂNG MỞ RỘNG CHO MOBILE APP (SINH VIÊN)

> **Mục tiêu:** Tài liệu này mô tả các chức năng đề xuất mở rộng dành cho ứng dụng Mobile Smart Dormitory. Đây là các tính năng hướng đến trải nghiệm sinh viên và là cơ sở để nhóm Backend phân tích, thiết kế Database, API và Business Logic trong các giai đoạn tiếp theo.

---

# PRIORITY P1 - ƯU TIÊN CAO

## 1. Báo hỏng cơ sở vật chất

### Mô tả

Sinh viên gửi yêu cầu sửa chữa trực tiếp trên ứng dụng.

### Chức năng Mobile

* Chụp nhiều ảnh
* Chọn loại hỏng
* Nhập mô tả
* Theo dõi tiến độ

### Backend cần phát triển

### Database

```
maintenance_requests
```

### API

```
POST /maintenance/requests
GET /maintenance/requests
GET /maintenance/requests/{id}
```



# PRIORITY P2 - ƯU TIÊN TRUNG BÌNH

## 3. Quản lý khách thăm

### Mô tả

Sinh viên đăng ký khách đến thăm và sinh mã QR để khách sử dụng tại cổng.

### Chức năng Mobile

* Nhập thông tin khách
* Chọn thời gian
* Sinh QR Code

### Backend cần phát triển

### Database

```
visitor_requests
```

### API

```
POST /visitors
GET /visitors
```

---

# PRIORITY P3 - MỞ RỘNG

## 4. Thống kê điện nước

### Mô tả

Hiển thị biểu đồ sử dụng điện nước theo thời gian.

### Mobile

* Line Chart
* Bar Chart

### Backend cần phát triển

### Database

```
utility_usage
```

### API

```
GET /utilities/usage
```

---

## 5. AI Chatbot

### Mô tả

Trợ lý AI giải đáp các câu hỏi liên quan đến KTX.

### Ví dụ

* Nội quy
* Hóa đơn
* Gia hạn
* Đổi phòng
* Báo hỏng

### Backend

* LLM
* RAG
* LangChain

---

## 6. SOS Khẩn cấp

### Mô tả

Cho phép sinh viên gửi cảnh báo khẩn cấp tới bảo vệ.

### Mobile

Một nút SOS lớn.

Thông tin gửi:

* Sinh viên
* Phòng
* Tòa nhà
* GPS
* Thời gian

### Backend

* Push Notification
* Dashboard bảo vệ
* Nhật ký sự kiện

---

## 7. QR Check-in Nội bộ

### Mô tả

Sử dụng QR cho các hoạt động nội bộ.

Ví dụ:

* Hội nghị
* Điểm danh
* Sự kiện
* Workshop

### Backend

```
qr_sessions
qr_checkins
```

---

# CÁC CHỨC NĂNG NÊN BỔ SUNG

## 8. Lịch cắt điện / cắt nước

Sinh viên nhận thông báo trước khi bảo trì.

Backend

```
maintenance_schedule
```

---

## 9. Thông báo nhận bưu phẩm

Sinh viên biết khi có hàng được gửi đến KTX.

Backend

```
packages
```

---

## 10. Đặt lịch sử dụng máy giặt

Cho phép đặt trước khung giờ sử dụng phòng giặt.

Backend

```
laundry_booking
```

---

## 11. Đăng ký chuyển phòng

Sinh viên gửi yêu cầu chuyển phòng trực tuyến.

Backend

```
room_transfer_requests
```

---

## 12. Xin giấy xác nhận nội trú

Sinh viên gửi yêu cầu và tải giấy xác nhận dưới dạng PDF.

Backend

```
resident_certificates
```

---

## 13. Đánh giá dịch vụ

Sinh viên đánh giá sau khi yêu cầu được xử lý.

Ví dụ

* Báo hỏng
* Chuyển phòng
* Gia hạn

Backend

```
service_feedback
```

---

## 14. Góp ý & Phản ánh

Cho phép gửi góp ý trực tiếp đến Ban quản lý.

Backend

```
feedback
```

---

## 15. Khảo sát sinh viên

Ban quản lý tạo khảo sát trực tuyến.

Backend

```
surveys
survey_answers
```

---

# TỔNG KẾT

## Chức năng đề xuất theo mức ưu tiên

### ⭐⭐⭐⭐ P1

* Báo hỏng
* Thông báo FCM

---

### ⭐⭐⭐ P2

* Quản lý khách thăm

---

### ⭐⭐ P3

* Thống kê điện nước
* AI Chatbot
* SOS
* QR Check-in
* Lịch bảo trì
* Bưu phẩm
* Máy giặt
* Chuyển phòng
* Giấy xác nhận nội trú
* Đánh giá dịch vụ
* Góp ý
* Khảo sát

---

## Mục tiêu

Danh sách trên là roadmap đề xuất nhằm mở rộng hệ thống Smart Dormitory theo định hướng **Student-Centric**, giúp ứng dụng Mobile trở thành cổng dịch vụ số toàn diện cho sinh viên nội trú, đồng thời cung cấp cơ sở để nhóm Backend thiết kế API, Database và Business Workflow trong các giai đoạn phát triển tiếp theo.
