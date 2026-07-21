# Business Rule Mapping - Full Inventory

This document maps every identified Backend Business Rule (SSOT) to its corresponding enforcement mechanism in the Android application.

| Rule ID | Logic Category | Mobile Guard / UX Behavior | Implementation |
| :--- | :--- | :--- | :--- |
| **BR-A02** | Security | Password complexity regex on forms. | `ValidationUtils.kt` |
| **BR-S01** | Security | Identity protection (IDOR); studentId from JWT. | `AuthInterceptor.kt` |
| **SEC-04** | Security | Biometric binding via KeyStore (CryptoObject). | `SecurityUtils.kt` |
| **SEC-07** | Security | Device Integrity (Root/Emulator detection). | `IntegrityChecker.kt` |
| **BR-S04** | Security | Idempotency event IDs for IoT and Payments. | `IdempotencyInterceptor.kt` |
| **BR-R02** | Finance | Block checkout if student has unpaid/overdue bills. | `CheckoutViewModel.kt` |
| **BR-I01** | Face AI | Limit to 1 active face profile (Pending/Approved). | `FaceRegistrationScreen.kt` |
| **BR-H06** | Residency | Extension allowed only during official period. | `ExtensionViewModel.kt` |
| **BR-U01** | Storage | Image-only upload for student documents. | `ImageUtil.kt` |
| **BR-C01** | Admin | RFID card assignment during check-in. | `CheckInViewModel.kt` |
| **BR-A01** | Access | Curfew exception requests for late entry. | `CurfewRequestScreen.kt` |
| **BR-A03** | Access | Remote unlock permissions for Admin/Staff. | `SmartAccessViewModel.kt` |
| **BR-E01** | Access | Mandatory reason for Emergency Overrides. | `SmartAccessViewModel.kt` |
| **ARCH-04** | IoT | Unified Timeline merging logic (< 10s window). | `TimelineMapper.kt` |
| **PERF-04** | Scaling | Paging 3 integration for high-volume logs. | `PaymentHistoryPagingSource.kt` |

---

> [!NOTE]
> **Thesis Relevance**: These rules demonstrate the application's ability to handle complex distributed state and security requirements across multiple domains (IoT, Finance, Identity).
