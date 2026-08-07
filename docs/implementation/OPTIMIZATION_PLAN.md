# 📋 Kế Hoạch Tối Ưu Giao Diện & Cấu Trúc Hệ Thống

> **Ngày lập:** 2026-08-06  
> **Dự án:** Smart Dormitory Android App  
> **Mục tiêu:** Dọn dẹp cấu trúc folder dư thừa + tối ưu giao diện UI

---

## 🔍 PHÂN TÍCH HIỆN TRẠNG

### Cấu trúc Package gốc (`com.ktx.dormitory`)

```
com.ktx.dormitory/
├── admin/              ✅ Đúng chỗ (các tính năng Admin)
├── ai/                 ✅ Đúng chỗ (Face AI processing)
├── core/               ✅ Đúng chỗ (utilities, network, base)
├── data/               ⚠️  TRÙNG LẮP với shared/core (chỉ còn 3 file)
├── di/                 ✅ Đúng chỗ (Hilt DI modules)
├── domain/             ⚠️  TRÙNG LẮP với shared/core (chỉ còn 2 file)
├── navigation/         ✅ Đúng chỗ (navigation graphs, Screen.kt)
├── shared/             ✅ Đúng chỗ (auth, notification, profile)
├── student/            ✅ Đúng chỗ (các tính năng Student)
└── ui/                 ✅ Đúng chỗ (theme, components)
```

### Màn hình hiện có (từ Screen.kt)

| Route | Màn hình | Role | Trạng thái |
|-------|----------|------|------------|
| splash | SplashScreen | Both | ✅ |
| login | LoginScreen | Both | ✅ |
| student_home | HomeScreen | Student | ✅ |
| access_history | AccessHistoryScreen | Student | ✅ |
| access_detail/{id} | AccessDetailScreen | Student | ✅ |
| curfew_request | CurfewRequestScreen | Student | ✅ |
| curfew_detail/{id} | CurfewDetailScreen | Student | ✅ |
| create_curfew_request | CreateCurfewRequestScreen | Student | ✅ |
| face_registration | FaceRegistrationScreen | Student | ✅ |
| face_status | FaceStatusScreen | Student | ✅ |
| face_verification_history | FaceVerificationHistoryScreen | Student | ✅ |
| face_verification_detail/{id} | FaceVerificationDetailScreen | Student | ✅ |
| profile | ProfileScreen | Student | ✅ (shared) |
| payment | PaymentScreen | Student | ✅ |
| payment_history | PaymentHistoryScreen | Student | ✅ |
| payment_instruction | PaymentInstructionScreen | Student | ✅ |
| room_info | RoomScreen | Student | ✅ |
| room_utilities | RoomUtilitiesScreen | Student | ✅ |
| room_transfer | RoomTransferScreen | Student | ✅ |
| room_transfer_detail/{id} | RoomTransferDetailScreen | Student | ✅ |
| maintenance | MaintenanceScreen | Student | ✅ |
| maintenance_detail/{id} | MaintenanceDetailScreen | Student | ✅ |
| create_maintenance | CreateMaintenanceScreen | Student | ✅ |
| quick_extend | QuickExtendScreen | Student | ✅ |
| checkout | CheckoutScreen | Student | ✅ |
| notifications | NotificationScreen | Both | ✅ (shared) |
| change_password | ChangePasswordScreen | Both | ✅ (shared) |
| forgot_password | ForgotPasswordScreen | Both | ✅ (shared) |
| admin_dashboard | AdminDashboardScreen | Admin | ✅ |
| admin_face_approval | FaceApprovalScreen | Admin | ✅ |
| admin_check_in | CheckInScreen | Admin | ✅ |
| admin_smart_access | SmartAccessScreen | Admin | ✅ |
| admin_notification_broadcast | NotificationBroadcastScreen | Admin | ✅ |
| admin_checkout_approval | CheckoutApprovalScreen | Admin | ✅ |
| admin_extension_approval | StayExtensionScreen | Admin | ✅ |
| admin_accounts | ❌ CHƯA CÓ SCREEN | Admin | ❌ Thiếu |
| admin_reports | ❌ CHƯA CÓ SCREEN | Admin | ❌ Thiếu |
| admin_settings | ❌ CHƯA CÓ SCREEN | Admin | ❌ Thiếu |

---

## 🚨 VẤN ĐỀ CẦN XỬ LÝ

### Vấn đề 1: Folder dư thừa / rỗng (PRIORITY HIGH)

#### A. data/ ở root level — có file nhưng sai vị trí
- data/common/local/PendingSyncDao.kt     → nên ở core/sync/
- data/common/local/PendingSyncEntity.kt  → nên ở core/sync/
- data/local/AppDatabase.kt               → cân nhắc giữ nguyên hoặc chuyển vào core/database/
- data/settings/repository/SettingsRepositoryImpl.kt → nên ở core/settings/repository/

#### B. domain/ ở root level — trùng lắp
- domain/admin/model/AdminModels.kt         → TRÙNG với admin/common/domain/model/AdminModels.kt (cần verify)
- domain/settings/repository/SettingsRepository.kt → thiếu implementation hoặc nên merge vào core/

#### C. Admin folders rỗng (tạo rồi để đó)
- admin/checkin/data/       RỖNG (0 file) → XÓA
- admin/checkin/domain/     RỖNG (0 file) → XÓA
- admin/dashboard/data/     RỖNG (0 file) → XÓA
- admin/dashboard/domain/   RỖNG (0 file) → XÓA
- admin/notification/data/  RỖNG (0 file) → XÓA
- admin/notification/domain/RỖNG (0 file) → XÓA
- admin/smartaccess/data/   RỖNG (0 file) → XÓA

#### D. DI folder rỗng
- di/security/ → RỖNG → XÓA
- di/worker/   → RỖNG → XÓA
- navigation/components/ → RỖNG → XÓA

#### E. 3 Admin screens khai báo trong Screen.kt nhưng chưa có file
- AdminAccounts → chưa có presentation folder
- AdminReports  → chưa có presentation folder
- AdminSettings → chưa có presentation folder

---

### Vấn đề 2: Giao diện UI cần tối ưu (PRIORITY MEDIUM)

#### A. Shared Components còn thiếu
```
ui/components/ hiện có:
- BottomNavBar.kt
- CommonStates.kt

Cần thêm:
- DormitoryTopBar.kt     (TopAppBar tái sử dụng)
- EmptyStateView.kt      (khi danh sách rỗng)
- ErrorRetryView.kt      (error + nút thử lại)
- LoadingScreen.kt       (loading toàn màn hình)
- ConfirmDialog.kt       (dialog xác nhận)
- StatusBadge.kt         (badge Pending/Approved/Rejected)
- InfoCard.kt            (card key-value thông tin)
- SectionHeader.kt       (header phân vùng)
```

#### B. HomeScreen cần cải thiện UX
Layout đề xuất:
```
TopBar: Smart Dormitory + [Refresh] [Bell🔔]
Card: Xin chào [Tên] | Phòng B201 | Hết hạn 31/8
Banner: ⚠️ Hóa đơn chưa thanh toán (nếu có)
Grid: Quick Actions [Ra/vào] [Thanh toán] [Bảo trì] [Khuôn mặt] [Đổi phòng]
List: Thông báo gần đây (3 item mới nhất)
```

#### C. AdminDashboardScreen cần build thực sự
Layout đề xuất:
```
TopBar: Hệ thống KTX
Row Stats: [Tổng SV] [Vào hôm nay] [Chờ duyệt]
List Pending: Khuôn mặt/Trả phòng/Gia hạn chờ duyệt
Grid Quick Actions: [Duyệt mặt] [Nhận phòng] [Điều khiển] [Thông báo]
```

#### D. Theme/Design System cần hoàn thiện
- Bổ sung semantic colors (success green, warning amber, error red)
- Dark theme implementation đầy đủ
- Shape system (xSmall → extraLarge)
- Typography scale đồng nhất

---

## 📋 KẾ HOẠCH THỰC HIỆN THEO BƯỚC

### GIAI ĐOẠN 1: Dọn dẹp cấu trúc (1 ngày)

BƯỚC 1.1 — Xóa folder rỗng
- Xóa admin/checkin/data/, admin/checkin/domain/
- Xóa admin/dashboard/data/, admin/dashboard/domain/
- Xóa admin/notification/data/, admin/notification/domain/
- Xóa admin/smartaccess/data/
- Xóa di/security/, di/worker/
- Xóa navigation/components/

BƯỚC 1.2 — Di chuyển file cấu trúc (cẩn thận import)
- Move PendingSyncDao.kt + PendingSyncEntity.kt → core/sync/
- Move SettingsRepository.kt + SettingsRepositoryImpl.kt → core/settings/repository/
- Verify và xóa domain/admin/model/AdminModels.kt nếu trùng
- Cập nhật DatabaseModule.kt và các DI module liên quan

BƯỚC 1.3 — Kiểm tra DI Modules
- Verify AdminModule.kt inject đủ use cases
- Xem xét rename RequestModule.kt → StudentAccessModule.kt

BƯỚC 1.4 — Thêm 3 Admin Screen placeholder
- Tạo admin/accounts/presentation/AdminAccountsScreen.kt
- Tạo admin/reports/presentation/AdminReportsScreen.kt
- Tạo admin/settings/presentation/AdminSettingsScreen.kt
- Đăng ký vào AdminNavGraph.kt

### GIAI ĐOẠN 2: Tối ưu UI Foundation (2-3 ngày)

BƯỚC 2.1 — Hoàn thiện Theme
- Bổ sung semantic colors vào Color.kt
- Hoàn thiện darkColorScheme trong Theme.kt
- Thêm Shape system

BƯỚC 2.2 — Tạo Shared Components
- DormitoryTopBar.kt
- EmptyStateView.kt
- ErrorRetryView.kt
- LoadingScreen.kt
- ConfirmDialog.kt
- StatusBadge.kt
- InfoCard.kt
- SectionHeader.kt

BƯỚC 2.3 — Refactor HomeScreen
- Thêm greeting card với thông tin sinh viên/phòng
- Thêm alert banner cho hóa đơn/thông báo quan trọng
- Cải thiện quick actions grid
- Thêm recent notifications section

BƯỚC 2.4 — Build AdminDashboardScreen thực sự
- Stats cards (tổng SV, vào hôm nay, chờ duyệt)
- Pending actions list
- Quick access grid

### GIAI ĐOẠN 3: Hoàn thiện Admin Features (3-5 ngày)

BƯỚC 3.1 — AdminAccountsScreen (full)
- Danh sách sinh viên với tìm kiếm + lọc
- Chi tiết sinh viên
- Kích hoạt/vô hiệu hóa tài khoản

BƯỚC 3.2 — AdminReportsScreen (full)
- Báo cáo ra/vào theo ngày/tháng
- Báo cáo thanh toán
- Charts/biểu đồ

BƯỚC 3.3 — AdminSettingsScreen (full)
- Cài đặt giờ giới nghiêm
- Thông số hệ thống

---

## ✅ CHECKLIST THỰC HIỆN

### Phase 1: Cấu trúc
- [ ] 1.1 Xóa 7 folder rỗng trong admin/
- [ ] 1.1 Xóa di/security/, di/worker/
- [ ] 1.1 Xóa navigation/components/
- [ ] 1.2 Di chuyển PendingSync files → core/sync/
- [ ] 1.2 Tạo core/settings/repository/ và move SettingsRepository files
- [ ] 1.2 Xác minh + xóa domain/admin/model/AdminModels.kt nếu trùng
- [ ] 1.3 Kiểm tra + đổi tên RequestModule.kt nếu cần
- [ ] 1.4 Tạo AdminAccountsScreen.kt (placeholder)
- [ ] 1.4 Tạo AdminReportsScreen.kt (placeholder)
- [ ] 1.4 Tạo AdminSettingsScreen.kt (placeholder)
- [ ] 1.4 Đăng ký 3 screen mới vào AdminNavGraph.kt

### Phase 2: UI Foundation
- [ ] 2.1 Bổ sung semantic colors vào Color.kt
- [ ] 2.1 Hoàn thiện Dark theme trong Theme.kt
- [ ] 2.1 Thêm Shape system vào Theme.kt
- [ ] 2.2 Tạo DormitoryTopBar.kt
- [ ] 2.2 Tạo EmptyStateView.kt
- [ ] 2.2 Tạo ErrorRetryView.kt
- [ ] 2.2 Tạo LoadingScreen.kt
- [ ] 2.2 Tạo ConfirmDialog.kt
- [ ] 2.2 Tạo StatusBadge.kt
- [ ] 2.2 Tạo InfoCard.kt
- [ ] 2.3 Refactor HomeScreen.kt theo layout mới
- [ ] 2.4 Refactor AdminDashboardScreen.kt theo layout mới

### Phase 3: Admin Features
- [ ] 3.1 Implement AdminAccountsScreen (full feature)
- [ ] 3.2 Implement AdminReportsScreen (full feature)
- [ ] 3.3 Implement AdminSettingsScreen (full feature)

---

## 📊 ƯỚC TÍNH THỜI GIAN

| Giai đoạn | Task | Thời gian |
|-----------|------|-----------|
| Phase 1 | Dọn dẹp cấu trúc | 1 ngày |
| Phase 2 | Design System + Shared Components | 2 ngày |
| Phase 3 | Refactor HomeScreen + AdminDashboard | 1 ngày |
| Phase 4 | Admin màn hình mới (full features) | 3 ngày |
| Tổng | | ~7 ngày |

---

## 🏗️ CẤU TRÚC MỤC TIÊU SAU KHI DỌN DẸP

```
com.ktx.dormitory/
├── admin/
│   ├── accounts/presentation/      NEW
│   ├── checkin/presentation/       KEEP
│   ├── checkout/presentation/      KEEP
│   ├── common/                     KEEP (shared data + domain)
│   ├── dashboard/presentation/     KEEP (refactor UI)
│   ├── extension/presentation/     KEEP
│   ├── face/presentation/          KEEP
│   ├── notification/presentation/  KEEP
│   ├── reports/presentation/       NEW
│   ├── settings/presentation/      NEW
│   └── smartaccess/
│       ├── domain/repository/      KEEP
│       └── presentation/           KEEP
├── ai/                             KEEP
├── core/
│   ├── base/                       KEEP
│   ├── common/                     KEEP
│   ├── datastore/                  KEEP
│   ├── dispatcher/                 KEEP
│   ├── mapper/                     KEEP
│   ├── network/                    KEEP
│   ├── receiver/                   KEEP
│   ├── security/                   KEEP
│   ├── settings/repository/        NEW (move từ domain/ và data/)
│   ├── sync/                       REFACTOR (move PendingSync files)
│   └── util/                       KEEP
├── data/
│   └── local/AppDatabase.kt        KEEP (xóa subfolder dư)
├── di/
│   ├── common/                     KEEP
│   ├── database/                   KEEP
│   ├── feature/                    KEEP (xem xét rename)
│   └── network/                    KEEP
├── navigation/                     KEEP (xóa components/ rỗng)
├── shared/
│   ├── auth/                       KEEP
│   ├── notification/               KEEP
│   └── profile/                    KEEP
├── student/
│   ├── access/                     KEEP
│   ├── checkout/                   KEEP
│   ├── extension/                  KEEP
│   ├── face/                       KEEP
│   ├── home/                       KEEP (refactor UI)
│   ├── maintenance/                KEEP
│   ├── payment/                    KEEP
│   └── room/                       KEEP
└── ui/
    ├── components/                  ADD 8 new components
    └── theme/                       ENHANCE dark mode + shapes
```

---

> QUAN TRỌNG:
> - Thứ tự ưu tiên: Phase 1 (cấu trúc) → Phase 2.2 (components) → Phase 2.3/2.4 (UI screens) → Phase 3 (admin features)
> - Khi di chuyển file: luôn cập nhật imports và DI modules, chạy build sau mỗi bước
> - Phase 3 (admin accounts/reports/settings) cần API backend tương ứng
