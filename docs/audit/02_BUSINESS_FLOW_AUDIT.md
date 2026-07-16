# Business Flow Audit - SDMS Android

**Date**: July 14, 2026
**Version**: 1.1.0
**Status**: COMPLETED
**Auditor**: Senior Technical Lead AI

---

## Executive Summary
The Business Logic implementation remains the project's core strength. The recent integration of RFID assignment for students during the Check-in process has significantly improved the Admin workflow. Critical business rules (BR-R02, BR-I01) are consistently enforced. However, gaps in "Active QR" fallback for access control and "Issue History" for students still exist, preventing full production readiness.

**Overall Business Maturity Score: 95/100** (↑ 2 points)

---

## 🟢 Student Modules

### 1. Login & Security
- **Flow**: Splash -> Session Check -> Biometric (if enabled) -> Home.
- **Compliance**: BR-A02 (Password complexity) enforced.
- **Strengths**: Biometric production flow is well-implemented (Success -> Trigger Refresh Token).
- **Missing Steps**: Auto-logout on token expiration (requires Interceptor feedback to UI).

### 2. Profile
- **Flow**: View Profile -> Edit -> Upload Avatar -> Sync.
- **Compliance**: BR-U01 (MIME type check) handled in `ImageUtil`.
- **Backend Compatibility**: Uses Multipart for avatar upload.

### 3. Room & Issue Report
- **Flow**: View Room Info -> Trigger "Báo hỏng" (Issue Report) via BottomSheet -> **View Issue History**. [✔ Improved]
- **Compliance**: Direct link between User room and Report payload.
- **Strengths**: Students can now track the status of their reported issues via the "Lịch sử báo hỏng" screen. [✔ Fixed]

### 4. Payment
- **Flow**: View Invoices -> Instruction -> Manual Verification Request.
- **Compliance**: BR-S04 (Idempotency) used for verification requests.
- **Backend Compatibility**: High. Correctly maps `UNPAID` and `OVERDUE` statuses.

### 5. Face (AI)
- **Flow**: Check Status -> Liveness (4 steps) -> Server Embedding -> Approval.
- **Compliance**: BR-I01 enforced (UI hidden if status is PENDING/APPROVED).
- **Strengths**: Verified MVI contract and analysis listener separation. [✔ Fixed]

### 6. Checkout (Early Exit)
- **Flow**: Check Debts -> Submit Request -> Admin Review.
- **Compliance**: **BR-R02 STRICTLY ENFORCED**. UI blocks submission if `hasUnpaidBills` is true.
- **Strengths**: Integrated debt checking before submission. Quick access link added to Room Screen. [✔ Improved]

### 7. Extension
- **Flow**: Check Period -> Check Eligibility -> Submit.
- **Compliance**: Correctly checks "Đợt gia hạn" (Extension Period) status.

---

## 🔵 Admin Modules

### 1. Dashboard
- **Implementation**: Grid-based navigation to sub-tools.
- **Missing Features**: "System Analytics" cards are currently placeholders with static data. [✔ Still Valid]

### 2. Smart Access
- **Flow**: Select Door -> Remote Unlock / Emergency Override.
- **Backend Compatibility**: Communicates with Backend IoT gateway. Fully documented in `docs/implementation/003_ADMIN_SMART_ACCESS.md`. [✔ Improved]

### 3. Face Approval
- **Flow**: List Pending -> View Image -> Approve/Reject.
- **Compliance**: Correctly handles UUID-based identification.
- **Strengths**: Role isolation ensures only Admins can access this graph. [✔ Fixed]

### 4. Checkout & Extension Approval
- **Flow**: Review Student Request -> Decision (Approve/Reject) -> Notification.
- **Business Rule**: Admin is the final authority for BR-R02 overrides if necessary.

### 5. Check-in [✔ Improved]
- **Flow**: Search Student -> Confirm Room Assignment -> **Assign RFID Card**.
- **Evidence**: `CheckInViewModel` now implements `AssignRfidUseCase` allowing admins to link physical RFID cards to students during check-in.

---

## ❌ Critical Findings (Broken/Missing)

| Module | Issue | Status | Impact | Priority |
| :--- | :--- | :--- | :--- | :--- |
| **QR / Access** | Missing Active Verification | ✔ Still Valid | Student cannot trigger a QR code for manual scanner fallback. | **P1** |
| **Admin Dashboard** | Static Analytics | ✔ Still Valid | Placeholders mislead admins on system status. | **P2** |
| **Role Guard** | Session Persistence | ✔ Still Valid | Role transition without full session reset might leak UI states. | **P1** |
| **Face Registration**| No Reject History | ✔ Still Valid | Student only sees the latest rejection, not history. | **P2** |

---

## Business Rule Compliance Matrix

| Rule ID | Status | Evidence |
| :--- | :--- | :--- |
| **BR-R02** | ✅ Passed | `CheckoutViewModel.loadInitialData` checks `billsResult`. |
| **BR-I01** | ✅ Passed | `FaceRegistrationScreen` hides buttons based on `FaceProfileDto.status`. |
| **BR-S04** | ✅ Passed | `IdempotencyInterceptor` observed in network stack. |
| **BR-A02** | ✅ Passed | Validation logic in `LoginViewModel`. |

---

## Improvement Suggestions
1.  **Add Issue History**: Extend `IssueReportViewModel` to fetch and display previous reports.
2.  **Live QR Generator**: Implement a TOTP-based QR code for Access Control (Fallback).
3.  **Real Admin Analytics**: Connect `AdminDashboardScreen` to a real `AnalyticsUseCase`.
4.  **Token Expiry Event**: Implement a global event bus or SharedFlow to handle 401 Unauthorized by navigating to Login.

---

## Conclusion
The business logic remains robust. The addition of RFID assignment is a positive step towards a complete operational flow. Priorities for the next phase should include implementing the "Issue History" and "Active QR" fallback to eliminate the remaining P1/P2 issues.

---

## Audit History

| Date | Summary | Files Changed | Issues Fixed | New Issues | Score |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 2026-07-11 | Initial Business Audit | All | - | 4 | 92 |
| 2026-07-14 | Updated Audit: Added RFID Assignment | CheckIn | None | None | 93 |
| 2026-07-16 | Feature Completion: Issue History & Room UI | IssueHistory, RoomScreen | Issue History | None | 95 |
