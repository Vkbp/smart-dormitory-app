# Technical Debt Tracker - SDMS Android

This document tracks unresolved technical, architectural, and security issues.

## 🏗️ Architecture Debt
| ID | Issue | Priority | Status | Impact |
| :--- | :--- | :--- | :--- | :--- |
| ARCH-05 | Lack of Push Notifications for Curfew status | P2 | OPEN | Students must manually refresh to see approval status |

## 🧪 Testing Debt
| ID | Issue | Priority | Status | Impact |
| :--- | :--- | :--- | :--- | :--- |
| TEST-01 | 0% Unit Test coverage for UseCases/Repos | P0 | IN_PROGRESS | Added `LoginUseCaseTest`. Expanding coverage. |
| TEST-02 | Lack of instrumentation tests for Student flow | P1 | OPEN | Broken UI state detection |

## 📦 Infrastructure Debt
| ID | Issue | Priority | Status | Impact |
| :--- | :--- | :--- | :--- | :--- |
| INFRA-01 | Missing FCM (Firebase Cloud Messaging) | P0 | DEFERRED | Backend support for IN_APP channel is currently pending. |
| ACC-01 | Missing Access QR Fallback (Offline QR) | P1 | DEFERRED | Pending algorithm/spec alignment with Backend. |

---
*Maintained by the Documentation Governance System.*
