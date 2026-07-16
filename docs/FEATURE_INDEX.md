# Feature Index - SDMS Android

Bản đồ tính năng của dự án Android.

| Feature | Description | Role | Presentation Pkg | Domain Pkg | Data Pkg |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Auth** | Đăng nhập, Quên MK, Kích hoạt | ALL | `features.auth` | `domain.auth` | `data.auth` |
| **Face** | Đăng ký & Quản lý khuôn mặt | STUDENT | `features.student.face` | `domain.face` | `data.face` |
| **Payment** | Hóa đơn & Lịch sử thanh toán | STUDENT | `features.student.payment` | `domain.payment` | `data.payment` |
| **Room** | Xem thông tin chỗ ở & Đổi phòng | STUDENT | `features.student.room` | `domain.room` | `data.room` |
| **Access** | Lịch sử ra vào & Giới nghiêm | STUDENT | `features.student.access` | `domain.access` | `data.access` |
| **Checkout** | Yêu cầu trả phòng sớm | STUDENT | `features.student.checkout` | `domain.checkout` | `data.checkout` |
| **Extension** | Gia hạn thời gian ở | STUDENT | `features.student.extension` | `domain.extension` | `data.extension` |
| **Profile** | Cập nhật thông tin cá nhân | STUDENT | `features.student.profile` | `domain.profile` | `data.profile` |
| **Notify** | Xem & quản lý thông báo, Báo hỏng | ALL | `features.student.notification`, `features.student.issue` | `domain.notification` | `data.notification` |
| **Dashboard** | Trung tâm điều khiển Admin | ADMIN | `features.admin.dashboard` | - | - |
| **SmartAccess**| Mở cửa & Khẩn cấp | ADMIN | `features.admin.smartaccess` | `domain.admin` | `data.admin` |
| **FaceAudit** | Duyệt khuôn mặt sinh viên | ADMIN | `features.admin.face` | `domain.admin` | `data.admin` |
| **CheckIn** | Thủ tục nhận phòng | ADMIN | `features.admin.checkin` | `domain.admin` | `data.admin` |
| **Broadcast** | Gửi thông báo toàn hệ thống | ADMIN | `features.admin.notification`| `domain.admin` | `data.admin` |

---
*Mọi feature đều tuân thủ kiến trúc Clean Architecture.*
