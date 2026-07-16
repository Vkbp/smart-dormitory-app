# Architecture Principles - SDMS Android

This document outlines the core architecture decisions and principles for the Smart Dormitory Management System (SDMS) Android application.

## 1. Clean Architecture
The application follows a strict three-layer Clean Architecture to ensure separation of concerns, testability, and maintainability.

- **Presentation Layer**: Compose-based UIs and ViewModels. Uses MVI-lite (Contract: State, Event, Effect).
- **Domain Layer**: Contains Business Models, Repository Interfaces, and UseCases. This is the "heart" of the application and contains no Android-specific dependencies.
- **Data Layer**: Repository implementations, Remote (Retrofit) and Local (Room) data sources, and Mappers.

> [!NOTE]
> Dependency flow is strictly inward: `Presentation -> Domain <- Data`.

## 2. Feature-based Organization
Code is organized by functional feature modules (e.g., `auth`, `face`, `payment`) rather than technical layers at the top level. This allows for better scalability and potential future extraction into Gradle modules.

## 3. Single Source of Truth (SSOT)
The UI should only observe data from the Repository (usually via Flow). The Repository is responsible for synchronizing between the Remote API and Local Database.
- **Offline First**: For critical student features (Profile, Payment History), data is cached in Room and synced when connectivity is available.
- **StateFlow**: ViewModels expose UI state through `StateFlow` to ensure a single, consistent state is rendered.

## 4. MVI-lite Pattern
To manage complex UI logic, we use a simplified Model-View-Intent (MVI) pattern:
- **State**: A data class representing the entire UI state.
- **Event**: Sealed classes representing user actions.
- **Effect**: One-time side effects (Toasts, Navigation) handled via `Channel`.

## 5. Dependency Injection (Hilt)
Dagger Hilt is used for DI. Each feature should have its own Hilt module (e.g., `AdminModule`, `AuthModule`) to manage its dependencies.

## 6. Business Rules Enforcement
While the Backend is the ultimate authority, the Android app must enforce basic business rules for a better UX:
- **Validation**: Perform client-side validation (e.g., password complexity, required fields) before calling APIs.
- **Guard Conditions**: Disable buttons or show warnings if user does not meet business criteria (e.g., cannot request checkout if debt exists - BR-R02).

## 7. Performance Principles
- **Recomposition Optimization**: Use `key` in `LazyColumn`, avoid unstable types in UI state.
- **Network Efficiency**: Cache images using Coil, avoid redundant API calls by checking cache validity.
- **Memory Management**: Release resources (CameraX, TFLite) immediately when screens are disposed.

## 🔗 Liên kết tài liệu (Related Documents)
- **Prerequisites**: [PROJECT_RULE.md](../../PROJECT_RULE.md)
- **Read Next**: [Coding Standard](./CODING_STANDARD.md)
- **See Also**: [API Integration Guide](./API_INTEGRATION_GUIDE.md)

---
*Derived from SDMS Backend Docs: PROJECT_RULE.md, BUSINESS_RULES.md*
