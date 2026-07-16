# Code Review Checklist - SDMS Android

Mandatory checks before merging a Pull Request.

## 1. Architecture
- [ ] Does it follow Clean Architecture?
- [ ] Is the business logic in the Domain layer?
- [ ] Are DTOs mapped to Domain Models?
- [ ] Does it use MVI-lite Contract?

## 2. Business Logic
- [ ] Does it violate any `BUSINESS_RULES.md`? (e.g., BR-R02 Debt check).
- [ ] Are edge cases (empty list, null data) handled?
- [ ] Is input validation implemented on the client side?

## 3. Security
- [ ] Are tokens stored in secure storage?
- [ ] Is sensitive data masked in logs?
- [ ] Are there hardcoded API keys or IDs?
- [ ] Does it respect Role-Based Access Control?

## 4. Performance
- [ ] Is Recomposition minimized in Compose?
- [ ] Are resources (CameraX/TFLite) released properly?
- [ ] Are redundant API calls avoided?
- [ ] Is image loading efficient (Coil)?

## 5. Offline & Concurrency
- [ ] Does it handle "No Internet" scenarios gracefully?
- [ ] Is `SyncWorker` used for critical updates?
- [ ] Are coroutines launched in the correct Dispatcher (IO/Main)?

## 6. UI/UX
- [ ] Does it match Material 3 guidelines?
- [ ] Are Loading, Error, and Empty states implemented?
- [ ] Does it look correct in Dark Mode?

## 7. Quality
- [ ] Are Unit Tests included for new UseCases/ViewModels?
- [ ] Is the build successful?
- [ ] Are there any severe Lint warnings?

---
*Derived from SDMS Backend Docs: PROJECT_RULE.md, DoD section*
