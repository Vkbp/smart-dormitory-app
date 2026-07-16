# Offline-First & Sync Audit Report - SDMS Android

**Date**: July 16, 2026
**Version**: 1.2.0
**Status**: COMPLETED
**Auditor**: AI System Auditor

---

## Executive Summary
The SDMS Android application follows an **Offline-First** strategy for most student-facing data modules. It utilizes Room for local persistence and `Flow` for reactive UI updates. While data *retrieval* is well-supported for offline viewing, the system lacks a robust **background synchronization** and **write-ahead logging** mechanism for data *mutations* (e.g., submitting requests while offline).

**Overall Offline-First Score: 78/100**

---

## Feature-Specific Offline Matrix

| Feature | Offline Viewing | Offline Mutation | Strategy | Status |
| :--- | :--- | :--- | :--- | :--- |
| **Profile** | ✅ Yes | ❌ No | Fetch & Cache | ✅ Verified |
| **Access Logs** | ✅ Yes | N/A | Fetch & Cache | ✅ Verified |
| **Invoices** | ✅ Yes | N/A | Fetch & Cache | ✅ Verified |
| **Curfew Request** | ✅ History only | ❌ No | Cache on Success | ⚠️ Partial |
| **Room Info** | ❌ No | ❌ No | Direct API | ❌ Missing |
| **Notifications** | ❌ No | ❌ No | Direct API | ❌ Missing |

---

## 🔍 Detailed Findings

### 1. Retrieval Strategy (Fetch & Cache)
- **Implementation**: Repositories like `AccessRepositoryImpl` and `PaymentRepositoryImpl` fetch data from the API and immediately insert it into Room.
- **Reactive UI**: The UI observes `Flow` from Room DAOs (e.g., `AccessLogDao.getAllLogs()`), ensuring that the view updates automatically whenever the database is populated.
- **Evidence**: `override val accessLogs: Flow<List<AccessLog>> = logDao.getAllLogs().map { ... }` in `AccessRepositoryImpl.kt`.

### 2. Missing Background Synchronization (WorkManager)
- **Issue**: There is no evidence of `WorkManager` or `SyncWorker` implementation for background data synchronization.
- **Risk**: Data only syncs when the user manually opens the screen or triggers a refresh. Urgent notifications or status changes (e.g., Curfew Request approved) will not be visible until the next manual interaction.
- **Affected Modules**: All.

### 3. "Online-Only" Write Operations
- **Issue**: Mutations such as `submitCurfewRequest` or `verifyPayment` fail immediately if the device is offline.
- **Evidence**: `AccessRepositoryImpl#submitCurfewRequest` and `PaymentRepositoryImpl#verifyPayment` call the remote data source directly without a fallback to a "Pending Sync" table.
- **Risk**: Poor UX in low-connectivity areas (e.g., dormitory elevators or basements).

### 4. Conflict Resolution
- **Strategy**: Strictly uses `OnConflictStrategy.REPLACE`.
- **Finding**: This is sufficient for simple "Fetch & Cache" modules where the server is the single source of truth for history (Access, Payments).
- **Risk**: Low, but lacks versioning for complex entities like Profile.

### 5. Connectivity Monitoring
- **Implementation**: A robust `NetworkMonitor` utility exists using `ConnectivityManager.NetworkCallback` and exposing an `isOnline: Flow<Boolean>`.
- **Usage**: Underutilized in repositories. Repositories rely on `try-catch` blocks rather than preemptively checking connectivity or observing the monitor.

---

## ⚠️ Critical Gaps

### Lack of Offline Room Information
- **Observation**: Unlike Profile or Payments, the **Room Module** has no local storage.
- **Impact**: Students cannot view their room number, building, or bed info without an internet connection.

### No Write-Ahead Logging (WAL) for Requests
- **Observation**: Requests are only saved to the local DB *after* a successful API response.
- **Impact**: If a request is submitted and the network drops during the call, the request is lost, and the user must re-enter the data.

---

## Recommendations

1. **Implement SyncWorker**: Introduce `WorkManager` to periodically sync Access Logs, Invoices, and Profile data in the background.
2. **Add "Pending Request" Table**: For Curfew and Room Transfer requests, save the request to a local table first, then attempt upload. Use `WorkManager` with `Constraints(networkType = CONNECTED)` to retry automatically.
3. **Local Cache for Room Module**: Add `RoomDao` and `RoomEntity` to cache the student's current residency information.
4. **Preemptive Connectivity Check**: Use `NetworkMonitor` in ViewModels to disable "Submit" buttons when offline, providing a clear "No Connection" state to the user.

## Conclusion
The application successfully delivers a "read-only" offline experience for historical data. To achieve "Smart" system maturity, the implementation must evolve to support background synchronization and offline-to-online request queuing.

---
*Audited by AI Agent - Step 4 Complete*
