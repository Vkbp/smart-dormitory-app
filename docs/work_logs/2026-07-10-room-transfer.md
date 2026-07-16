# Implementation Report: Room Transfer Request

**Date:** 2026-07-10
**Task:** Implement "Room Transfer Request" and "Request History" for Students.

## Changes

### 1. Data Layer
- **RoomApiService**: Added `POST /api/v1/student/change-room` and `GET /api/v1/student/change-room`.
- **DTOs**: 
    - `RoomTransferRequest`: Request body for submission.
    - `RoomTransferHistoryDto`: Response item for history list.
- **Repository**: Updated `RoomRepositoryImpl` to handle transfer requests and history, including mapping via `RoomMapper.kt`.

### 2. Domain Layer
- **Models**: Created `RoomTransferHistory` domain model.
- **UseCases**:
    - `SubmitTransferRequestUseCase`: Handles validation and submission.
    - `GetTransferHistoryUseCase`: Fetches user's transfer history.

### 3. Presentation Layer
- **Contract**: Defined `RoomTransferUiState`, `UiEvent`, and `UiEffect`.
- **ViewModel**: `RoomTransferViewModel` manages form state, submission logic, and history loading.
- **UI**: `RoomTransferScreen` implemented with Jetpack Compose.
    - Tab-based navigation: "Gửi yêu cầu" (Form) and "Lịch sử" (List).
    - Form includes Reason (required) and Target Room ID (optional).
    - History list displays status (`PENDING`, `APPROVED`, `REJECTED`) and admin notes.

### 4. Navigation & Integration
- Added `RoomTransfer` route to `Screen.kt`.
- Registered route in `StudentNavGraph.kt`.
- Added "Đổi phòng" item to `HomeScreen` dashboard.

### 5. Documentation
- Updated `API_INDEX.md` and `FEATURE_INDEX.md`.
- Created this work log.

## Verification
- Build successful.
- Manual verification of navigation flow: Home -> Đổi phòng.
- Validation of required field (Reason) in the form.
- Tab switching logic confirmed.
- Code adheres to Clean Architecture and project naming conventions.
