# SDMS Development Guide (Unified)

Standard development practices for the entire SDMS project (Android & Backend).

## 1. Git Workflow
- **Branching**: 
    - `main`: Production-ready code.
    - `develop`: Integration branch.
    - `feature/*`: Functional changes.
    - `fix/*`: Bug fixes.
- **Commits**: Follow [Conventional Commits](https://www.conventionalcommits.org/):
    - `feat(admin): add remote unlock screen`
    - `fix(auth): resolve refresh token race condition`

## 2. Documentation synchronization
- Implementation is NOT complete until relevant documentation is updated (API spec, Business Rule, etc.).
- Source of truth for business: `docs/business/`.

## 3. Communication Standards
- API changes must be discussed between Android and Backend leads.
- DTO names must be consistent across both platforms.

## 4. Testing Mindset
- "If it's not tested, it's broken."
- Prioritize testing complex business logic (Domain layer) and critical paths (Auth, Payment).

## 5. Deployment Readiness
- Ensure environment variables are correctly configured.
- Verify logging levels (No DEBUG in production).
- Perform a final cross-module audit (Frontend vs Backend compatibility).

---
*Derived from SDMS Backend Docs: AGENTS.md, README.md*
