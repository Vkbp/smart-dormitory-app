# Implementation Plan - Admin Notification Broadcast UI/UX Overhaul

Redesign the Notification Broadcast module for a professional, "System Administrator" experience with clear composition, live previews, and safety checks.

## User Review Required

> [!IMPORTANT]
> - **Composition UI**: I will transition from a simple list of fields to a "Composer" layout with specialized cards for writing and targeting.
> - **Real-time Preview**: I will add a "Live Preview" section so admins can see exactly how the notification will appear on a user's device.
> - **Confirmation Flow**: The "Broadcast" action will trigger a confirmation dialog to prevent accidental system-wide alerts.

## Proposed Changes

### [Admin Notification Presentation]

#### [NotificationBroadcastScreen.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/admin/notification/presentation/NotificationBroadcastScreen.kt)
- **New Composer Layout**:
    - **Header**: Introduction and system capacity status.
    - **Editor Section**: Styled `OutlinedTextField`s with character counters and clear labels.
    - **Audience Selection**: Improved `FilterChip` UI with icons representing different user roles (Students, Staff, etc.).
    - **Live Preview Card**: A dedicated section that mimics the mobile notification appearance.
- **Safety Features**:
    - **Confirmation Dialog**: Detailed summary of the message and recipient count before final transmission.
    - **Status Animations**: Better visual feedback during the broadcast process.

---

### [Admin Smart Access History]
- (Completed) Implementation of `AdminAccessHistoryScreen` and related components.

---

### [Admin Smart Access UI/UX]
- (Completed) Redesign of dashboard and interaction dialogs.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to verify compilation.

### Manual Verification
1. **Composer**: Verify the new UI layout is responsive and organized.
2. **Character Count**: Check if character limits/counters provide helpful feedback.
3. **Live Preview**: Verify that the preview updates instantly as the admin types.
4. **Broadcast Flow**: Verify the confirmation dialog correctly summarizes the action and triggering the broadcast shows clear success/error states.
