# Technical Debt Tracker - SDMS Android

This document tracks unresolved technical, architectural, and security issues.

## 🏗️ Architecture Debt
| ID | Issue | Priority | Status | Impact |
| :--- | :--- | :--- | :--- | :--- |
| ARCH-01 | ViewModels bypass Domain layer to call DataSources | P0 | CLOSED | Fixed: Added UseCases and removed direct Data dependencies |
| ARCH-02 | `LoginViewModel` is a God Object (11+ UseCases) | P1 | CLOSED | Fixed: Decomposed into Login, Account, and Security ViewModels |
| ARCH-03 | Inconsistent use of `BaseViewModel` | P2 | CLOSED | Standardized all core ViewModels |
| ARCH-04 | Logic in ViewModels (Merging) | P1 | OPEN | `AccessViewModel` merging logic should move to UseCase |
| ARCH-05 | Lack of Push Notifications for Curfew status | P2 | OPEN | Students must manually refresh to see approval status |

## 🛡️ Security Debt
| ID | Issue | Priority | Status | Impact |
| :--- | :--- | :--- | :--- | :--- |
| SEC-01 | Cleartext traffic permitted in Manifest/Config | P0 | CLOSED | Fixed: HTTPS enforced in Manifest & Network Config |
| SEC-02 | Lack of TLS Pinning | P1 | CLOSED | Fixed: Added `CertificatePinner` in `NetworkModule` |
| SEC-03 | Room Database is unencrypted | P1 | CLOSED | Fixed: Integrated SQLCipher encryption |
| SEC-04 | Biometric auth not cryptographically bound | P2 | OPEN | UI-only protection |
| SEC-05 | Placeholder TLS Pins | P1 | OPEN | `Constants.kt` uses dummy pins |
| SEC-06 | Cleartext Traffic Permitted | P0 | CLOSED | Fixed: Manifest and Config hardened |

## 🚀 Performance Debt
| ID | Issue | Priority | Status | Impact |
| :--- | :--- | :--- | :--- | :--- |
| PERF-01 | Excessive Bitmap allocations in AI module | P1 | CLOSED | Fixed: Optimized `FaceAnalyzer` to skip Bitmap creation when not needed |
| PERF-02 | Missing unique keys in `LazyColumn` items | P1 | CLOSED | Fixed: Added `key` to all major lists |
| PERF-03 | R8/Minification disabled in release builds | P1 | CLOSED | Large APK, insecure code |

## 🧪 Testing Debt
| ID | Issue | Priority | Status | Impact |
| :--- | :--- | :--- | :--- | :--- |
| TEST-01 | 0% Unit Test coverage for UseCases/Repos | P0 | IN_PROGRESS | Added `LoginUseCaseTest`. Expanding coverage. |
| TEST-02 | Lack of instrumentation tests for Student flow | P1 | OPEN | Broken UI state detection |

## 🎨 UI/UX Debt
| ID | Issue | Priority | Status | Impact |
| :--- | :--- | :--- | :--- | :--- |
| UI-01 | Lack of Dark Mode support | P1 | CLOSED | Fixed: Added `DarkColors` and enabled theme switching |
| UI-02 | Missing content descriptions for 60% of icons | P2 | OPEN | Poor Accessibility |
| UI-03 | Missing Client-side Regex validation in forms | P1 | OPEN | Poor UX and invalid data submission risk |

## 📦 Infrastructure Debt
| ID | Issue | Priority | Status | Impact |
| :--- | :--- | :--- | :--- | :--- |
| INFRA-01 | Missing FCM (Firebase Cloud Messaging) | P0 | DEFERRED | Backend support for IN_APP channel is currently pending. |
| INFRA-02 | Missing Paging 3 in Access/Payment lists | P1 | OPEN | Potential memory issues with large datasets. Backend supports pagination. |
| ACC-01 | Missing Access QR Fallback (Offline QR) | P1 | DEFERRED | Pending algorithm/spec alignment with Backend. |

---
*Maintained by the Documentation Governance System.*
