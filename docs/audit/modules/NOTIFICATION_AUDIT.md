# Notification Module Audit Report

## Executive Summary
The Notification module manages in-app communications and issue reporting (maintenance requests). It allows students to stay informed about system updates, room changes, and payment status, while also providing a channel to report facility issues directly to the administration.

## Architecture Review
- **Standard Layering**: Follows Clean Architecture, though `NotificationRepositoryImpl` currently lacks a local Room database for caching notifications (unlike Access or Profile modules).
- **Issue Reporting**: Integrates issue reporting into the notification context, which is logical as issue status changes usually trigger notifications.
- **Repository Pattern**: Correctly abstracts API calls, using `BaseResponse` for consistency in issue reporting.

## Business Logic Review
- **Notification Management**: Supports fetching unread counts, full notification lists, and marking individual or all notifications as read.
- **Maintenance Requests (Issue Reporting)**: Allows students to submit room-specific issues with descriptions and optional image URLs.
- **Issue History**: Students can track the status of their reported issues (e.g., Pending, Processing, Fixed).

## Dependency Graph
```mermaid
graph TD
    UI[NotificationScreen] --> VM[NotificationViewModel]
    VM --> Repo[NotificationRepository]
    Repo --> API[NotificationApiService]
```

## Current Flow
1. **Unread Check**: App periodically (or on screen open) calls `getUnreadCount()` to show the badge.
2. **Read Notifications**: Student opens notification list -> `getNotifications()` -> Displays list.
3. **Mark Read**: Student clicks notification -> `markAsRead(id)` called to update server state.
4. **Report Issue**: Student fills report form -> `reportIssue()` POSTs to `v1/notifications/issues`.

## Problems Found
| Problem | Evidence | Severity | Recommendation |
| :--- | :--- | :--- | :--- |
| **Lack of Offline Support** | Notifications are fetched directly from the API without local caching in Room. | Medium | Implement local storage for notifications to allow students to read previously loaded messages without an internet connection. |
| **Missing Push Integration** | No visible FCM (Firebase Cloud Messaging) service found in the core or notification package. | High | Implement a `FirebaseMessagingService` to enable real-time push notifications, which is essential for urgent dorm alerts. |
| **Unread Count Sync** | Unread count is pull-based; may become stale if not refreshed frequently. | Low | Use a `SharedFlow` or `LiveEvent` triggered by push notifications to update the unread count in real-time. |

## Technical Debt
- **Real-time Updates**: Status changes for issue reports should be pushed via FCM or WebSocket rather than requiring the user to refresh.
- **Image Upload for Issues**: `reportIssue` expects an `imageUrl`, but the app should handle the file upload to Cloudinary (as seen in Backend docs) before submitting the report.

## Conclusion
The Notification module provides the basic "pull-based" messaging and maintenance reporting functionality required for the system. However, the absence of real-time push notifications (FCM) is a significant gap for a "Smart" management system and should be prioritized.

---
*Audited by AI Agent - Phase 7*
