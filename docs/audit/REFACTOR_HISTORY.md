# Refactor History - SDMS Android

This document records major structural changes to the project to ensure traceability and capture lessons learned.

## [2026-07-08] AI Client Optimization & Server-Side Shift

- **Reason**: Performance issues on low-end devices and duplication of logic between local TFLite and Server API.
- **Affected Modules**: `ai`, `presentation/features/face`, `data/face`.
- **Files**: Removed `FaceNet` models, `TFLite` interpreters, and local `FaceDao`.
- **Architecture Impact**:
    - Shifted from Hybrid AI (Local + Remote) to **Pure Remote AI**.
    - ML Kit is now used only for UX (Cropping, Liveness) rather than identification.
- **Risk**: Increased latency for face identification (requires network).
- **Rollback Plan**: Revert to version 1.0.0 branch if backend latency exceeds 3 seconds.
- **Lessons Learned**: Local TFLite overhead for high-precision models (ArcFace) is too high for students with mid-range phones.

## [2026-07-07] Repository Extraction & Clean Layering

- **Reason**: Technical debt where remote data sources were used directly in UseCases.
- **Affected Modules**: `payment`, `access`, `profile`.
- **Architecture Impact**: Formalized the Repository Pattern (Interface in Domain, Impl in Data).
- **Lessons Learned**: Centralizing data mapping in repositories reduces duplicate logic in ViewModels.

---
*Maintained by the Documentation Governance System.*
