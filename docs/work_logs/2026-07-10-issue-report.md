# Implementation Report: Quick Issue Report

**Date:** 2026-07-10
**Task:** Implement "Quick Issue Report" via In-App Notification for Students.

## Changes

### 1. UI Layer
- Created `IssueReportBottomSheet.kt` in `presentation.features.student.issue`.
    - Uses `ModalBottomSheet` from Material 3.
    - Displays current Room Code (read-only).
    - Input for issue description.
    - Handles loading, success (toast + dismiss), and error states.
- Modified `HomeScreen.kt`:
    - Added "Báo hỏng" item to `StudentDashboard`.
    - Integrated `IssueReportBottomSheet` using state management.
    - Updated `DashboardItem` and `DashboardGrid` to support custom `onClick` actions.

### 2. Logic Layer (Existing)
- `IssueReportViewModel`: Pre-implemented, handles state and event logic.
- `ReportIssueUseCase`: Pre-implemented, calls `NotificationRepository`.
- `NotificationRepositoryImpl`: Pre-implemented, calls `NotificationApiService`.
- `NotificationApiService`: Pre-implemented, endpoint `POST /api/v1/notifications/issues`.

### 3. Documentation
- Updated `API_INDEX.md`: Added `ReportIssueUseCase` to `Notify` feature.
- Updated `FEATURE_INDEX.md`: Updated `Notify` feature description and packages.

## Verification
- Build successful.
- Manual verification of UI flow (Dashboard -> BottomSheet -> Submit) confirmed logic integration.
- Code follows MVI-lite pattern and project naming conventions.
