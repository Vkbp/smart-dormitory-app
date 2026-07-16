You are acting as

- Senior Software Architect
- Senior Android Engineer
- Technical Lead
- Software Security Reviewer
- System Integration Auditor

==================================================
OBJECTIVE
==================================================

The project already contains a complete audit documentation under

docs/audit/

including

01_ARCHITECTURE_AUDIT.md

02_BUSINESS_FLOW_AUDIT.md

03_API_AUDIT.md

04_DATABASE_OFFLINE_AUDIT.md

05_SECURITY_AUDIT.md

06_UI_UX_AUDIT.md

07_PERFORMANCE_AUDIT.md

08_TESTING_AUDIT.md

09_PRODUCTION_READINESS.md

10_FINAL_SYSTEM_REPORT.md

Your task is NOT to recreate these documents.

Your task is to synchronize them with the current source code.

Treat every audit document as a Living Document.

==================================================
READ FIRST
==================================================

Read

PROJECT_RULE.md

AGENT.md

README.md

TECH_DEBT.md

REFACTOR_HISTORY.md

Previous audit reports

Relevant documentation

Current source code

==================================================
RULES
==================================================

Never overwrite reports blindly.

Keep previous findings if they are still valid.

Update findings if implementation has changed.

Remove findings that are no longer true.

Add newly discovered issues.

Keep document history.

Never fabricate issues.

Every conclusion must be supported by source code.

==================================================
UPDATE STRATEGY
==================================================

For every audit document

Perform

Compare

↓

Current Source Code

↓

Previous Audit

↓

Determine

Still Valid

Fixed

Regression

New Issue

Deprecated

**Cross-Sync (MANDATORY)**

If an audit reveals a change in Architecture, API, Security, or Business logic:

↓

Update the corresponding Guide/Index:

- `docs/architecture/API_INTEGRATION_GUIDE.md`

- `docs/architecture/SECURITY_GUIDE.md`

- `docs/BUSINESS_INDEX.md`

- `docs/FEATURE_INDEX.md`

- `docs/audit/DECISION_LOG.md` (if a new ADR is found)

For every finding

add one of the following status

✔ Still Valid

✔ Fixed

✔ Improved

✔ New

✔ Deprecated

==================================================
SCORING RULE
==================================================

If a critical or high-priority issue (P0/P1) is marked as ✔ Fixed or ✔ Improved:

1. Re-calculate the specific module score.

2. Update the trend in 10_FINAL_SYSTEM_REPORT.md.

3. If scores decrease due to new issues (Regression), highlight with ⚠ CAUTION.

==================================================
UPDATE
==================================================

Update

01_ARCHITECTURE_AUDIT.md

Review

Package

Architecture

DI

Navigation

Feature Structure

Role Structure

Dependency Rule

MVI Pattern Compliance

Layer Bypass Checking

==================================================

Update

02_BUSINESS_FLOW_AUDIT.md

Review

Every Student Flow

Every Admin Flow

Business Rules

Missing Logic

==================================================

Update

03_API_AUDIT.md

Review

Endpoints

DTO

Mapper

Repository

UseCase

ViewModel

==================================================

Update

04_DATABASE_OFFLINE_AUDIT.md

Review

Room

DAO

Migration

Offline

Sync

==================================================

Update

05_SECURITY_AUDIT.md

Review

JWT

Refresh

Role

Encryption

Authorization

Authentication

Certificate Pinning

Database Encryption (SQLCipher)

==================================================

Update

06_UI_UX_AUDIT.md

Review

Compose

Material 3

Loading

Error

Navigation

Dark Mode

==================================================

Update

07_PERFORMANCE_AUDIT.md

Review

Compose

Memory

Coroutine

Room

Retrofit

LazyColumn Keys

Bitmap Allocation Optimization

==================================================

Update

08_TESTING_AUDIT.md

Review

Unit Test

Repository Test

ViewModel Test

Coverage

==================================================

Update

09_PRODUCTION_READINESS.md

Review

Crash

Analytics

Release

Baseline Profile

CI/CD

==================================================

Finally

Update

10_FINAL_SYSTEM_REPORT.md

using all previous reports.

==================================================
CHANGE LOG
==================================================

For every report

Append a section

## Audit History

Date

Summary

Files Changed

Modules Reviewed

Issues Fixed

New Issues

Architecture Impact

Security Impact

Business Impact

==================================================
FINAL SUMMARY
==================================================

Generate

docs/audit/AUDIT_REFRESH_REPORT.md

Include

Audit Date

Audited Modules

Reports Updated

**Non-Audit Docs Synced** (List of guides/indexes updated)

New Findings

Resolved Findings

Regression

Overall Score Comparison

Previous Overall Score

Current Overall Score

Improvement Trend

Recommendations

==================================================
QUALITY REQUIREMENTS
==================================================

Never recreate reports.

Always preserve history.

Treat every audit report as a living document.

Never lose previous audit knowledge.

Only update what has changed.

Everything must be verified against source code.

Documentation synchronization is mandatory before finishing.