# Architecture Refactoring Plan

Refactor the SDMS project structure to improve scalability, maintainability, and support for a production-ready multi-role environment.

## User Review Required

> [!IMPORTANT]
> - **Moving `BaseResponse` and `PageResponse`**: I plan to move these from `data/common/dto/response` to `core/network` or `core/common`. This will affect almost all `ApiService` and `RemoteDataSource` files.
> - **Splitting `AuthRepository`**: I propose splitting it into `AuthRepository` (Remote Auth), `SessionRepository` (Local Session), and `SecurityRepository` (Biometrics).
> - **DI Module Splitting**: Giant modules in `di/feature` will be broken down by feature or group (e.g., `AuthModule`, `PaymentModule`).

## Proposed Changes

### Core Module Refactoring

Refactor `core` package into a standardized structure.

#### [NEW] [BaseViewModel.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/core/base/BaseViewModel.kt)
- Standardize UI state and event handling.

#### [NEW] [BaseContract.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/core/base/BaseContract.kt)
- Standardize the MVI-lite contract interface.

#### [Constants.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/core/common/Constants.kt)
- Consolidate all system constants here.

---

### Data Layer Refactoring

Clean up and standardize feature packages.

#### [NEW] [AuthModule.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/di/feature/AuthModule.kt)
- Extracted from `RepositoryModule` and `DataSourceModule`.

#### [DELETE] [data/dto/dto](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/data/dto/dto)
- Remove empty/redundant DTO folders.

---

### Final Folder Tree (Production-Ready)

```
com/ktx/dormitory/
├── ai/                 # Face analysis & processing logic
├── core/
│   ├── base/           # BaseViewModel, BaseContract, BaseRepository
│   ├── common/         # Constants, Result, Global DTOs (BaseResponse)
│   ├── dispatcher/     # CoroutineDispatchers
│   ├── network/        # Interceptors, Authenticators, NetworkMonitor, ApiClient
│   ├── security/       # BiometricUtils, SecurityUtils, Encryption
│   ├── datastore/      # DataStoreManager
│   ├── sync/           # WorkManager, Sync Workers
│   ├── mapper/         # Mapper interface
│   ├── exception/      # Domain & App exceptions
│   ├── extension/      # Kotlin Extensions
│   └── util/           # DateTime, Image, String utils
├── di/
│   ├── common/         # Coroutine, App modules
│   ├── network/        # OkHttp, Retrofit
│   ├── database/       # Room, DAOs
│   ├── repository/     # Split by feature (AuthRepositoryModule, etc.)
│   ├── datasource/     # Split by feature (AuthDataSourceModule, etc.)
│   ├── worker/         # Hilt Worker bindings
│   └── security/       # Biometric/Crypto providers
├── data/
│   ├── [feature]/      # auth, payment, room, profile...
│   │   ├── dto/        # Request/Response DTOs
│   │   ├── local/      # DAO, Entity, Relation
│   │   ├── remote/     # ApiService, RemoteDataSource
│   │   ├── mapper/     # DTO to Domain, Entity to Domain mappers
│   │   └── repository/ # Repository implementations
│   └── common/         # Shared entities (PendingSync)
├── domain/
│   ├── [feature]/
│   │   ├── model/      # Domain models
│   │   ├── repository/ # Repository interfaces
│   │   └── usecase/    # Single-purpose UseCases
│   └── common/         # Shared domain models
├── navigation/
│   ├── graphs/         # Nested Graphs (Student, Staff, Admin)
│   ├── components/     # NavHost, BottomBar
│   └── RoleGuard.kt    # Role-based access control
└── presentation/
    ├── features/
    │   ├── auth/
    │   ├── student/
    │   ├── staff/
    │   └── admin/
    ├── components/     # Shared UI components
    └── theme/          # Material 3 theme, colors, typography
```

## Migration Plan

| Step | Purpose | Affected files | Risk |
| :--- | :--- | :--- | :--- |
| 1. Refactor Core | Consolidate infra | `core/*`, `BaseResponse` | Medium (Broken imports) |
| 2. Split DI | Improve modularity | `di/*` | Low (Hilt errors) |
| 3. Organize Data | Feature-first | `data/*` | Medium (Broken repo bindings) |
| 4. Organize Domain | Feature-first | `domain/*` | Low (Broken UseCase imports) |
| 5. Role-based Logic| Preparation | `domain/*/usecase/` | Low (New UseCases) |

## Verification Plan

### Automated Tests
- `gradlew build`: Ensure compilation is successful after moving files.
- `gradlew test`: Run all existing unit tests to ensure no regression.
- Specific check for `AuthRepository` tests after splitting.

### Manual Verification
- Deploy to device/emulator.
- Verify Login flow (Student).
- Verify Navigation between tabs.
- Verify Biometric authentication.
