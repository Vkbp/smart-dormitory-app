# Audit Changelog - SDMS Android

Permanent history of system audits and score trends. **Never recreate this file; always append.**

## Audit Timeline

### [2026-07-11] Feature: Student RFID Assignment Integration
- **Contributor**: Development Agent
- **Actions Taken**:
    - Implemented RFID card assignment feature in Domain, Data, and Presentation layers.
    - Updated `AdminRepository`, `AdminApiService`, and `CheckInViewModel`.
    - Added "Cấp thẻ RFID" UI with auto-focus dialog in `CheckInScreen`.
    - Updated `API_INTEGRATION_GUIDE.md` with new endpoint documentation.

### [2026-07-14] Phase 4: Role Isolation & God Object Refactor
- **Auditor**: Senior Lead AI
- **Total Maturity Score**: **83/100**
- **Improvements**:
    - **Architecture**: Implemented Role-Based Vertical Slicing. Strictly isolated `admin/`, `student/`, and `shared/` logic.
    - **Architecture**: Decomposed `LoginViewModel` (God Object) into specialized ViewModels (Login, Account, Security).
    - **Security**: Added ADR-006 to record architectural isolation decisions.
    - **Clean Architecture**: Rebuilt Admin data layer to support isolation.
- **Regressions**: None.

## Score Trend (0-100)

| Category | 2026-07-11 | 2026-07-14 | 2026-07-15 | Target (v2.0) |
| :--- | :--- | :--- | :--- | :--- |
| **Architecture** | 78 | 92 | 93 | 95 |
| **Business Logic** | 92 | 93 | 95 | 95 |
| **Security** | 45 | 65 | 65 | 85 |
| **Performance** | 72 | 82 | 83 | 85 |
| **Testing** | 15 | 16 | 16 | 70 |
| **UI/UX** | 85 | 87 | 88 | 90 |
| **Production Ready** | 40 | 55 | 60 | 90 |

### [2026-07-16] Feature: Admin Smart Access & AI Refinement
- **Contributor**: Development Agent
- **Actions Taken**:
    - **Feature Implementation**: Fully documented Admin Smart Access (Remote Unlock & Emergency Override).
    - **Documentation Sync**: Updated `API_INTEGRATION_GUIDE.md`, `BUSINESS_INDEX.md` with Smart Access and IoT rules.
    - **Architecture Optimization**: Refactored `FaceAnalyzer.kt` to use interface-based callbacks, decoupling AI core from Presentation layer.
    - **Maturity**: Improved "Production Ready" score due to better IoT error handling and documentation.
    - **Academic Support**: Created detailed Implementation Report `docs/implementation/003_ADMIN_SMART_ACCESS.md`.

### [2026-07-15] Feature: Student Curfew Request Integration
- **Contributor**: Development Agent
- **Actions Taken**:
    - Integrated Curfew Request functionality into `student.access` module.
    - Added `CurfewRequest` domain model, DTOs, and mapping logic.
    - Updated `AccessRepository` and `AccessApiService` for new endpoints.
    - **Technical Optimization**: Implemented Offline-First strategy using Room Database (`CurfewRequestEntity`, `CurfewRequestDao`).
    - Implemented `SubmitCurfewRequestUseCase`, `GetCurfewRequestsUseCase`, and `ObserveCurfewRequestsUseCase`.
    - Created `CurfewRequestScreen` for submitting late entry requests and viewing history.
    - Updated `AccessHistoryScreen` to navigate to Curfew Request form.
    - Synchronized documentation: Updated `PROJECT_RULE.md`, `BUSINESS_INDEX.md`, `PACKAGE_INDEX.md`, và `DECISION_LOG.md`.

### [2026-07-16] Repository Audit: GitHub Readiness
- **Auditor**: Senior Android Architect (AI)
- **Actions Taken**:
    - Performed comprehensive Repository Audit for Open Source Readiness.
    - Generated `docs/audit/GITHUB_REPOSITORY_AUDIT.md`.
    - Identified missing standard files (`LICENSE`, `CONTRIBUTING.md`, etc.).
    - Evaluated Security, Structure, and Documentation for public release.
    - **Current Readiness Score**: **72/100**.

---
*Maintained by the Documentation Governance System.*
