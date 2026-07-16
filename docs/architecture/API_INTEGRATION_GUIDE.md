# API Integration Guide - SDMS Android

Standard conventions for integrating the Android client with the SDMS Spring Boot Backend.

## 1. Response Handling
All API responses follow a unified wrapper format (`BaseResponse<T>`):
```json
{
  "success": true,
  "message": "Success message",
  "errorCode": null,
  "data": { ... }
}
```
- **Paginated Responses**: Use `PageResponse<T>` which includes `content`, `pageNumber`, `totalElements`, etc.

## 2. DTO and Mapping
- **Data Layer**: Retrofit interfaces use DTO classes (e.g., `FaceProfileDto`).
- **Mapping**: DTOs MUST be mapped to Domain Models in the Data layer using extension functions.
- **Strict Typing**: Avoid using `Map` or `Any` for API payloads.

## 3. Error Mapping
- **HTTP 400**: Validation error (Business Rule violation).
- **HTTP 401**: Unauthorized (Token expired/Invalid).
- **HTTP 403**: Forbidden (Insufficient role).
- **HTTP 415**: Unsupported Media Type (BR-U01 - non-image upload).
- **HTTP 500**: Internal Server Error.

> [!TIP]
> The `AuthInterceptor` should handle 401/403 errors globally to trigger logout or token refresh.

## 4. Common API Rules
- **Base URL**: Configured dynamically via `local.properties` (Build Variant dependent).
- **Idempotency**: Critical IoT or Payment requests must include an `eventId` (UUID) to prevent double processing (BR-S04).
- **Timeouts**: 
    - Standard: 30s
    - Image Upload: 60s
    - IoT Remote Unlock: 10s (High responsiveness required).

## 5. Offline Synchronization
- **SyncWorker**: Use WorkManager to retry failed critical requests (Payment Verify, Profile Update).
- **Conflict Resolution**: Last-write-wins strategy for profile updates.

## 6. Feature Specific API Guides

### Student RFID Assignment
- **Endpoint**: `POST /api/v1/students/{studentId}/rfid`
- **Purpose**: Assigns a physical RFID tag (Mifare Classic/Desfire) to a student.
- **Parameters**: `rfidCode` (Query, String).
- **UseCase**: `AssignRfidUseCase` in Admin features.
- **Behavior**: Used during the Check-in process to link a physical card to the student profile.

### Curfew & Late Entry Requests
- **Endpoints**:
    - `POST /api/v1/curfew-requests`: Submit a new late entry request.
    - `GET /api/v1/curfew-requests/me`: Retrieve student's request history.
- **Payload**: `reason`, `expectedArrivalTime`, `note`.
- **Rules**: Used to bypass physical gate lockdown when approved by Admin.

### Smart Access Control
- **Endpoints**:
    - `POST /api/v1/access/gates/{gateId}/unlock`: Remote unlock a specific gate/room.
    - `POST /api/v1/access/emergency`: Emergency broadcast to open all doors in a building or the entire campus.
- **Parameters**: 
    - `buildingId` (Query, UUID) for both endpoints.
    - `actionType` (Query, String) and `reason` (Query, String) for emergency.
- **Rules**: 
    - IoT Remote Unlock requires high responsiveness. Timeout is set to **10s**.
    - Emergency operations are logged with the provided reason for audit purposes.

## 🔗 Liên kết tài liệu (Related Documents)
- **API Map**: [API Index](../API_INDEX.md)
- **Security**: [Security Guide](./SECURITY_GUIDE.md)
- **Standards**: [Coding Standard](./CODING_STANDARD.md)

---
*Derived from SDMS Backend Docs: application.yml, BUSINESS_RULES.md, PROJECT_RULE.md*
