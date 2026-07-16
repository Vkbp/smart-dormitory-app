# API Index - SDMS Android

Ánh xạ giữa Tính năng, API Interface và UseCase.

| Feature | API Interface | Repository | Primary UseCases |
| :--- | :--- | :--- | :--- |
| **Auth** | `AuthApiService` | `AuthRepository` | `LoginUseCase`, `RefreshTokenUseCase` |
| **Face** | `FaceApiService` | `FaceRepository` | `RegisterFaceUseCase`, `GetFaceProfileUseCase` |
| **Payment** | `PaymentApiService` | `PaymentRepository` | `GetInvoicesUseCase`, `VerifyPaymentUseCase` |
| **Room** | `RoomApiService` | `RoomRepository` | `GetRoomInfoUseCase`, `SubmitTransferRequestUseCase`, `GetTransferHistoryUseCase` |
| **Access** | `AccessApiService` | `AccessRepository` | `GetAccessHistoryUseCase`, `SubmitCurfewRequestUseCase` |
| **Checkout** | `CheckoutApiService` | `CheckoutRepository` | `SubmitCheckoutRequestUseCase` |
| **Extension**| `ExtensionApiService` | `ExtensionRepository` | `RequestExtensionUseCase` |
| **Profile** | `ProfileApiService` | `ProfileRepository` | `UpdateProfileUseCase`, `UploadAvatarUseCase` |
| **Notify** | `NotificationApiService`| `NotificationRepository`| `GetNotificationsUseCase`, `ReportIssueUseCase` |
| **Admin** | `AdminApiService` | `AdminRepository` | `ApproveFaceUseCase`, `ConfirmCheckInUseCase` |

---
*Tất cả API được định nghĩa trong package `data.[feature].remote`.*
