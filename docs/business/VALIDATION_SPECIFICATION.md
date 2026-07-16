# Client-Side Validation Specification

This document details the validation constraints and patterns enforced by the Android application to maintain parity with Backend requirements (SSOT).

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

## 2. PII Constraints (BR-R01)
Used in: `ProfileScreen`, `AdminCheckInScreen`.

| Field | Validation Rule | Error Message |
| :--- | :--- | :--- |
| **CCCD** | Exactly 12 digits | "CCCD phải bao gồm 12 chữ số" |
| **Phone** | Vietnamese phone format (10 digits) | "Số điện thoại không hợp lệ" |
| **Email** | RFC 5322 compliant format | "Email không đúng định dạng" |

## 3. File Upload Guards (BR-U01)
Used in: `FaceRegistrationScreen`, `ProfileViewModel`.

- **MIME Type Check**:
    - Allowed: `image/jpeg`, `image/png`.
    - Blocked: `application/pdf`, `text/*`, etc.
- **Size Limit**: 10MB (Local compression to 720p usually reduces this to <500KB before upload).

## 4. Logical Guards
Rules that depend on calculated state.

- **Debt Check (BR-R02)**:
    - Calculation: `bills.any { it.status == UNPAID || it.status == OVERDUE }`
    - Action: Set `hasUnpaidBills = true` in UI state; disable "Gửi yêu cầu trả phòng".

---
*Reference: [BUSINESS_INDEX.md](../BUSINESS_INDEX.md)*
