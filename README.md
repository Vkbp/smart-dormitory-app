# SDMS Android: Smart Dormitory Management System

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/kotlin-2.0.21-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-5.0%2B-green.svg?logo=android)](https://developer.android.com)

**SDMS Android** is the official mobile client for the Smart Dormitory Management System. Built with modern Android technologies, it provides a seamless experience for both students and administrators to manage dormitory activities, access control, and security.

---

## ✨ Key Features

### 👨‍🎓 For Students
- **Smart Access**: QR-based check-in and IoT-integrated door unlocking.
- **Curfew Management**: Submit and track late-entry requests.
- **Identity**: Manage face recognition profiles and RFID credentials.
- **Notifications**: Real-time alerts for dormitory announcements.

### 👮 For Administrators
- **Remote Unlock**: Emergency door override and remote gate control.
- **User Management**: Efficient student onboarding and RFID assignment.
- **Security Logs**: Real-time monitoring of access logs and AI verification events.
- **System Health**: Monitor IoT connectivity and database status.

---

## 🏗️ Technical Stack

- **UI**: Jetpack Compose (Material 3)
- **Architecture**: Clean Architecture + MVI (Model-View-Intent)
- **Dependency Injection**: Hilt
- **Local Storage**: Room + SQLCipher (Encrypted)
- **Networking**: Retrofit + OkHttp (with SSL Pinning support)
- **AI/ML**: ML Kit (Face Detection) + TensorFlow Lite
- **Async**: Kotlin Coroutines & Flow

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1) or newer.
- JDK 17.
- Android device or emulator (API 24+).

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/sdms-android.git
   ```
2. Open the project in Android Studio.
3. Create a `local.properties` file in the root directory (if it doesn't exist) and set your API base URL:
   ```properties
   BASE_URL=https://your-api-server.com/api/
   ```
4. Build and Run.

---

## 📐 Documentation Hub
We follow a **Documentation-First** approach. For deep dives into architecture, security, and business rules, visit our internal hub:

- [Current System Health](./docs/PROJECT_HEALTH.md) (**Maturity: 89/100**)
- [Master Documentation Index](./docs/DOCUMENTATION_INDEX.md)
- [Architecture Principles](./docs/architecture/ARCHITECTURE_PRINCIPLES.md)
- [Security Guide](./docs/architecture/SECURITY_GUIDE.md)

---

## 🤝 Contributing
Contributions are welcome! Please read our [Contributing Guide](CONTRIBUTING.md) and [Code of Conduct](CODE_OF_CONDUCT.md) before submitting a pull request.

---

## ⚖️ License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
*Developed as part of the SDMS ecosystem.*
