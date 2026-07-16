# Architectural Decision Log (ADR) - SDMS Android

This log tracks key technical and architectural choices.

## ADR-001: Pure Remote Face Identification

- **Status**: ACCEPTED
- **Date**: 2026-07-08
- **Context**: Face identification required ArcFace-512 embeddings. Running this on-device via TFLite caused 400ms+ latency and high RAM usage.
- **Decision**: All embedding extraction and identity matching moved to the FastAPI backend.
- **Alternatives**: MobileFaceNet (too low accuracy), Quantized ArcFace (lower precision).
- **Consequences**:
    - **Pros**: Reduced APK size (no .tflite models), improved accuracy, lower device heat.
    - **Cons**: Requires stable internet for access control (partially mitigated by "Offline-First" historical logs).

## ADR-002: Hilt for Dependency Injection

- **Status**: ACCEPTED
- **Date**: Project Inception
- **Context**: Need a scalable DI framework for Multi-module readiness.
- **Decision**: Use Dagger Hilt.
- **Consequences**: Standardizes dependency lifetimes and simplifies Testing via `HiltAndroidTest`.

## ADR-003: Documentation Governance System

- **Status**: ACCEPTED
- **Date**: 2026-07-11
- **Context**: Project scaling to multi-agent development requires strict documentation-to-code synchronization.
- **Decision**: Implement a mandatory 10-step sync workflow and a multi-index navigation system.
- **Consequences**: Higher overhead per task but significantly reduced technical debt and "stale" documentation.

## ADR-004: Database Encryption via SQLCipher

- **Status**: ACCEPTED
- **Date**: 2026-07-14
- **Context**: PII and authentication logs stored in Room require at-rest encryption for security audit compliance.
- **Decision**: Integrate `androidx.sqlite:sqlite-ktx` and `net.zetetic:android-database-sqlcipher`.
- **Consequences**: Improved security; slight performance overhead on DB open; increased APK size.

## ADR-005: Theme Standardization (Dark Mode)

- **Status**: ACCEPTED
- **Date**: 2026-07-14
- **Context**: Modern UI requirement for low-light environments.
- **Decision**: Implement full Material 3 Dark/Light color schemes in `Theme.kt`.
- **Consequences**: Improved accessibility and UX consistency.

## ADR-006: Role-Based Vertical Slicing

- **Status**: ACCEPTED
- **Date**: 2026-07-14
- **Context**: Project complexity increasing; need for strict isolation between Student and Admin logic. Layer-based organization caused cross-role coupling.
- **Decision**: Restructure packages into `shared/`, `student/`, and `admin/` domains. Each domain contains its own `data`, `domain`, and `presentation` layers.
- **Consequences**: Improved encapsulation, simplified dependency management, and easier maintenance for role-specific features.

## ADR-007: Integration of Curfew Request Feature

- **Status**: ACCEPTED
- **Date**: 2026-07-15
- **Context**: Students need a way to request late entry (curfew violation) through the app to avoid security bottlenecks.
- **Decision**: Integrate Curfew Request functionality into the `student.access` module. Uses a dedicated form for submitting reasons and expected arrival times, with a history view for status tracking.
- **Consequences**:
    - **Pros**: Reduces manual intervention by guards; improves security audit trail.
    - **Cons**: Adds new API dependencies and state management complexity to the Access module.
