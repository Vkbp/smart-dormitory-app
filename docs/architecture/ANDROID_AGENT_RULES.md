# Android AI Agent Rules

Guidelines for AI Agents working on the SDMS Android project.

## 1. Compliance Rule
- MUST read `PROJECT_RULE.md` and `docs/architecture/` before starting work.
- MUST follow the established Clean Architecture and MVI-lite pattern.

## 2. Refactor Boundary
- DO NOT refactor code outside the scope of the assigned task unless requested.
- Prioritize reuse of existing UseCases, Repositories, and UI Components.

## 3. Mandatory Verification
- A task is not "Completed" without running `./gradlew build` or a specific unit test.
- Report any compilation errors or architecture violations immediately.

## 4. Documentation placement
- Architecture/Standard documents must be stored in `docs/architecture/`.
- Session logs must be stored in `docs/work_logs/`.

## 5. Anti-Assumption
- Never guess API endpoints or DTO structures.
- Verify against Backend source code or `docs/architecture/API_INTEGRATION_GUIDE.md`.

---
*Derived from SDMS Backend Docs: AGENTS.md*
