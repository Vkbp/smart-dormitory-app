# Package Index - SDMS Android

Bản đồ cấu trúc package mới của dự án (Refactored for Role-based Isolation).

| Package | Purpose | Sub-Packages / Features |
| :--- | :--- | :--- |
| `com.ktx.dormitory.core` | Hệ thống nền tảng (Foundation). | `network`, `security`, `sync`, `base`, `util` |
| `com.ktx.dormitory.shared` | Tính năng dùng chung cho mọi Role. | `auth`, `profile`, `notification` (Layers: `data`, `domain`, `presentation`) |
| `com.ktx.dormitory.student` | Nghiệp vụ dành riêng cho Sinh viên. | `face`, `checkout`, `payment`, `room`, `access`, `extension` (Layers: `data`, `domain`, `presentation`) |
| `com.ktx.dormitory.admin` | Nghiệp vụ dành riêng cho Quản trị viên. | `smartaccess`, `faceaudit`, `checkin`, `broadcast` (Layers: `data`, `domain`, `presentation`) |
| `com.ktx.dormitory.ui` | Linh kiện và tài nguyên giao diện. | `components`, `theme` |
| `com.ktx.dormitory.di` | Cấu hình Dependency Injection (Hilt). | `network`, `database`, `feature` |
| `com.ktx.dormitory.navigation` | Điều hướng và Role Guard. | - |
| `com.ktx.dormitory.ai` | Xử lý AI cục bộ (ML Kit). | `core`, `processing` |

---
### 🛠️ Nguyên tắc phân lớp bên trong Feature:
Mỗi folder bên dưới `shared`, `student`, `admin` sẽ được chia thành 3 lớp Clean Architecture:
1.  **data**: DTO, Entity, Mapper, Repository Impl.
2.  **domain**: Model, Repository Interface, UseCase.
3.  **presentation**: Composable Screens, ViewModels, Contracts.
