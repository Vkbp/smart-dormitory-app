# Security Guide - SDMS Android

This guide defines the security standards and authentication flows for the Android application.

## 1. Authentication Flow
SDMS uses JWT-based authentication with Access and Refresh tokens.

### JWT Handling
- **Access Token**: Short-lived (15 mins), included in all API requests via `AuthInterceptor`.
- **Refresh Token**: Long-lived (7 days), used to obtain new access tokens automatically via `TokenAuthenticator`.
- **Storage**: Tokens must be stored in `EncryptedSharedPreferences` (AES256_GCM) or `DataStore` with encryption.

### Biometric Authentication
- **Standard Flow**: Biometric Success -> Trigger Refresh Token (Manual) -> Navigate Home.
- **Requirement**: Biometric must be prompted at `SplashScreen` if enabled by the user.

## 2. Role-Based Access Control (RBAC)
The app uses **Nested Graphs** and **RoleGuards** to protect routes.
- **Roles**: `STUDENT`, `ADMIN`.
- **Guard**: The `LoginViewModel` stores the user's role. Navigation logic ensures users cannot access graphs belonging to other roles.

## 3. IDOR Protection (Client-side)
In alignment with backend rule **BR-S01**, the Android app should:
- Never send `userId` or `studentId` in payloads for personal operations.
- Rely on the Backend to extract identity from the JWT.

## 4. Secure Communication
- **HTTPS**: All communication with the Spring Boot API must use TLS 1.2+.
- **Certificate Pinning**: Implemented via `CertificatePinner` in `NetworkModule`. (Note: Currently using placeholder pins for development).

## 5. Input Sanitization & Validation
- **Regex Validation**: Client-side validation for passwords (BR-A02) and CCCD numbers before submission.
- **Sensitive Data**: Never log passwords, tokens, or personal identifiers in logcat.

## 6. Secure Storage
- **Room Database**: Encrypted using **SQLCipher**. A passphrase-based `SupportFactory` is used in `DatabaseModule` to secure the database file.
- **Root Detection**: (Future) Implementation recommended to prevent data extraction on compromised devices.

## 🔗 Liên kết tài liệu (Related Documents)
- **Primary Standard**: [PROJECT_RULE.md](../../PROJECT_RULE.md)
- **API Security**: [API Integration Guide](./API_INTEGRATION_GUIDE.md)
- **Business Logic**: [Business Index](../BUSINESS_INDEX.md)

---
*Derived from SDMS Backend Docs: application.yml, PROJECT_RULE.md, BUSINESS_RULES.md*
