# SDMS Android - AI Operating Manual
**Version: 6.0.0**

ĐÂY LÀ CẨM NANG VẬN HÀNH BẮT BUỘC dành cho mọi AI Agent. Mục tiêu: Tối ưu hóa ngữ cảnh (Context), bảo vệ kiến trúc, hỗ trợ viết Luận văn và đảm bảo Tài liệu luôn đồng bộ với Code.

---

## 1. Task Routing System

Hệ thống điều hướng tác vụ giúp giảm tải việc load ngữ cảnh không cần thiết và tăng hiệu suất phát triển. Agent PHẢI tự động xác định loại task và tài liệu cần đọc dựa trên yêu cầu của người dùng.

### 1.1. Luồng công việc tổng quát (General Pipeline)

Yêu cầu người dùng (User Request)
↓
Xác định loại tác vụ (Identify Task Type)
↓
Tìm tài liệu liên quan (Locate Related Documentation)
↓
Đọc tài liệu bắt buộc (Read Required Documents)
↓
Xác định mã nguồn liên quan (Locate Related Source Code)
↓
Phân tích (Analyze)
↓
Triển khai (Implementation)
↓
Xác minh (Verification)
↓
Đồng bộ hóa tài liệu (Documentation Synchronization)
↓
Cập nhật nhật ký Audit (Audit Update)
↓
Hoàn thành (Finish)

### 1.2. Bảng điều hướng tác vụ (Task Routing Table)

| Loại tác vụ | Tài liệu cần đọc | Thư mục Source cần quét |
| :--- | :--- | :--- |
| **Authentication** | `PROJECT_RULE.md`, `SECURITY_GUIDE.md`, `API_INTEGRATION_GUIDE.md`, `ARCHITECTURE_PRINCIPLES.md` | `presentation/features/auth/`, `domain/auth/`, `data/auth/` |
| **Student Features** | `PROJECT_RULE.md`, `docs/business/`, `API_INTEGRATION_GUIDE.md`, `CODING_STANDARD.md` | `presentation/features/student/`, `domain/`, `data/` |
| **Admin Features** | `PROJECT_RULE.md`, `ARCHITECTURE_PRINCIPLES.md`, `API_INTEGRATION_GUIDE.md`, `SECURITY_GUIDE.md`, Backend Docs (`docs/smart-dormitory-management-system-main/`) | `presentation/features/admin/`, `domain/`, `data/` |
| **API Integration** | `PROJECT_RULE.md`, `API_INTEGRATION_GUIDE.md`, Backend Docs | Retrofit, DTO, Mapper, Repository, UseCase, ViewModel liên quan |
| **Database** | `PROJECT_RULE.md`, `ARCHITECTURE_PRINCIPLES.md` | Room Entities, DAO, Repository, Offline Sync |
| **AI/ML & Processing** | `PROJECT_RULE.md`, `AI_INTEGRATION_GUIDE.md`, `docs/audit/07_PERFORMANCE_AUDIT.md` | `ai/`, `presentation/features/face/`, `core/util/ImageUtil.kt` |
| **Security** | `PROJECT_RULE.md`, `SECURITY_GUIDE.md` | Auth, JWT, RoleGuard, Interceptors |
| **Navigation** | `PROJECT_RULE.md`, `ARCHITECTURE_PRINCIPLES.md` | Navigation Graph, Route, Role Dispatcher |
| **Refactoring** | `PROJECT_RULE.md`, `ARCHITECTURE_PRINCIPLES.md`, `CODING_STANDARD.md`, `CODE_REVIEW_CHECKLIST.md`, Audit Reports | Toàn bộ module bị ảnh hưởng. Thực hiện RCA và Impact Analysis trước. |
| **Bug Fix** | `PROJECT_RULE.md`, Feature Docs liên quan, Backend API | Source code liên quan. Thực hiện RCA trước khi sửa. |
| **Code Review** | `PROJECT_RULE.md`, `CODE_REVIEW_CHECKLIST.md`, Architecture Guides | Review theo 7 tiêu chí: Arch, Logic, Security, Perf, Offline, Test, Maintainability. |

### 1.3. Điều hướng tài liệu Audit & Quality

| Loại Audit | Tài liệu cần đọc | Tài liệu cần cập nhật |
| :--- | :--- | :--- |
| **Architecture** | `PROJECT_RULE.md`, `README.md`, `DOCUMENTATION_INDEX.md`, All Arch Docs | `docs/audit/01_ARCHITECTURE_AUDIT.md`, `DECISION_LOG.md` |
| **Business** | Business Docs, Backend Docs | `docs/audit/02_BUSINESS_FLOW_AUDIT.md` |
| **Security** | `SECURITY_GUIDE.md`, Auth Source | `docs/audit/05_SECURITY_AUDIT.md`, `TECH_DEBT.md` |
| **Performance** | `ARCHITECTURE_PRINCIPLES.md`, Compose/Room/Repo Source | `docs/audit/07_PERFORMANCE_AUDIT.md` |
| **Testing** | Testing Guide, Test folders, ViewModels/Repos | `docs/audit/08_TESTING_AUDIT.md` |

---

## 2. Documentation Governance (BẮT BUỘC)

Một task CHỈ được coi là hoàn thành khi toàn bộ tài liệu bị ảnh hưởng đã được đồng bộ hóa. Tài liệu không bao giờ "xong", nó tiến hóa cùng mã nguồn.

### 2.1. Quy trình đồng bộ hóa (MANDATORY SYNC)
Sau mỗi task, Agent PHẢI thực hiện:
1. **Xác định tài liệu bị ảnh hưởng**: Dựa trên "Ma trận cập nhật tài liệu".
2. **Đồng bộ hóa**: Cập nhật nội dung tài liệu theo thực tế Code.
3. **Cập nhật Project Health**: Phản ánh trạng thái mới nhất vào `docs/PROJECT_HEALTH.md`.
4. **Cập nhật AUDIT_CHANGELOG.md**: Ghi lại lịch sử thay đổi (Date, Task, Docs affected).
5. **Kiểm tra tính nhất quán**: Đảm bảo không có link hỏng, không mâu thuẫn giữa các tài liệu.

### 2.2. Ma trận cập nhật tài liệu (Document Update Matrix)

| Thay đổi | Tài liệu cần cập nhật |
| :--- | :--- |
| **Authentication** | `API_INDEX.md`, `FEATURE_INDEX.md`, `PROJECT_HEALTH.md`, `AUDIT_CHANGELOG.md` |
| **Navigation** | `ARCHITECTURE_INDEX.md`, `FEATURE_INDEX.md`, `PROJECT_HEALTH.md` |
| **Repository** | `ARCHITECTURE_INDEX.md`, `PROJECT_HEALTH.md`, `TECH_DEBT.md` (nếu cần) |
| **API Interface** | `API_INDEX.md`, `PROJECT_HEALTH.md`, `README.md` (nếu API public) |
| **Business Rules** | Business Docs, `PROJECT_RULE.md`, `PROJECT_HEALTH.md` |
| **Security** | `SECURITY_GUIDE.md`, `PROJECT_RULE.md`, `PROJECT_HEALTH.md` |
| **Thêm Feature** | `FEATURE_INDEX.md`, `DOCUMENTATION_INDEX.md`, `PROJECT_HEALTH.md`, `README.md` |
| **Xóa Feature** | Đánh dấu `Deprecated` trong `FEATURE_INDEX.md`, `PROJECT_HEALTH.md` |
| **Architecture** | `ARCHITECTURE_INDEX.md`, `PROJECT_RULE.md`, `PROJECT_HEALTH.md` |
| **Hoàn thành Audit** | `AUDIT_CHANGELOG.md`, `TECH_DEBT.md`, `PROJECT_HEALTH.md` |

### 2.3. Chính sách cập nhật (Update Policy)
- **Append Only**: Luôn thêm mới, không ghi đè lịch sử (Audit, Changelog, Refactor).
- **Traceability**: Mỗi thay đổi tài liệu phải tham chiếu đến Feature/Audit/API liên quan.
- **Project Health SSOT**: `docs/PROJECT_HEALTH.md` phải luôn phản ánh trạng thái thực tế nhất.

### 2.5. Chữ ký xác thực hoàn tất (Mandatory Completion Signature)
BẮT BUỘC Agent phải xuất bảng sau ở cuối mỗi task có thay đổi code:

---
**AGENT SYNC VERIFICATION:**
- [ ] **Code Integrity**: Build/Logic verified?
- [ ] **Documentation**: List of updated files (e.g., `ROOM_AUDIT.md`, `API_INDEX.md`).
- [ ] **Project Health**: New Score & Trend.
- [ ] **Next Action**: What should be done next?
---

## 3. Tối ưu hóa ngữ cảnh (Context Optimization)

- **Minimization**: Luôn giảm thiểu việc sử dụng context. Chỉ đọc tài liệu và source code liên quan trực tiếp đến task.
- **No Global Scan**: Tuyệt đối không quét toàn bộ repository bằng các query chung chung như `.kt`, `.xml` hoặc `*`.
- **Tool Selection Strategy (BẮT BUỘC)**:
    1. **Tìm Symbol**: Sử dụng `find_declaration(symbol = "Name")` thay vì grep toàn bộ project.
    2. **Định vị File**: Sử dụng `list_files` theo thư mục tính năng đã xác định ở Task Routing. 
    3. **Tìm kiếm chính xác**: Chỉ dùng `find_files` với tên file cụ thể (ví dụ: `AppDatabase.kt`) khi `list_files` không thấy.
    4. **Token Safety**: Nếu một file/class quan trọng được reference trong code nhưng không tìm thấy sau 2 bước trên, hãy đối chiếu `D:\HocTap\LuanVan\Code\app\src\structure.txt` và tự khởi tạo lại dựa trên kiến trúc (Clean Arch) thay vì quét diện rộng.
- **Knowledge Priority**:
    1. `PROJECT_RULE.md` (Hiến pháp).
    2. `README.md` (Navigation Hub).
    3. `docs/DOCUMENTATION_INDEX.md` (Master Map).
    4. `AGENT.md` (Workflow).
    5. Các tài liệu kỹ thuật chi tiết.
    6. **Source Code** (Sự thật cuối cùng).

---

## 4. Academic & Thesis Support (Hỗ trợ Luận văn)

Vì đây là dự án Đồ án tốt nghiệp, Agent phải tuân thủ các quy tắc bổ sung sau:

1. **Reasoning Transparency**: Khi thực hiện các thay đổi kiến trúc quan trọng (ví dụ: SQLCipher, MVI), Agent phải giải thích ngắn gọn "Tại sao" (Rationale) dựa trên các nguyên lý kỹ thuật.
2. **Trade-off Analysis**: Luôn đề cập đến sự đánh đổi khi chọn một giải pháp. Ví dụ: "Dùng SQLCipher tăng tính bảo mật nhưng làm tăng kích thước APK và tốn tài nguyên hơn một chút".
3. **Comment for Thesis**: Viết code comment rõ ràng ở các phần logic phức tạp (AI processing, MVI State machine) để người dùng dễ dàng giải trình trong báo cáo.
4. **Decision Logging**: Mọi thay đổi thư viện hoặc design pattern PHẢI được ghi vào `DECISION_LOG.md` (hoặc tương đương) để làm tư liệu truy vết.

## 5. Checklist hoàn thành (Pre-Completion Checklist)

- [ ] Build thành công (không lỗi compile).
- [ ] Test thành công (nếu có).
- [ ] Tài liệu kỹ thuật đã được đồng bộ (Sync).
- [ ] `AUDIT_CHANGELOG.md` đã được cập nhật.
- [ ] `TECH_DEBT.md` / `REFACTOR_HISTORY.md` đã được cập nhật (nếu cần).
- [ ] Các liên kết chéo (Cross-references) đã được kiểm tra (Relative paths).

---

*Hệ thống điều hướng tác vụ đã được kích hoạt. Tuân thủ tuyệt đối để bảo vệ dự án.*
