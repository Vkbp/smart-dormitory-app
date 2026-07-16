Mình nghĩ có thể làm tốt hơn việc chỉ "liệt kê công cụ".

Với đồ án của bạn (SDMS Android + Backend + Luận văn), mình sẽ thiết kế thành một **hệ sinh thái AI cho luận văn** giống như một công ty có nhiều nhân sự AI, mỗi AI đảm nhiệm một vai trò. Như vậy bạn chỉ quản lý bằng prompt, còn AI tự phối hợp.

---

# Tổng quan hệ thống

```text
                                    Bạn
                                     │
                                     │ Prompt
                                     ▼
                    ┌─────────────────────────────────┐
                    │        Orchestrator Agent        │
                    │      (Gemini CLI/Codex CLI)      │
                    └─────────────────────────────────┘
              ┌──────────────┬───────────────┬──────────────┐
              ▼              ▼               ▼              ▼
      Technical AI     Research AI     Writing AI     Diagram AI
              ▼              ▼               ▼              ▼
      Code Repository   Papers/PDF      Word DOCX     Draw.io/Mermaid
              └──────────────┬───────────────┬──────────────┘
                             ▼
                      thesis.docx
```

Đây là mô hình mình khuyến nghị vì nó mở rộng được, không chỉ phục vụ luận văn mà cả báo cáo thực tập, báo cáo đồ án và sau này đi làm.

---

# Thành phần 1 - Word

**Mục đích**

Lưu toàn bộ luận văn.

**Công cụ**

* Microsoft Word

**Vai trò**

* Chỉ để xem và chỉnh tay khi cần.
* Không phải copy/paste từ AI.

---

# Thành phần 2 - Thesis Agent

Đây là thành phần quan trọng nhất.

Ví dụ bạn gõ:

```text
Hoàn thiện Chương 4 theo backend mới.
```

Agent sẽ tự:

```
Đọc thesis.docx

↓

Đọc backend

↓

Đọc prompt

↓

Sửa Word

↓

Lưu
```

Không upload.

---

# Thành phần 3 - Repository

Toàn bộ source

```
Android

Backend

Database

API

```

Agent luôn đọc trực tiếp.

Không cần gửi code vào chat.

---

# Thành phần 4 - Prompt Center

Thư mục

```
prompts/

    update.md

    review.md

    rewrite.md

    diagram.md

    citation.md
```

Ví dụ

```
update.md
```

ghi

```
Cập nhật Chương 3.

Thêm:

Clean Architecture

JWT

Role Guard

Không sửa chương khác.
```

Agent đọc xong tự sửa.

---

# Thành phần 5 - AGENT.md

Đây là "luật".

Ví dụ

```
Không đổi font.

Không đổi heading.

Không đổi numbering.

Không được bịa API.

Không được bịa UML.

Nếu thiếu dữ liệu thì TODO.

```

Agent luôn đọc đầu tiên.

---

# Thành phần 6 - Style Guide

Ví dụ

```
Times New Roman

13

1.5 line

Heading 1

Heading 2

Caption

Reference

```

AI sẽ không làm sai format.

---

# Thành phần 7 - Structure

Ví dụ

```
Chapter 1

Chapter 2

...

```

AI biết phải chèn đúng vị trí.

---

# Thành phần 8 - Research AI

Chỉ chuyên:

* đọc paper
* đọc IEEE
* đọc ACM
* đọc Springer

Không đụng Word.

Nó xuất:

```
summary.md
```

---

# Thành phần 9 - Writing AI

Chỉ viết.

Ví dụ

```
Viết mục

2.4 JWT Authentication

```

Nó không sửa Word.

Nó chỉ sinh nội dung.

---

# Thành phần 10 - Technical AI

Chỉ đọc code.

Ví dụ

```
Repository

MVVM

Retrofit

Room

JWT

Role Guard

```

Nó tạo:

```
implementation.md
```

---

# Thành phần 11 - Diagram AI

Sinh

```
Architecture

Use Case

Sequence

ERD

Flowchart

```

Xuất PNG hoặc SVG.

---

# Thành phần 12 - Review AI

Đọc xong luận văn.

Báo

```
Thiếu hình

Thiếu nguồn

Sai numbering

Sai caption

Sai thuật ngữ

```

---

# Thành phần 13 - Citation AI

Quản lý

```
IEEE

APA

ISO690

```

Không phải sửa tay.

---

# Thành phần 14 - Backup

Mỗi lần sửa

```
thesis.docx

↓

backup

↓

timestamp

↓

edit
```

Không sợ AI sửa hỏng.

---

# Thành phần 15 - Python Automation

Ví dụ

```
Open Word

↓

Tìm Heading

↓

Replace

↓

Insert Table

↓

Insert Image

↓

Save

```

Đây là phần thật sự sửa Word.

---

# Toàn bộ thư mục

```text
THESIS/

│
├── thesis.docx
│
├── docs/
│     structure.md
│     style.md
│     glossary.md
│     AGENT.md
│
├── prompts/
│     update.md
│     rewrite.md
│     review.md
│     citation.md
│
├── outputs/
│     diagrams/
│     images/
│     tables/
│
├── research/
│     papers/
│     summaries/
│
├── project/
│     android/
│     backend/
│
├── scripts/
│     update_doc.py
│     review.py
│     backup.py
│
└── backup/
```

---

# Quy trình làm việc

```text
Bạn

↓

Viết prompt

↓

Agent

↓

Đọc AGENT.md

↓

Đọc Style

↓

Đọc Structure

↓

Đọc Source Code

↓

Đọc thesis.docx

↓

Phân tích

↓

Review trước

↓

Bạn OK

↓

Backup

↓

Edit Word

↓

Save

↓

Sinh báo cáo thay đổi
```

---

# Bộ AI mình khuyến nghị

| Vai trò                     | Công cụ                               |
| --------------------------- | ------------------------------------- |
| AI điều phối (Orchestrator) | Gemini CLI hoặc Codex CLI             |
| Chỉnh sửa Word              | Python (`pywin32`/COM Automation)     |
| Viết nội dung               | ChatGPT hoặc Gemini                   |
| Phân tích mã nguồn          | Gemini CLI hoặc Codex CLI             |
| Nghiên cứu tài liệu         | ChatGPT (có tìm kiếm web) hoặc Gemini |
| Vẽ sơ đồ                    | Mermaid, PlantUML, Draw.io            |
| Quản lý phiên bản           | Git                                   |
| Sao lưu                     | Script Python                         |

## Vì sao mình chọn kiến trúc này?

Điểm mạnh của kiến trúc này là **không phụ thuộc vào một AI duy nhất**. Mỗi công cụ làm đúng việc mình mạnh nhất:

* **Gemini CLI/Codex CLI** đọc toàn bộ source code và điều phối quy trình.
* **Python COM Automation** thao tác trực tiếp với Microsoft Word, giúp giữ định dạng tốt hơn so với việc chuyển đổi qua Markdown.
* **ChatGPT** hỗ trợ viết, rà soát, diễn đạt học thuật và kiểm tra tính mạch lạc của nội dung.
* **Git** quản lý lịch sử thay đổi để luôn có thể quay lại phiên bản trước.

Theo mình, đây là một workflow đủ mạnh để xử lý một luận văn kỹ thuật dài 100–200 trang mà vẫn dễ bảo trì và mở rộng.

**Mình còn có thể thiết kế phiên bản "Thesis AI Ecosystem v2.0"**, mô phỏng như một sản phẩm phần mềm chuyên nghiệp: có sơ đồ kiến trúc nhiều tầng, hơn 20 agent chuyên biệt (Research Manager, Chapter Manager, Figure Manager, Citation Manager, QA Manager, Defense Manager...), quy trình CI/CD cho luận văn, cơ chế Review → Approve → Apply và tự động sinh báo cáo thay đổi sau mỗi lần chỉnh sửa. Với dự án SDMS của bạn, đây sẽ là kiến trúc gần như hoàn chỉnh để làm luận văn bằng AI từ đầu đến cuối.
