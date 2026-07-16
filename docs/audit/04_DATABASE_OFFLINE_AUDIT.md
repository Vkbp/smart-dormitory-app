# Database & Offline Architecture Audit - SDMS Android

**Date**: July 14, 2026
**Version**: 1.1.0
**Status**: COMPLETED
**Auditor**: Senior Technical Lead AI

---

## Executive Summary
The local database layer has been hardened with **SQLCipher** encryption, addressing a major security gap. However, the architectural issues regarding offline synchronization and database reactivity remain. The "Offline First" promise is still inconsistent, with critical write operations (Profile, Payments) lacking automated local fallback triggers. The lack of a formal migration strategy continues to pose a risk for production deployments.

**Overall Database Maturity Score: 75/100** (↑ 5 points for Encryption)

---

## Database Architecture (Room)

### 1. Entities & Schema
- **Current Entities**: `UserProfileEntity`, `InvoiceEntity`, `AccessLogEntity`, `PendingSyncEntity`.
- **Strength**: **SQLCipher Encryption Integrated.** [✔ Improved] Database is now encrypted using a passphrase-based `SupportFactory` in `DatabaseModule`.
- **Weakness**: **Missing versioning strategy.** [✔ Still Valid] The `AppDatabase` continues to use `fallbackToDestructiveMigration()`, which is unacceptable for production environments as it wipes user data on schema changes.

### 2. DAO Quality
- **Inconsistency**: Most DAOs return `Flow<T>`, which is good for reactive UI, but Repository implementations sometimes use `.first()` to bridge to suspension, losing reactivity.
- **Evidence**: `PaymentRepositoryImpl.getInvoices()` uses `invoiceDao.getAllInvoices().first()`.

---

## Offline & Sync Architecture

### 1. Pending Sync Mechanism [✔ Still Valid]
- **Implementation**: Uses `PendingSyncEntity` to store actions when offline.
- **Actions Covered**: `VERIFY_PAYMENT`, `UPDATE_PROFILE`, `REGISTER_FACE`.
- **Critical Violation**: **Inconsistent Triggering.** `UpdateProfileUseCase.kt` and `VerifyPaymentUseCase.kt` still lack local fallback logic, meaning changes made while offline will be lost.

### 2. SyncWorker (WorkManager)
- **Execution**: Background sync is performed via `SyncWorker` using a `Mutex` to prevent concurrent sync operations.
- **Retry Logic**: Basic retry (max 5 attempts) before marking as `FAILED`.
- **Vulnerability**: **File Dependency in Sync.** For `REGISTER_FACE`, the worker depends on the local `imagePath`. If the user deletes the photo before sync occurs, the sync will fail permanently.

### 3. Conflict Resolution
- **Status**: **NON-EXISTENT.** The application uses a "Last Write Wins" strategy for local cache and does not handle version conflicts between local changes and server updates during sync.

---

## 🏗️ Layer-by-Layer Verification

### Entity -> DAO
- ✅ Correct Room annotations.
- ✅ Threading: `suspend` functions for writes, `Flow` for reads.

### Repository -> Sync
- ❌ **Broken Chain**: Many repositories fail to queue actions to `PendingSyncDao` automatically. The logic is currently scattered in some UseCases but missing in others.

### SyncWorker -> Backend
- ✅ Correct DTO reconstruction from JSON payloads.
- ✅ Correct handling of multipart (Face) in background.

---

## Risk Analysis

| Risk | Impact | Priority | Evidence |
| :--- | :--- | :--- | :--- |
| **Silent Data Loss** | High | **P0** | `UpdateProfileUseCase` lacks offline fallback. |
| **Sync Failure (Missing Files)** | Medium | **P1** | `SyncWorker` depends on transient local file paths. |
| **Stale Data** | Medium | **P2** | No TTL (Time-To-Live) on local caches. |
| **Database Corruption** | High | **P1** | No explicit `Migration` strategy documented. |

---

## Improvement Plan
1.  **Centralize Sync Logic**: Move `PendingSync` insertion into a `BaseRepository` helper or dedicated `OfflineManager` so it's not forgotten in new features.
2.  **Complete UseCase Coverage**: Update all "Write" UseCases (Payment, Profile, Room Transfer) to include `PendingSync` fallback.
3.  **Implement Migrations**: Define explicit `Migration` objects for `AppDatabase` to handle production schema updates safely.
4.  **Resource Persistence**: For sync actions involving files (Face Registration), move the file to a "Sync Internal Folder" to prevent deletion by the user before completion.
5.  **Reactivity Fix**: Update Repositories to expose `Flow` from DAOs all the way to the Presentation layer instead of using `.first()`.

---

## Conclusion
While the addition of SQLCipher is a significant security improvement, the offline sync reliability and database migration strategy are the primary technical debts. Centralizing sync triggers at the repository layer should be the next major architectural refactor.

---

## Audit History

| Date | Summary | Files Changed | Issues Fixed | New Issues | Score |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 2026-07-11 | Initial Database Audit | All | - | 4 | 70 |
| 2026-07-14 | Update Audit: Confirmed SQLCipher | DatabaseModule | SEC-03 | None | 75 |
