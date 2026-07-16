# Business Logic & Mobile Guards

## Overview
This directory documents how the **Smart Dormitory Management System (SDMS)** business rules, defined in the Backend Single Source of Truth (SSOT), are enforced and reflected within the Android application.

While the Backend is the final authority for all business logic, the Android app implements **UX Guards**, **Client-side Validation**, and **State Management** to provide a seamless and compliant user experience.

## Relationship with Backend SSOT
- **Backend Rules**: [BUSINESS_RULES.md](../smart-dormitory-management-system-main/docs/business/BUSINESS_RULES.md)
- **Mobile Mapping**: [BUSINESS_RULE_MAPPING.md](./BUSINESS_RULE_MAPPING.md)

## Key Documents
1. **[BUSINESS_RULE_MAPPING.md](./BUSINESS_RULE_MAPPING.md)**: A technical table mapping Backend Rule IDs to Android implementation classes (ViewModels/Screens).
2. **[UI_STATE_MACHINES.md](./UI_STATE_MACHINES.md)**: Visual representation of entity status transitions and their corresponding UI states.
3. **[VALIDATION_SPECIFICATION.md](./VALIDATION_SPECIFICATION.md)**: Detailed specifications for input validation (Regex, constraints) matching backend requirements.

---
> [!IMPORTANT]
> Any change to the Backend business logic MUST be reviewed for impact on the Android application's UI guards and validation logic.
