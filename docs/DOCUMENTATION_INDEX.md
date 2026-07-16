# SDMS Master Documentation Index

This is the master navigation document for the Smart Dormitory Management System (SDMS) Android project. It tracks the purpose, ownership, and maintenance rules for all documentation.

## 🏛️ Core Governance
| Document | Purpose | Owner | Update Trigger | Dependencies |
| :--- | :--- | :--- | :--- | :--- |
| [PROJECT_HEALTH.md](./PROJECT_HEALTH.md) | Living document for system maturity | Tech Lead | Post-audit sync | None |
| [PROJECT_RULE.md](../PROJECT_RULE.md) | Supreme laws of the project | Architect | Permanent architectural change | None |
| [AGENT.md](../.agents/AGENT.md) | AI operating manual & workflow | AI Lead | Workflow / Policy change | PROJECT_RULE |
| [README.md](../README.md) | High-level navigation hub | Tech Lead | New documentation module | DOCUMENTATION_INDEX |

## 📐 Architecture & Standards
| Document | Purpose | Owner | Update Trigger |
| :--- | :--- | :--- | :--- |
| [Architecture](./architecture/ARCHITECTURE_PRINCIPLES.md) | Clean Arch & MVI principles | Architect | Structural refactor |
| [API Guide](./architecture/API_INTEGRATION_GUIDE.md) | Backend communication rules | Tech Lead | New API module / Interceptor |
| [Security Guide](./architecture/SECURITY_GUIDE.md) | Auth, JWT, Biometric, Encr | Security Lead | New security feature |
| [Coding Standard](./architecture/CODING_STANDARD.md) | Naming & UI standards | Tech Lead | UI/Code style update |
| [API Index](./API_INDEX.md) | Feature-to-API mapping | Developer | API change |

## 💼 Business & Features
| Document | Purpose | Owner | Update Trigger |
| :--- | :--- | :--- | :--- |
| [Business Rules](./BUSINESS_INDEX.md) | Android logic enforcement | Product Owner | Logic change |
| [Feature Index](./FEATURE_INDEX.md) | Functional decomposition | Product Owner | New feature added |
| [Package Index](./PACKAGE_INDEX.md) | Technical folder mapping | Developer | Package refactor |

## 💼 Business Logic & Guards
| Document | Purpose | Owner | Update Trigger |
| :--- | :--- | :--- | :--- |
| [Business README](./business/README.md) | Entry point for business logic | Product Owner | Documentation restructure |
| [Rule Mapping](./business/BUSINESS_RULE_MAPPING.md) | Backend vs Mobile enforcement | Tech Lead | Logic change |
| [UI State Machines](./business/UI_STATE_MACHINES.md) | Visual state transitions | UI/UX Lead | Status enum change |
| [Validation Specs](./business/VALIDATION_SPECIFICATION.md) | Regex & Input constraints | Developer | Constraint change |

## 🛡️ Quality & Audit (Living Documents)
| Document | Purpose | Owner | Update Trigger |
| :--- | :--- | :--- | :--- |
| [Final System Report](./audit/10_FINAL_SYSTEM_REPORT.md) | Definitive system status | Architect | Post-audit synthesis |
| [Module Audits](./audit/modules/) | Individual module audit reports | QA Lead | Feature-specific audit |
| [Audit Changelog](./audit/AUDIT_CHANGELOG.md) | Permanent audit history | QA Lead | Any new audit task |
| [GitHub Readiness](./audit/GITHUB_REPOSITORY_AUDIT.md) | GitHub publication prep | Architect | Pre-release audit |
| [Governance Report](./DOCUMENTATION_GOVERNANCE_REPORT.md) | Documentation sync report | AI Lead | Post-governance activation |
| [Technical Debt](./audit/TECH_DEBT.md) | Tracking unresolved issues | Architect | New debt identified |
| [Health Changelog](./dashboard/PROJECT_HEALTH_CHANGELOG.md) | History of system maturity | Tech Lead | Dashboard update |
| [Refactor History](./audit/REFACTOR_HISTORY.md) | Major structural changes | Architect | Significant refactor task |
| [Decision Log (ADR)](./audit/DECISION_LOG.md) | Architectural Decision Records | Architect | Key design choice made |

## 📝 Logs & History
- [Work Logs Directory](./work_logs/) - Historical session reports.
- [Implementation Reports](./implementation/) - Feature implementation details.

---
*Maintained by the Documentation Governance System.*
