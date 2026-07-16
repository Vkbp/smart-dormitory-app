# Implementation Plan - Student RFID Card Assignment

This plan outlines the steps to integrate the RFID card assignment feature into the Student Check-in screen. This feature allows administrators to assign an RFID card to a student by entering or scanning a Hex code during the check-in process.

## Proposed Changes

### Domain Layer

#### [AdminRepository.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/domain/admin/repository/AdminRepository.kt)
- Add `assignRfid(studentId: UUID, rfidCode: String): Result<String>` to the interface.

#### [AdminUseCases.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/domain/admin/usecase/AdminUseCases.kt)
- Add a new `AssignRfidUseCase` class.

#### [AdminModels.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/domain/admin/model/AdminModels.kt)
- Add `studentId` to `CheckInStudent` model to support the API call (currently only has `assignmentId`).

---

### Data Layer

#### [AdminApiService.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/data/admin/remote/AdminApiService.kt)
- Add the POST endpoint for RFID assignment: `/api/v1/students/{studentId}/rfid`.

```kotlin
@POST("api/v1/students/{studentId}/rfid")
suspend fun assignRfid(
    @Path("studentId") studentId: UUID,
    @Query("rfidCode") rfidCode: String
): Response<BaseResponse<Unit>>
```

#### [AdminRepositoryImpl.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/data/admin/repository/AdminRepositoryImpl.kt)
- Implement `assignRfid` using `handleMessageResponse`.

#### [CheckInSearchResponseDto.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/data/admin/dto/response/CheckInSearchResponseDto.kt)
- Add `studentId` field to the DTO.

#### [AdminMappers.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/data/admin/mapper/AdminMappers.kt)
- Update `CheckInSearchResponseDto.toDomain()` to include `studentId`.

---

### Presentation Layer

#### [CheckInContract.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/presentation/features/admin/checkin/CheckInContract.kt)
- Add `AssignRfid(studentId: UUID, rfidCode: String)` to `CheckInUiEvent`.

#### [CheckInViewModel.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/presentation/features/admin/checkin/CheckInViewModel.kt)
- Inject `AssignRfidUseCase`.
- Add `assignRfid(studentId: UUID, rfidCode: String)` function to handle the event.

#### [CheckInScreen.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/presentation/features/admin/checkin/CheckInScreen.kt)
- Implement `RfidAssignmentDialog` composable.
- Add "Cấp thẻ RFID" button to `StudentCheckInInfo`.
- Manage dialog visibility state in `CheckInScreen`.

---

### Documentation

#### [API_INTEGRATION_GUIDE.md](file:///D:/HocTap/LuanVan/Code/docs/architecture/API_INTEGRATION_GUIDE.md)
- Document the new RFID assignment API endpoint.

---

## Verification Plan

### Automated Tests
- I will check if there are any existing tests for `AdminRepositoryImpl` or `CheckInViewModel` and add unit tests if possible.
- Command: `./gradlew test` (to be refined after finding specific test files).

### Manual Verification
- Deploy the app to a device/emulator.
- Navigate to "Nhận phòng" (Check-in) screen.
- Search for a student by CCCD.
- Click "Cấp thẻ RFID".
- Enter a hex code in the dialog.
- Confirm and verify the Toast message "Gán thẻ thành công" (Mocking API response if needed or using a real backend if available).
