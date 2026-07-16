# API Compatibility Audit - SDMS Android

**Date**: July 16, 2026
**Version**: 1.2.0
**Status**: COMPLETED
**Auditor**: AI System Auditor

---

## Executive Summary
The API layer of the SDMS Android application is currently in a **high-risk state** due to significant discrepancies in response wrapping and data type expectations between the client and the backend. While many features follow Clean Architecture, the integration layer suffers from fragmentation in execution styles and a critical failure to adhere to the "Envelope Pattern" in several key services.

**Overall API Compatibility Score: 72/100** (⚠️ Major Risks Identified)

---

## API Compatibility Matrix

| Feature | Retrofit Interface | DTO Status | Mapper Status | Sync Status |
| :--- | :--- | :--- | :--- | :--- |
| **Auth** | `AuthApiService` | ✅ Matches | ✅ Domain Models | ✅ Strict Wrapping |
| **Face** | `FaceApiService` | ⚠️ UUID mismatch | ✅ Domain Models | ❌ Raw Return |
| **Payment** | `PaymentApiService` | ✅ Matches | ✅ Local/Domain | ❌ Inconsistent Wrapping |
| **Access** | `AccessApiService` | ✅ Matches | ✅ Reactive Flow | ✅ Strict Wrapping |
| **Notify** | `NotificationApiService`| ❌ **Broken** | ✅ Partial Models | ❌ **No Wrapping** |
| **Admin** | `AdminApiService` | ✅ Matches | ⚠️ Direct DTO usage | ✅ Double Wrapping |

---

## ❌ Critical Discrepancies (P0/P1)

### 1. Broken Notification Endpoints (P0 - High Risk of Crash)
- **Issue**: `NotificationApiService` expects raw types (`Long`, `List<NotificationResponse>`) for `getUnreadCount` and `getNotifications`. However, the Backend `NotificationController` wraps these in `ApiResponse<T>`.
- **Evidence**: 
    - Android: `suspend fun getNotifications(): Response<List<NotificationResponse>>`
    - Backend: `public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUserNotifications()`
- **Impact**: **Immediate crash** or `JsonSyntaxException` when fetching notifications as the JSON parser encounters an object `{...}` instead of an array `[...]`.

### 2. Payment Instruction Wrapping Mismatch (P0 - High Risk)
- **Issue**: `PaymentApiService.getPaymentInstructions()` returns a raw `PaymentInstructionDto`. The backend wraps this in `ApiResponse`.
- **Evidence**: 
    - Android: `suspend fun getPaymentInstructions(): PaymentInstructionDto`
    - Backend: `public ResponseEntity<ApiResponse<PaymentInstructionResponse>> getPaymentInstructions()`
- **Impact**: High risk of crash during the payment flow initialization.

### 3. Execution Style Fragmentation (P1 - Maintainability)
- **Issue**: There is no standard for using Retrofit's `Response<T>` vs raw `T`.
- **Evidence**:
    - `AuthApiService` returns `BaseResponse<T>` directly.
    - `AdminApiService` returns `Response<BaseResponse<T>>`.
    - `NotificationApiService` returns `Response<T>` (raw).
- **Impact**: Fragmentation of error handling logic in Repositories (some use `response.isSuccessful`, others use `try-catch` on raw calls).

### 4. Manual Request Construction (P1 - Type Safety)
- **Issue**: `PaymentRemoteDataSourceImpl.verifyPayment` constructs a `HashMap<String, Any>` manually.
- **Evidence**: `api.verifyPayment(hashMapOf("billId" to billId, ...))`
- **Impact**: Loss of type safety. Inconsistency with backend's `OnlinePaymentRequest` which uses `BigDecimal` and `PaymentMethod` enum.

---

## 🏗️ Layer-by-Layer Verification

### Backend -> Retrofit
- **Discrepancy**: `registerFace` in backend returns `ResponseEntity<ApiResponse<UUID>>`, but Android `FaceApiService` returns `BaseResponse<UUID>`. This matches the wrapper but ignores the `Response` metadata standard used in other services.
- **Discrepancy**: Backend `CurfewRequest` endpoints (`v1/curfew-requests`) appear in Android docs and code but are **missing** from the provided backend source code (Potential roadmap desync).

### Retrofit -> DTO
- **Mismatch**: `FaceApiService.registerFace` returns `BaseResponse<UUID>`, but the `FaceRepositoryImpl` treats it as `Result<Unit>`, ignoring the returned ID.

### DTO -> Mapper
- **Strength**: `AccessMapper` and `PaymentMapper` are well-implemented with support for local entities.
- **Weakness**: Missing mapping for `Admin` features; many Admin ViewModels might be using DTOs directly (Technical debt).

### ViewModel -> Compose
- **Observation**: `FaceManagementViewModel` consumes `FaceProfileDto` and `VerificationAttemptDto` directly.
- **Impact**: UI layer is coupled to the API schema. Changes in backend DTOs require changes in UI code.

---

## Risk Analysis

| Risk | Impact | Priority | Evidence |
| :--- | :--- | :--- | :--- |
| **JSON Parsing Failure** | High (Crash) | **P0** | `NotificationApiService#getNotifications` |
| **Response Format Mismatch** | High (Crash) | **P0** | `PaymentApiService#getPaymentInstructions` |
| **Type Safety Loss** | Medium (Runtime) | **P1** | `verifyPayment` HashMap usage |
| **Endpoint Ghosting** | Medium (Logic) | **P1** | `CurfewRequest` missing in backend source |

---

## Recommendations
1. **Unify Response Wrapper**: (URGENT) Update `NotificationApiService` and `PaymentApiService` to use `BaseResponse<T>` for all methods.
2. **Standardize Call Style**: Adopt either `BaseResponse<T>` (letting an Interceptor handle errors) or `Response<BaseResponse<T>>` across the entire project.
3. **Formalize Request DTOs**: Replace all `HashMap` payloads (especially in Payment) with concrete `@Serializable` or `Gson` classes.
4. **Complete Mapping Layer**: Ensure no DTOs reach the UI layer. Convert them to Domain Models in the Data layer.
5. **Sync Roadmap**: Verify if `CurfewRequest` backend implementation exists in another branch or module.

## Conclusion
The API layer is architecturally sound in its intent but flawed in its execution. The inconsistencies in response wrapping are critical and must be resolved before the next release to prevent runtime crashes.

---
*Audited by AI Agent - Step 2 Complete*
