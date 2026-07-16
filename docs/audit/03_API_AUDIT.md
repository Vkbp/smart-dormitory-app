# API Compatibility Audit - SDMS Android

**Date**: July 14, 2026
**Version**: 1.1.0
**Status**: COMPLETED
**Auditor**: Senior Technical Lead AI

---

## Executive Summary
The API layer remains functionally complete but architecturally fragmented. While new features like "RFID Assignment" have been integrated into the `AdminApiService`, the critical inconsistencies in response wrapping and call execution styles (Raw `BaseResponse` vs. `Response<BaseResponse>`) persist. The high-priority discrepancy in `PaymentApiService` remains unresolved, posing a risk of runtime crashes if the backend synchronizes its response format.

**Overall API Compatibility Score: 85/100** (✔ Still Valid)

---

## API Compatibility Matrix

| Feature | Retrofit Interface | DTO Status | Mapper Status | Sync Status |
| :--- | :--- | :--- | :--- | :--- |
| **Auth** | `AuthApiService` | ✅ Matches | ✅ Domain Models | ✅ Hilt Injected |
| **Face** | `FaceApiService` | ⚠️ UUID mismatch | ✅ PageResponse | ✅ Multipart |
| **Payment** | `PaymentApiService` | ❌ Raw DTO Return | ✅ Complex Mapper | ✅ HashMap Request |
| **Checkout**| `CheckoutApiService` | ✅ BaseResponse | ✅ Domain Models | ✅ Nested DTO |
| **Admin** | `AdminApiService` | ✅ UUID Params | ✅ PageResponse | ✅ Multi-Role |

---

## ❌ Critical Discrepancies

### 1. Inconsistent Response Wrapping [✔ Still Valid]
- **Issue**: `PaymentApiService.getPaymentInstructions()` returns a raw `PaymentInstructionDto` instead of `BaseResponse<PaymentInstructionDto>`. This will cause a `JsonSyntaxException` if the backend wraps it (SSOT rule).
- **Affected File**: [PaymentApiService.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/data/payment/remote/PaymentApiService.kt#L20)
- **Impact**: High risk of crash during payment flow initialization.

### 2. Manual Request Construction [✔ Still Valid]
- **Issue**: `PaymentRemoteDataSourceImpl.verifyPayment` constructs a `HashMap<String, Any>` manually instead of using a `VerifyPaymentRequestDto`.
- **Affected File**: [PaymentRemoteDataSourceImpl.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/data/payment/remote/PaymentRemoteDataSourceImpl.kt#L12)
- **Status**: Pending refactor to Request DTO.

### 3. Error Handling Fragmentation [✔ Still Valid]
- **Issue**: Lack of standardized usage of `BaseRepository.safeApiCall`. Repositories like `CheckoutRepositoryImpl` and `FaceRepositoryImpl` still implement manual try-catch logic with fragmented error parsing.

### 4. DTO naming vs Domain naming [✔ Still Valid]
- **Issue**: UI layer ViewModels (e.g., `FaceManagementViewModel`) still consume DTOs directly, bypassing the Model mapping layer.

---

## 🏗️ Layer-by-Layer Verification

### Backend -> Retrofit
- **Auth**: Correct use of `@POST`, `@GET`, and `@PUT`.
- **Face**: Correct use of `@Multipart` and `@POST`.
- **Admin**: Correct use of `@Path` and `@Body`.

### Retrofit -> DTO
- **Mismatch**: `FaceApiService.registerFace` returns `BaseResponse<UUID>`, but the `FaceRepositoryImpl` treats it as `Result<Unit>`. While not a crash, it ignores the returned Face ID.

### DTO -> Mapper
- **Strength**: `PaymentMapper` is well-implemented with support for Local entities (Room) and Domain models.
- **Weakness**: Missing Mappers for `Auth` features; ViewModels use DTOs directly in some cases (e.g., `FaceManagementViewModel` uses `FaceProfileDto`).

### ViewModel -> Compose
- **Observation**: `PaymentInstructionScreen` uses a specific `PaymentInstructionViewModel` which is clean, but relies on a potentially broken API call (see Discrepancy #1).

---

## Risk Analysis

| Risk | Impact | Priority | Evidence |
| :--- | :--- | :--- | :--- |
| **Response Format Mismatch** | High (Crash) | **P0** | `PaymentApiService#getPaymentInstructions` |
| **Type Safety Loss** | Medium (Runtime error) | **P1** | `verifyPayment` HashMap |
| **Redundant JWT Info** | Low (Code smell) | **P2** | `RegisterFaceUseCase` params |

---

## Improvement Plan
1. **Unify Response Wrapper**: Wrap all `ApiService` methods in `BaseResponse<T>`.
2. **Standardize Repository Calls**: Migrate all repositories to use `BaseRepository.safeApiCall`.
3. **Formalize Request DTOs**: Replace all `HashMap` payloads with concrete `@Serializable` or `Gson` DTO classes.
4. **Complete Mapping Layer**: Ensure no DTOs reach the UI layer. Convert them to Domain Models in the Repository.

---

## Conclusion
The API layer is stable for current features but lacks the rigor required for a large-scale enterprise application. Standardizing on `BaseResponse` and `SafeApiCall` is the most urgent technical debt in this module.

---

## Audit History

| Date | Summary | Files Changed | Issues Fixed | New Issues | Score |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 2026-07-11 | Initial API Audit | All | - | 4 | 85 |
| 2026-07-14 | Update Audit: Verified Admin API additions | AdminApiService | None | Inconsistent Call Styles | 85 |
