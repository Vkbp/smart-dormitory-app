# Technical Knowledge Extraction Report - SDMS Android

## 1. Overview
This report summarizes the process of extracting technical knowledge from the SDMS Backend documentation repository and transforming it into an Android-focused knowledge base.

## 2. Documents Analyzed
The following source documents from `docs/smart-dormitory-management-system-main/` were analyzed:
- `sdms-frontend/PROJECT_RULE.md`
- `docs/business/BUSINESS_RULES.md`
- `sdms-backend/src/main/resources/application.yml`
- `.agents/AGENTS.md`
- `.agents/MENTOR_WAKEUP_PROMPT.md`
- `README.md` (Root)

## 3. Extraction Summary

### Rules Extracted (Converted to Android Context)
- **Governance Hierarchy**: Prioritizing Business Docs over implementation.
- **Single Source of Truth**: Data flow through Repositories and Room caching.
- **MVI Pattern**: Adapted from React Hooks/State logic to Android MVI-lite.
- **Business Rules Enforcement**: BR-R02 (Debt check), BR-A02 (Password complexity), BR-S01 (IDOR protection).
- **JWT & Auth Flow**: Short-lived access tokens, automatic refresh via interceptor.
- **Idempotency**: Using `eventId` for IoT operations.

### Rules Discarded (Backend-specific)
- PostgreSQL configuration and driver settings.
- JPA/Hibernate properties and Flyway migration steps.
- Spring Boot DevTools and Actuator setup.
- Nginx and Frontend-specific deployment rules.

### Conflicts Resolved
- **DTO Naming**: Backend docs sometimes used slightly different names than actual code. Verified against source code and prioritized the actual code implementation in `API_INTEGRATION_GUIDE.md`.
- **Response Format**: Merged different descriptions of the unified API wrapper into a single `BaseResponse<T>` standard.

## 4. Output Files Created
The following files were created in `docs/architecture/`:
1. `ANDROID_AGENT_RULES.md`
2. `SDMS_DEVELOPMENT_GUIDE.md`
3. `ARCHITECTURE_PRINCIPLES.md`
4. `SECURITY_GUIDE.md`
5. `API_INTEGRATION_GUIDE.md`
6. `CODING_STANDARD.md`
7. `CODE_REVIEW_CHECKLIST.md`

## 5. Final Recommendations
- **Maintain Sync**: Periodically re-audit `docs/business/BUSINESS_RULES.md` in the backend repo to ensure the Android client enforces the latest logic.
- **Hilt Modules**: Ensure every new feature follows the Hilt module pattern established in `ARCHITECTURE_PRINCIPLES.md`.
- **Dark Mode**: Add Dark Mode verification to the `CODE_REVIEW_CHECKLIST.md` as part of Material 3 compliance.
