# Coding Standard - SDMS Android

This document defines the coding conventions and standards for Android development.

## 1. Naming Conventions
- **Classes/Interfaces**: `PascalCase` (e.g., `LoginViewModel`, `AuthRepository`).
- **Functions/Variables**: `camelCase` (e.g., `onEvent`, `isLoading`).
- **Resource IDs**: `snake_case` (e.g., `btn_login`).
- **Compose**: 
    - Screen: `[Feature]Screen.kt`.
    - ViewModel: `[Feature]ViewModel.kt`.
    - Contract: `[Feature]Contract.kt`.

## 2. Package Organization
Standard folder structure:
```
com.ktx.dormitory/
├── core/         # Shared utils, network, interceptors
├── data/         # DTO, Entity, RepositoryImpl
├── domain/       # UseCase, Model, Repository Interface
├── presentation/ # Screens, ViewModels, Theme
└── di/           # Hilt Modules
```

## 3. Jetpack Compose Rules
- **Statelessness**: Composable functions should be stateless where possible. Pass state down and events up.
- **Preview**: Every custom component should have a `@Preview`.
- **Material 3**: Use `MaterialTheme.colorScheme` and `MaterialTheme.typography` instead of hardcoded values.

## 4. ViewModel & StateFlow
- **MVI-lite**: ViewModels must implement `onEvent` and expose `uiState` as `StateFlow`.
- **Scope**: Use `viewModelScope` for coroutines. Ensure proper cancellation handling.

## 5. Repository & UseCase
- **UseCase**: Each UseCase should perform exactly one task (Single Responsibility).
- **Safe Call**: Repositories must use `safeApiCall` to catch network exceptions and return `Result<T>`.

## 6. Offline-First Rules
- **Room**: Entity names must end with `Entity` (e.g., `InvoiceEntity`).
- **Sync**: Use `PendingSyncEntity` for actions that need backend acknowledgment.

## 🔗 Liên kết tài liệu (Related Documents)
- **Prerequisites**: [Architecture Principles](./ARCHITECTURE_PRINCIPLES.md)
- **Read Next**: [API Integration Guide](./API_INTEGRATION_GUIDE.md)
- **Checklist**: [Code Review Checklist](./CODE_REVIEW_CHECKLIST.md)

---
*Derived from SDMS Backend Docs: PROJECT_RULE.md, AGENTS.md*
