# Implementation Report: Admin Smart Access Control

## 1. Overview
The Admin Smart Access Control feature allows authorized personnel to remotely manage physical access points (doors, gates) within the dormitory. This is critical for both daily operations (helping students who lost keys) and emergency situations (fire safety).

## 2. Technical Architecture
The implementation follows Clean Architecture principles:

- **Presentation Layer**: 
    - `SmartAccessScreen`: Jetpack Compose UI with dedicated dialogs for Unlock and Emergency actions.
    - `SmartAccessViewModel`: Manages UI state using MVI pattern and handles resource loading (Buildings, Gates).
- **Domain Layer**:
    - `RemoteUnlockUseCase`: Triggers individual gate unlocking.
    - `EmergencyOverrideUseCase`: Triggers building-wide or campus-wide emergency opening.
- **Data Layer**:
    - `AdminRepositoryImpl`: Handles API communication with the Spring Boot backend.
    - `AdminApiService`: Defines Retrofit endpoints with strict 10s timeouts for IoT responsiveness.

## 3. Business Rules Enforcement
- **BR-A03 (Remote Unlock Authority)**: Only users with the `ADMIN` role (verified via JWT) can access these endpoints.
- **BR-E01 (Emergency Logging)**: The `reason` field is mandatory for the `v1/access/emergency` endpoint to ensure accountability during fire drills or real emergencies.

## 4. Academic Rationale (Thesis)
- **High Availability**: IoT operations use a reduced timeout (10s) to ensure the UI doesn't hang if the hardware is unresponsive, providing immediate feedback to the operator.
- **Security by Design**: Access control is centralized at the backend, but the mobile client provides a secure, role-guarded interface for real-time management.

## 5. Verification
- API endpoints verified against Backend specification.
- UI state machine tested for loading, success, and error states.
- Hilt dependency injection confirmed for all layers.

---
*Created on: 2026-07-16*
