# UI State Machines (Comprehensive Inventory)

This document describes how complex business processes and entity statuses translate into visual UI states in the Android application.

## 1. Face Profile & Liveness (BR-I01)
### Lifecycle Status
| Status | UI Visual State | Action Guard |
| :--- | :--- | :--- |
| **NONE** | "Start Registration" | Enable registration button |
| **PENDING** | Yellow Warning | Disable registration; Show info |
| **APPROVED** | Green Active | Show photo; Enable "Request Update" |
| **REJECTED** | Red Error | Show reason; Enable "Retry" |

### Liveness State Machine
Sequence enforced during face capture:
1.  **EYE_BLINK**: Detect probability < 0.2
2.  **TURN_LEFT**: Euler Y > 25 degrees
3.  **TURN_RIGHT**: Euler Y < -25 degrees
4.  **SMILE**: Probability > 0.7
5.  **COMPLETED**: Trigger API call

## 2. Residency Request Lifecycle
Applies to Room Transfer and Stay Extension.

| Status | Color | User Interaction |
| :--- | :--- | :--- |
| **PENDING** | Amber | View details; Action disabled |
| **APPROVED** | Green | Room info/Dates update automatically |
| **REJECTED** | Red | View Admin reason; Can resubmit |
| **CANCELLED**| Gray | Archived in history |

## 3. Bill & Payment Lifecycle (BR-R02)
| Status | UX Impact | Logic |
| :--- | :--- | :--- |
| **UNPAID** | Alert Card | Included in Total Debt |
| **OVERDUE** | Critical Red | Priority #1 in list; Blocks Checkout |
| **PAID** | Success Icon | Ignored by debt guards |
| **CANCELLED**| Strikethrough| Ignored |

## 4. Unified Access Timeline
Derived status from AI Face Verification + IoT Gate Logs.

| Result | Visual | Logic |
| :--- | :--- | :--- |
| **SUCCESS** | Green | AI Success AND Gate Granted |
| **ACCESS_DENIED**| Red | AI Success AND Gate Denied (Curfew) |
| **VERIFY_FAIL** | Amber | AI failed to recognize face |
| **UNKNOWN** | Gray | Desync or missing hardware log |

---
*Maintained for Thesis Documentation Standards.*
