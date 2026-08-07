# API Index - SDMS Android

Ánh xạ giữa Tính năng, API Interface và UseCase.

| Feature | API Interface | Repository | Primary UseCases |
| :--- | :--- | :--- | :--- |
| **Auth** | `AuthApiService` | `AuthRepository` | `LoginUseCase`, `RefreshTokenUseCase`, `ForgotPasswordUseCase`, `ResetPasswordUseCase` |
| **Face** | `FaceApiService` | `FaceRepository` | `RegisterFaceUseCase`, `GetFaceProfileUseCase` |
| **Payment** | `PaymentApiService` | `PaymentRepository` | `GetInvoicesUseCase`, `VerifyPaymentUseCase`, `GetPaymentHistoryPagingUseCase`, `GetUnpaidInvoicesUseCase` |
| **Room** | `RoomApiService` | `RoomRepository` | `GetRoomInfoUseCase`, `GetGroupedAvailableRoomsUseCase`, `SubmitTransferRequestUseCase` |
| **Access** | `AccessApiService` | `AccessRepository` | `GetUnifiedAccessHistoryUseCase`, `SubmitCurfewRequestUseCase` |
| **Checkout** | `CheckoutApiService` | `CheckoutRepository` | `SubmitCheckoutRequestUseCase`, `GetCheckoutHistoryUseCase` |
| **Extension**| `ExtensionApiService` | `ExtensionRepository` | `RequestExtensionUseCase` |
| **Profile** | `ProfileApiService` | `ProfileRepository` | `UpdateProfileUseCase`, `UploadAvatarUseCase` |
| **Notify** | `NotificationApiService`| `NotificationRepository`| `GetNotificationsUseCase`, `ReportIssueUseCase` |
| **Admin** | `AdminApiService` | `AdminRepository` | `ApproveFaceUseCase`, `ConfirmCheckInUseCase`, `GetSmartAccessResourcesUseCase` |

---
*Tất cả API được định nghĩa trong package `data.[feature].remote`.*
