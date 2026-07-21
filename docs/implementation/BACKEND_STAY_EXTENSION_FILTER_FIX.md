# Backend Requirement: Stay Extension Status Filtering

## Issue Description
The `GET /api/v1/admin/extensions` endpoint currently returns ALL stay extension records regardless of the `status` query parameter. 
- **Mobile Side Effect**: The dashboard shows a total count (e.g., 3) while the list screen is empty (due to client-side filtering).
- **Scalability Risk**: As the number of residents increases, the mobile app will fetch thousands of processed records just to find a few pending ones, causing high memory usage and data waste.

## Required Backend Changes

### 1. Update Repository
File: `StayExtensionRepository.java`

```java
// Thêm phương thức tìm kiếm theo status
Page<StayExtension> findAllByStatus(ExtensionStatus status, Pageable pageable);
```

### 2. Update Service
File: `StayExtensionService.java`

```java
@Transactional(readOnly = true)
public PageResponse<StayExtensionResponse> getAllExtensions(ExtensionStatus status, Pageable pageable) {
    Page<StayExtension> page;
    if (status != null) {
        page = stayExtensionRepository.findAllByStatus(status, pageable);
    } else {
        page = stayExtensionRepository.findAll(pageable);
    }
    
    List<StayExtensionResponse> content = page.getContent().stream()
            .map(this::buildResponse)
            .collect(Collectors.toList());
    return PageResponse.fromPage(page, content);
}
```

### 3. Update Controller
File: `StayExtensionAdminController.java`

```java
@GetMapping
public ResponseEntity<ApiResponse<PageResponse<StayExtensionResponse>>> getAllExtensions(
        @RequestParam(required = false) ExtensionStatus status, // Thêm param này
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
) {
    Pageable pageable = PageRequest.of(page, size);
    PageResponse<StayExtensionResponse> response = stayExtensionService.getAllExtensions(status, pageable);
    return ResponseEntity.ok(
            ApiResponse.success("Lấy danh sách đơn gia hạn thành công", response)
    );
}
```

## Impact
Once these changes are applied, the Mobile app's `totalElements` will correctly reflect the number of **Pending** requests, and the dashboard will show "0" correctly when no actions are required.
