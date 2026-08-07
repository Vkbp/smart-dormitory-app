# Implementation Plan - Checkout Request (Đơn Trả Phòng) Feature

This plan outlines the integration of the "Checkout Request" feature for students, following the backend specification.

## User Review Required

- **Endpoint Change**: Updating endpoints from `v1/students/checkout-requests` to `v1/checkout-requests` and `v1/checkout-requests/my`.
- **UI Status Mapping**: Updating status labels and colors to match the new backend status set (`COMPLETED` added).

## Proposed Changes

### Core Utilities

#### [DateTimeUtil.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/core/util/DateTimeUtil.kt)

- Add `formatToIsoDate(timestamp: Long): String` to format dates as `yyyy-MM-dd` as required by the backend.

### Student Checkout Feature

#### [CheckoutModels.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/student/checkout/domain/model/CheckoutModels.kt)

- Update `CheckoutResponse` to include all fields returned by the backend: `studentCode`, `fullName`, `reason`, `bankAccountNumber`, `bankName`, `createdAt`.
- Ensure `CheckoutRequest` fields remain aligned with the request body.

#### [CheckoutApiService.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/student/checkout/data/remote/CheckoutApiService.kt)

- Update `submitCheckoutRequest` endpoint to `v1/checkout-requests`.
- Update `getCheckoutHistory` endpoint to `v1/checkout-requests/my`.

#### [CheckoutScreen.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/student/checkout/presentation/CheckoutScreen.kt)

- Update `CheckoutForm` to use `DateTimeUtils.formatToIsoDate` for the payload.
- Update `CheckoutHistoryItem` to:
    - Support new status `COMPLETED`.
    - Map statuses to the recommended Vietnamese labels:
        - `PENDING` -> "Chờ xét duyệt"
        - `APPROVED` -> "Đã được duyệt"
        - `REJECTED` -> "Bị từ chối"
        - `COMPLETED` -> "Đã hoàn tất trả phòng"
    - Update colors for statuses.
    - (Optional) Display `createdAt` or `reason` if requested by user (currently not in requirements but available in model).

## Verification Plan

### Automated Tests
- N/A (Unit tests for ViewModel/UseCase could be added if requested, but logic is mostly API integration).

### Manual Verification
1.  **Submit Request**:
    - Open Checkout Screen.
    - Click "Tạo yêu cầu".
    - Select a date (verified to be >= 7 days ahead).
    - Fill in reason and bank details.
    - Submit and verify success Snackbar and history update.
2.  **View History**:
    - Verify list shows correct status labels and room/bed info.
3.  **Error Handling**:
    - Try to submit a request when already having a `PENDING` one (Backend should return 400).
    - Verify error message display.
