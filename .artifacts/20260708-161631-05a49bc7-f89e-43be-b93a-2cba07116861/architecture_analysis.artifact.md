# Architecture Analysis

## Feature Classification

| Feature | Category | Reason |
| :--- | :--- | :--- |
| Auth | Shared | Login, session management are common across roles. |
| Profile | Shared | Basic profile info (name, avatar, etc.) is common, though fields might differ. |
| Notification | Shared | System-wide notification mechanism. |
| Access (History) | Shared/Student | Students see their own, Staff/Admin see everyone's. Logic might be shared with permission filters. |
| Face | Shared | Registration and verification mechanism is common. |
| Room | Shared | Room information is shared, but management is Staff/Admin. |
| Payment | Student/Staff | Students pay, Staff/Admin verify/manage. |
| Checkout | Student/Staff | Students request, Staff/Admin approve. |
| Extension | Student/Staff | Students request, Staff/Admin approve. |
| Settings | Shared | General app settings. |

## Problems Found

1. **Core Package Clutter**:
    - `core/util` vs `core/utils` (inconsistency).
    - `core/base` is empty.
    - `core/constants` is empty.
    - Package names like `com.ktx.dormitory.core.util.util` (duplication).
    - Generic `BaseResponse` is in `data/common/dto/response` instead of `core`.
2. **DI Giant Modules**:
    - `RepositoryModule` and `DataSourceModule` in `di/feature` bind everything, making it non-modular and slow to compile.
3. **Data Layer Inconsistencies**:
    - `data/dto/dto` exists but is empty.
    - Some shared DTOs are in `data/common/dto` while others are scattered.
4. **Repository Bloat**:
    - `AuthRepository` handles API, Session, and Biometrics. These should be split for better SRP.
5. **Inconsistent Package Naming**:
    - Singular `util` vs Plural `utils`.
6. **Missing Base Infrastructure**:
    - No `BaseViewModel` or `BaseContract` to standardize the MVI-lite pattern.
7. **Database Organization**:
    - `AppDatabase` is fine, but there's no clear separation of `entity`, `dao`, `relation`, etc., within feature packages (it's flat).

## Feature Classification (Detailed)

| Feature | Category | Reason |
| :--- | :--- | :--- |
| Auth | Shared | Core login/session logic is universal. |
| Profile | Shared | Basic user info is shared. Role-specific extensions can be handled via polymorphism or optional fields. |
| Notification | Shared | Generic messaging system. |
| Access | Shared | History is shared, but filtering differs by role. |
| Face | Shared | Identity registration is a common requirement. |
| Room | Shared | Information is shared; management (Staff) uses same models. |
| Payment | Mixed | Student pays; Staff manages. Shared DTOs, different UseCases. |
| Checkout | Mixed | Student requests; Staff approves. Shared DTOs, different UseCases. |
| Extension | Mixed | Student requests; Staff approves. Shared DTOs, different UseCases. |
| Settings | Shared | Theme, biometrics, language are common. |
| Dashboard | Role-specific | Admin/Staff dashboards are fundamentally different from Student Home. |
| User Management| Admin | Admin only. |
| System Config | Admin | Admin only. |
