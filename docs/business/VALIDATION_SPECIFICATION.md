# Client-Side Validation Specification (Comprehensive)

This document details all validation constraints and patterns enforced by the Android application to maintain parity with Backend requirements (SSOT).

## 1. Password Complexity (BR-A02)
Used in: `ChangePasswordScreen`, `ActivationScreen`.

- **Regex**: `^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!])(?=\S+$).{8,50}$`
- **Constraints**:
    - Length: 8-50 characters.
    - At least 1 lowercase letter.
    - At least 1 uppercase letter.
    - At least 1 digit.
    - At least 1 special character (`@#$%^&+=!`).
    - No whitespace allowed.

## 2. PII & Identity Constraints (BR-R01)
Used in: `ProfileScreen`, `AdminCheckInScreen`.

| Field | Validation Rule | Error Message |
| :--- | :--- | :--- |
| **CCCD** | Exactly 12 digits | "CCCD phải bao gồm 12 chữ số" |
| **Phone** | Vietnamese format (0 + 9 digits) | "Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)" |
| **Email** | RFC 5322 compliant | "Email không đúng định dạng" |
| **RFID** | Non-blank, Hex/Decimal string | "Mã thẻ RFID không hợp lệ" |

## 3. AI Face Quality Thresholds
Used in: `FaceRegistrationScreen` via `FaceQualityManager`.

| Metric | Threshold | UI Feedback |
| :--- | :--- | :--- |
| **Face Size** | >= 25% of frame | "Vui lòng đưa mặt lại gần hơn" |
| **Brightness** | 40 (Min) to 230 (Max) | "Ánh sáng quá tối" / "Ánh sáng quá chói" |
| **Head Angle** | Euler Y/Z < 15 degrees | "Vui lòng nhìn thẳng vào camera" |
| **Eye Open** | Probability >= 0.4 | "Vui lòng mở mắt" |

## 4. Business Request Guards
Constraints that block submission before API calls.

| Context | Constraint | Implementation |
| :--- | :--- | :--- |
| **Room Transfer** | Reason length >= 10 chars | `RoomTransferViewModel` validation |
| **Checkout** | Bank account & Bank name mandatory | `CheckoutViewModel` validation |
| **Curfew (Other)** | Note is MANDATORY if reason is "Lý do khác" | `CurfewRequestScreen` button guard |
| **Stay Extension** | Check active period via API | `ExtensionViewModel.isLocked` |
| **IoT Access** | Unified matching window < 10s | `TimelineMapper.kt` |

## 5. Logical Guards (State-based)
Rules that depend on calculated system state.

- **Debt Check (BR-R02)**:
    - Calculation: `bills.any { it.status == UNPAID || it.status == OVERDUE }`
    - Action: Set `hasUnpaidBills = true`; display "Cần thanh toán nợ" dialog and block "Gửi yêu cầu trả phòng".
- **Face Limit (BR-I01)**:
    - Calculation: `faceProfile.status == PENDING || APPROVED`
    - Action: Hide "Đăng ký mới" button; show current status.

---
*Reference: [BUSINESS_INDEX.md](../BUSINESS_INDEX.md)*
