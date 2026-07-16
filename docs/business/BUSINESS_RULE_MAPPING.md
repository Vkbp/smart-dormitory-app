# Business Rule Mapping - Backend to Mobile

This document maps Backend Business Rules (SSOT) to their corresponding enforcement mechanisms in the Android application.

| Rule ID | Backend Logic Summary | Mobile Guard / UX Behavior | Implementation | Status |
| :--- | :--- | :--- | :--- | :--- |
| **BR-R02** | Block checkout if student has unpaid bills. | `CheckoutViewModel` checks `hasUnpaidBills` and disables the submit button. | [CheckoutViewModel.kt](../../app/src/main/java/com/ktx/dormitory/presentation/features/student/checkout/CheckoutViewModel.kt) | ✔ Verified |
| **BR-I01** | Only 1 pending or approved face profile allowed. | UI hides registration controls if status is `PENDING` or `APPROVED`. | [FaceRegistrationScreen.kt](../../app/src/main/java/com/ktx/dormitory/presentation/features/student/face/FaceRegistrationScreen.kt) | ✔ Verified |
| **BR-A02** | Activation password complexity. | Regex validation on the activation form before calling the API. | [LoginViewModel.kt](../../app/src/main/java/com/ktx/dormitory/presentation/features/auth/LoginViewModel.kt) | ✔ Verified |
| **BR-S01** | IDOR Protection via JWT. | Client does not send `studentId` in personal requests; extracts from JWT. | `AuthInterceptor.kt` / Repositories | ✔ Verified |
| **BR-S04** | Idempotency for IoT/Payment. | `IdempotencyInterceptor` adds unique event IDs to critical requests. | `IdempotencyInterceptor.kt` | ✔ Verified |
| **BR-U01** | Image-only upload for documents. | `ImageUtil` checks MIME types before processing uploads. | `ImageUtil.kt` | ✔ Verified |
| **BR-H06** | Extension only during active period. | `QuickExtendScreen` checks `isPeriodActive` before allowing submission. | `QuickExtendViewModel.kt` | ✔ In-Progress |
| **BR-S03** | Emergency Override. | Admin Dashboard allows remote unlock regardless of curfew/status. | [AdminDashboardScreen.kt](../../app/src/main/java/com/ktx/dormitory/presentation/features/admin/dashboard/AdminDashboardScreen.kt) | ✔ Verified |

---

> [!NOTE]
> **Enforcement Principle**: While the Mobile app provides these guards for a better UX, the Backend remains the ultimate authority and will reject any requests that bypass these client-side checks.
