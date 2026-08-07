# SDMS Android - Kế hoạch Tối ưu hóa Giao diện và Cấu trúc Hệ thống
**Phiên bản:** 1.0.0
**Ngày lập:** 2024-05-20
**Tình trạng:** Partially Completed (Architecture Optimized, UI Reverted to Original)

## 1. Mục tiêu (Objectives)
Tối ưu hóa mã nguồn để chuẩn bị cho giai đoạn hoàn thiện luận văn, đảm bảo tính chuyên nghiệp, dễ bảo trì và mở rộng. Tập trung vào 2 trụ cột chính:
- **Cấu trúc (Architecture)**: Đồng bộ hóa Clean Architecture trên toàn bộ các tính năng, loại bỏ các thư mục rác và làm sạch `core` module.
- **Giao diện (UI/UX)**: Thống nhất ngôn ngữ thiết kế Material 3, xây dựng thư viện thành phần dùng chung (Shared Components) và chuẩn hóa cách xử lý trạng thái (State Handling).

---

## 2. Tối ưu hóa Cấu trúc Hệ thống (System Structure)

### 2.1. Chuẩn hóa Feature Package
Hiện tại cấu trúc giữa các feature (Student, Admin, Shared) đang có sự sai lệch. Cần áp dụng quy tắc 3 lớp (Data - Domain - Presentation) nghiêm ngặt cho mọi folder tính năng.

**Cấu trúc mẫu cho một Feature:**
```text
com.ktx.dormitory.features.[feature_name]
├── data
│   ├── dto (Request/Response)
│   ├── local (Dao/Entity - nếu có)
│   ├── mapper
│   ├── remote (ApiService)
│   └── repository (Implementation)
├── domain
│   ├── model
│   ├── repository (Interface)
│   └── usecase
└── presentation
    ├── [Feature]Contract.kt (MVI State/Event/Effect)
    ├── [Feature]Screen.kt
    ├── [Feature]ViewModel.kt
    └── components (Thành phần chỉ dùng riêng cho feature này)
```

### 2.2. Tái cấu trúc DI Module
- Thay vì `CommonFeatureModule` quá lớn, chia nhỏ thành các `FeatureModule` riêng biệt (ví dụ: `RoomModule`, `ProfileModule`, `NotificationModule`).
- Đặt các Module này vào đúng package của feature hoặc một sub-package `di` bên trong feature nếu cần thiết để đảm bảo tính đóng gói.

### 2.3. Dọn dẹp thư mục `core` và `shared`
- **Core cleanup**: Xóa các package trống hoặc không cần thiết như `core/exception`, `core/extension` (nếu chưa có code).
- **Shared consolidation**: Đảm bảo các feature trong `shared/` (Auth, Profile, Notification) tuân thủ đúng cấu trúc chuẩn như các feature khác.

---

## 3. Tối ưu hóa Giao diện (UI/UX)

### 3.1. Xây dựng Design System (Atom/Molecule level)
Chuyển các thành phần UI dùng chung từ việc khai báo rải rác sang `ui/components`:
- **Atoms**: `SdmsButton`, `SdmsTextField`, `SdmsCard`.
- **Molecules**: `SdmsTopAppBar`, `SdmsBottomBar`, `SdmsDialog`.
- **Layouts**: Chuẩn hóa `Scaffold` template cho Admin và Student.

### 3.2. Chuẩn hóa Xử lý Trạng thái (State Management)
- Sử dụng `BaseViewModel` cho tất cả các màn hình để thống nhất MVI pattern.
- Đảm bảo 100% màn hình sử dụng `collectAsStateWithLifecycle` để tối ưu tài nguyên.
- Thống nhất việc sử dụng `LoadingView`, `EmptyView`, và `ErrorView` từ `CommonStates.kt`.

### 3.3. Material 3 Audit
- Kiểm tra lại bảng màu (Theme.kt) để đảm bảo độ tương phản (Accessibility).
- Sử dụng `MaterialTheme.colorScheme` và `MaterialTheme.typography` thay vì hardcode giá trị.

---

## 4. Lộ trình thực hiện (Roadmap)

### Giai đoạn 1: Dọn dẹp & Chuẩn hóa Core (Tuần 1)
1. Xóa các file/folder rác được liệt kê trong `structure.txt`.
2. Di chuyển các Model lạc lối về đúng Domain của feature tương ứng.
3. Refactor `CommonFeatureModule` thành các module nhỏ hơn.

### Giai đoạn 2: Tái cấu trúc Feature (Tuần 2)
1. Thực hiện "di cư" code cho từng feature: `access`, `payment`, `room`, `maintenance`.
2. Kiểm tra và sửa đổi imports sau khi di chuyển.
3. Đảm bảo mọi Repository đều có Interface trong Domain và Implementation trong Data.

### Giai đoạn 3: Unify UI (Tuần 3)
1. Review tất cả các file `*Screen.kt`.
2. Thay thế các component tự định nghĩa bằng Shared Components.
3. Áp dụng `ErrorView` có nút "Thử lại" cho tất cả các màn hình load dữ liệu từ API.

### Giai đoạn 4: Kiểm thử & Đóng gói (Tuần 4)
1. Chạy lại toàn bộ Unit Test và UI Test.
2. Cập nhật `structure.txt` mới.
3. Hoàn thiện tài liệu kiến trúc phục vụ viết luận văn.

---

## 5. Danh sách các folder/file cần xử lý ngay
- `app/src/main/java/com/ktx/dormitory/di/feature/CommonFeatureModule.kt` (Phân rã)
- `app/src/main/java/com/ktx/dormitory/di/feature/RequestModule.kt` (Đổi tên & Phân rã)
- `app/src/main/java/com/ktx/dormitory/domain/admin` (Di chuyển vào `admin` feature)
- `app/src/main/java/com/ktx/dormitory/core/exception` (Xóa nếu trống)
- `app/src/main/java/com/ktx/dormitory/core/extension` (Xóa nếu trống)

---
**Người lập kế hoạch:** AI Agent SDMS
**Chữ ký xác nhận:** [SDMS-OPTIMIZE-2024]
