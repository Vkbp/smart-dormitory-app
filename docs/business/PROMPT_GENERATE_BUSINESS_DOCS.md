# Prompt: Generate & Sync Business Documentation

**Objective**: Initialize and synchronize the `docs/business/` folder by mapping Backend Business Rules to Android-specific implementation guards and UX behaviors.

---

## 1. Context & Inputs
- **Backend SSOT**: `D:/HocTap/LuanVan/Code/docs/smart-dormitory-management-system-main/docs/business/BUSINESS_RULES.md`
- **Android Source**: `app/src/main/java/com/ktx/dormitory/`
- **Current Index**: `docs/BUSINESS_INDEX.md`

## 2. Mandatory Files to Generate

### A. [NEW] `docs/business/README.md`
- **Purpose**: Explain the relationship between Backend Rules and Mobile Guards.
- **Structure**:
    - Introduction to Business Logic Synchronization.
    - Reference to Backend SSOT.
    - Navigation to Mobile Business documents.

### B. [NEW] `docs/business/BUSINESS_RULE_MAPPING.md`
- **Core Content**: A detailed technical mapping table.
- **Columns**:
    - **Rule ID**: (e.g., BR-R02).
    - **Backend Logic**: Short summary of the server-side constraint.
    - **Mobile Guard/UX**: How the Android app enforces this (UI state, Input validation, Interceptors).
    - **Implementation**: Specific class/file paths (e.g., `CheckoutViewModel.kt`).
    - **Status**: (Verified / In-Progress / Missing).

### C. [NEW] `docs/business/UI_STATE_MACHINES.md`
- **Context**: Map entity statuses (from Backend `STATE_MACHINES.md`) to UI Visual States.
- **Examples**:
    - `FaceProfileStatus` (PENDING -> APPROVED) -> `FaceRegistrationScreen` visual transitions.
    - `BillStatus` (UNPAID -> OVERDUE) -> Color coding and priority in `InvoiceList`.

### D. [NEW] `docs/business/VALIDATION_SPECIFICATION.md`
- **Content**: Detailed specs for client-side validation logic.
- **Includes**: Regex patterns, field constraints, and error messages that match Backend requirements (BR-A02, BR-U01).

---

## 3. Execution Instructions (For AI Agent)

1.  **Phase 1: Analysis**
    - Read the Backend `BUSINESS_RULES.md` and identify all rules applicable to the Student and Admin mobile flows.
    - Scan the Android source code to find where these rules are currently guarded.

2.  **Phase 2: Generation**
    - Create the 4 files mentioned above.
    - Ensure all links are relative paths.
    - Use GitHub-style alerts for critical business constraints.

3.  **Phase 3: Synchronization**
    - Update `docs/DOCUMENTATION_INDEX.md` to include these new files.
    - Update `docs/README.md` hub.

## 4. Quality Requirements
- **Evidence-Based**: Every mapping must point to real source code.
- **Thesis-Ready**: Use professional terminology (Business Rules, UX Guard, State Transition).
- **Concise**: Focus on technical implementation details, not general descriptions.
