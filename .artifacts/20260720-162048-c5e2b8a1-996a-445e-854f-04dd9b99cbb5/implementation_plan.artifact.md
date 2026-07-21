# Implementation Plan - Face Approval UI/UX Redesign

Redesign the Face Approval screen for the Admin app to provide a more professional, intuitive, and modern user experience. The logic remains unchanged, focusing solely on the presentation layer.

## Proposed Changes

### [Admin Face Approval]

Redesign the approval screen and card components to follow modern Material 3 guidelines and improve visual hierarchy.

#### [FaceApprovalScreen.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/admin/face/presentation/FaceApprovalScreen.kt)

- **Top Bar**: Use `CenterAlignedTopAppBar` for a cleaner look. Replace "←" with `Icons.AutoMirrored.Filled.ArrowBack`. Replace "Tải lại" text with `Icons.Default.Refresh`.
- **Empty State**: Replace the simple text with the `EmptyView` component for consistency.
- **Loading Overlay**: Replace the semi-transparent black overlay with a more subtle loading state or the standard `LoadingView`.
- **FaceProfileCard**:
    - **Visuals**: Use a circular or rounded square mask for the face image with a subtle border.
    - **Hierarchy**: Improve text styling (bold titles, secondary colors for metadata).
    - **Interaction**: Add status chips (e.g., "PENDING") with appropriate colors.
    - **Actions**: Use clear, distinct icons for Approve (Check) and Reject (Close). Apply `FilledTonalButton` for "Approve" and `OutlinedButton` with error colors for "Reject".
- **Rejection Dialog**: Add validation feedback to the `OutlinedTextField` and use more descriptive titles.

---

### [UI Components]

Ensure shared components are utilized for a unified look.

#### [CommonStates.kt](file:///D:/HocTap/LuanVan/Code/app/src/main/java/com/ktx/dormitory/ui/components/CommonStates.kt)

- Utilize `LoadingView`, `EmptyView`, and `ErrorView` already present in the codebase.

## Verification Plan

### Manual Verification
- **Visual Inspection**: Use the IDE preview (if possible) or code analysis to verify the new layout structure.
- **Workflow Check**:
    1. Open Face Approval.
    2. Verify the card layout (Image alignment, font sizes).
    3. Click "Reject" -> Verify the dialog's new styling.
    4. Click "Approve" -> Verify the immediate feedback and loading state.
    5. Test "Refresh" button in the TopBar.
