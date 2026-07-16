# PROMPT: CẬP NHẬT HIẾN PHÁP DỰ ÁN (PROJECT_RULE.MD)

## Mục tiêu
Đảm bảo `PROJECT_RULE.md` luôn là Single Source of Truth (SSOT), phản ánh chính xác kiến trúc, nghiệp vụ và quy trình hiện tại sau các đợt Refactor hoặc thêm Feature mới.

## Quy trình thực hiện (Workflow)

1. **Thu thập ngữ cảnh (Context Gathering):**
   * Đọc `docs/audit/AUDIT_CHANGELOG.md` và `docs/audit/DECISION_LOG.md` để nắm bắt các thay đổi mới nhất đã được ghi nhận.
   * Quét các thư mục module liên quan đến thay đổi (theo Task Routing trong `AGENT.md`).

2. **Phân tích và So sánh:**
   * So sánh trạng thái thực tế của Source Code với các quy tắc hiện tại trong `PROJECT_RULE.md`.
   * Kiểm tra các thay đổi về:
     * **Module/Package:** Cấu trúc thư mục mới (Đối chiếu với `docs/PACKAGE_INDEX.md`).
     * **Kiến trúc:** Sự thay đổi trong MVI, Clean Arch hoặc DI (Hilt).
     * **Business Rules:** Các quy tắc nghiệp vụ mới.
     * **API & Security:** Endpoint mới, cơ chế bảo mật mới (SQLCipher, JWT).
     * **Technology Stack:** Thư viện mới hoặc cập nhật version quan trọng.

3. **Cập nhật tài liệu:**
   * **Chỉ cập nhật phần bị thay đổi.**
   * **Bổ sung Rationale (Lý do):** Với các thay đổi kiến trúc, hãy ghi chú ngắn gọn lý do kỹ thuật (để hỗ trợ viết Luận văn).
   * **Đảm bảo tính kế thừa:** Không làm mất đi các quy tắc cốt lõi về Clean Architecture và SSOT.

## Yêu cầu trình bày
* Giữ nguyên cấu trúc, tiêu đề và định dạng Markdown của `PROJECT_RULE.md`.
* Nếu có nội dung không chắc chắn hoặc code chưa hoàn thiện, đánh dấu là **"⚠️ Cần xác minh"**.
* Cập nhật số Version của `PROJECT_RULE.md` (nếu có).

## Bảng tóm tắt kết quả (BẮT BUỘC)
Sau khi cập nhật, tạo bảng tóm tắt:
* **Nội dung cập nhật:** (Tên section/quy tắc đã sửa)
* **Lý do (Rationale):** (Tại sao thay đổi này quan trọng cho hệ thống/luận văn?)
* **Ảnh hưởng:** (Có gây phá vỡ tính tương thích ngược hay không?)
* **Đồng bộ hóa:** (Có cần cập nhật `AGENT.md`, `PACKAGE_INDEX.md` hay `BUSINESS_INDEX.md` không?)

---
*Lưu ý: Mặc định KHÔNG sửa AGENT.md trừ khi quy trình làm việc của AI cần thay đổi để thích nghi với kiến trúc mới.*
