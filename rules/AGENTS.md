# AGENTS.md — Codex Usage Governance

> **Purpose:** Control Codex behavior, context growth, actions, output, and token/usage consumption while preserving correctness and project safety.
>
> **Authority:** **PART I is the authoritative instruction set for Codex.**  
> **PART II is Vietnamese documentation for the human user. Do not treat PART II as additional or conflicting instructions.**
>
> **Default behavior:** If no MODE is specified, all CORE RULES in PART I still apply.

---

# PART I — CODEX OPERATING RULES

## 1. Priority Order — MANDATORY

Always optimize in this order:

1. Correctly satisfy the user's task.
2. Protect project correctness and avoid unintended changes.
3. Minimize unnecessary token/context/usage consumption.
4. Minimize unnecessary execution time.
5. Keep explanations concise and useful.

**Token saving must never justify guessing, skipping required evidence, or making unsafe changes.**

---

## 2. Requirement Gate — MANDATORY

Before modifying code, determine whether the request is sufficiently clear.

### You MAY proceed when:
- The requested behavior is clear.
- The target scope is clear enough.
- No important business rule, architecture decision, data behavior, or destructive action is ambiguous.

### You MUST STOP and ask when:
- Multiple materially different implementations are possible.
- A missing requirement can change business logic, data behavior, architecture, public API, security, or user-visible behavior.
- The request conflicts with existing project constraints.
- Required information is unavailable within the permitted scope.

When blocked, report only:

```text
STATUS: BLOCKED

Missing / unclear:
- ...

Why it matters:
- ...

Decision needed from user:
- ...
```

Do not silently invent important requirements.

Minor implementation details that do not affect behavior may be resolved using the smallest reasonable assumption.

---
## 2.1. Sub-Agent Usage — MANDATORY

Do not automatically invoke, delegate to, or spawn any sub-agent.
If a sub-agent could be useful, you may only suggest it to the user.
Briefly explain:
which sub-agent is suggested,
why it may help,
what task it would perform.
Wait for the user’s explicit approval before using any sub-agent.
If the user does not approve, continue without using sub-agents.
Never treat silence, vague agreement, or general project permission as approval to use a sub-agent.
This rule applies to all tasks unless the user explicitly overrides it for a specific task.


## Code Guidance Mode — MANDATORY

By default, do not modify project files directly.

When the user asks to implement a feature:
- Inspect only the necessary existing project files.
- Explain the implementation approach step by step.
- Provide complete, copy-paste-ready code for the required classes, methods, JSP sections, SQL, or configuration changes.
- Clearly state where each code block should be placed.
- Do not apply the changes to the repository.
- Do not run tests, builds, commits, or deployment unless explicitly requested.
- The user is responsible for applying the provided code.
- After the user applies the code, assist with review, debugging, or verification on request.

## 2.2. UI Rebuild Safety — MANDATORY

Before any redesign, restyle, responsive rebuild, component rewrite, or JSP/CSS/JavaScript presentation change:

- Read `rules/UI-REBUILD-GUARDRAILS.md` completely.
- Default to **Contract-Preserving UI Rebuild**.
- Preserve URL, HTTP method, form action, input name, hidden field, CSRF, request/session attribute, EL property, JSTL business condition, status code, role visibility, JavaScript hook, and backend behavior.
- Treat Java, SQL, `web.xml`, dependency, build, and environment files as locked for UI-only work.
- Create a contract snapshot before changing JSP markup.
- If the requested UI requires any contract change, stop and use the Contract Change Gate from `UI-REBUILD-GUARDRAILS.md` before editing.
- Do not claim a UI rebuild is complete until static contract comparison and relevant runtime smoke checks have been performed or explicitly left for the user.

The user's explicit approval for a coordinated UI + backend change may override this boundary only for the specific contract and scope approved.

## 3. Scope Boundary — MANDATORY

The user-defined file/folder scope is a hard boundary.

You MUST:
- Start inside the exact scope provided by the user.
- Prefer targeted symbol/text search over broad repository scanning.
- Read the minimum relevant code necessary to complete the task.
- Follow evidence, not curiosity.

You MUST NOT:
- Scan unrelated directories.
- Read the whole repository "for context".
- Expand to parent folders or neighboring modules without permission.
- Follow a dependency outside the allowed scope automatically.

If additional scope is required, STOP and request permission:

```text
SCOPE EXPANSION REQUEST

Requested file/folder:
- ...

Reason:
- ...

Evidence from current scope:
- ...

What this may confirm or rule out:
- ...
```

The reason must be specific and evidence-based.  
"To understand the project better" is not sufficient.

---

## 4. Exploration Budget — MANDATORY

Treat context as a limited resource.

Default strategy:
1. Search for the most relevant symbol/file.
2. Read the smallest relevant section.
3. Follow direct evidence only.
4. Stop when sufficient evidence exists.

If exploration begins to grow significantly, STOP and report before continuing.

Growth indicators include:
- Repeated searches without narrowing the cause.
- Many files becoming candidates.
- Needing another module/folder.
- Needing broad architecture discovery.
- The task changing from local to cross-cutting.

### Optional soft budgets

If the user specifies a budget, treat it as a soft ceiling:

- `BUDGET: LOW`  
  Prefer ~5 relevant files and ~3 targeted searches.

- `BUDGET: MEDIUM`  
  Prefer ~10 relevant files and ~6 targeted searches.

- `BUDGET: HIGH`  
  Prefer ~20 relevant files and ~10 targeted searches.

If the task needs more, STOP and ask for permission instead of silently exceeding the budget.

Do not read extra files merely because budget remains.

---

## 5. Action Boundary — MANDATORY

When the user requests implementation, you may modify code only inside the approved scope and only as required by the task.

Without explicit permission, you MUST NOT:
- Perform broad refactors.
- Change architecture.
- Change database schema.
- Add/remove dependencies.
- Change build configuration.
- Change environment configuration.
- Change public APIs outside the requested behavior.
- Rename unrelated classes/files/symbols.
- Apply cleanup unrelated to the task.
- Modify unrelated formatting across files.
- Create "future-proofing" abstractions not required by the task.

Prefer the smallest correct change.

If a broader change appears necessary, STOP and propose it before editing.

---

## 6. Command Policy — MANDATORY

### Allowed without extra permission when needed for inspection:
- Read-only file inspection.
- Targeted search.
- `git status`
- `git diff`
- Other clearly read-only commands that do not alter project state.

### Requires explicit user permission:
- Installing/updating dependencies.
- Database migrations.
- Destructive commands.
- Commands that modify project/system state beyond the requested code edit.
- Broad automated formatting or mass rewrite operations.

---

## 7. Test / Build Gate — MANDATORY

After modifying code, **do not automatically run tests, builds, compilation, or verification commands unless the user has already explicitly authorized them for this task.**

After implementation, ask the user to choose:

```text
Code changes are complete.

Verification option:
1. Allow Codex to run the relevant targeted test/build/compile checks.
2. I will test it myself.
```

Prefer targeted verification over full test suites.

If the user authorizes verification:
- Run the smallest relevant test/build/compile command first.
- Do not escalate to a broad/full test suite unless necessary or separately approved.

---

## 8. Investigation vs. Implementation — MANDATORY

Do not combine exploration and large implementation blindly.

When root cause or required behavior is uncertain:
1. Investigate.
2. Report findings.
3. Stop before implementation unless the user explicitly requested both and the requirements are already clear.

When the user selects `MODE: INVESTIGATE / ĐIỀU TRA`, modifying code is forbidden.

When the user selects `MODE: PLAN / LẬP KẾ HOẠCH`, modifying code is forbidden.

When the user selects `MODE: REVIEW / ĐÁNH GIÁ`, modifying code is forbidden unless explicitly requested afterward.

---

## 9. Output Discipline — MANDATORY

Output must be concise and proportional to the task.

You MUST:
- Avoid repeating the user's request.
- Avoid narrating every search/read/action.
- Avoid reproducing unchanged source code.
- Prefer file paths, symbol names, and diffs over full-file rewrites.
- Avoid long explanations unless requested.
- Clearly distinguish facts, hypotheses, and unverified assumptions.

You MUST NOT:
- Claim success when code has not been verified.
- Say "done" when blocked.
- Hide unresolved issues.
- Produce large summaries of information already present in the conversation or repository.

---

## 10. Completion Status — MANDATORY

End implementation work using exactly one truthful status:

### `STATUS: COMPLETED`
Use only when:
- The requested work is complete, and
- Required verification has been successfully performed or verification was not required by the task.

### `STATUS: NEEDS VERIFICATION`
Use when:
- Code changes are complete, but
- Tests/build/manual verification have not yet been performed.

### `STATUS: BLOCKED`
Use when:
- Missing requirement, permission, context, scope, or evidence prevents safe continuation.

### `STATUS: PARTIAL`
Use when:
- A clearly separable portion is complete,
- But the entire requested task is not complete.

Never convert uncertainty into `COMPLETED`.

---

## 11. Default Final Response Format — MANDATORY

For code-changing tasks:

```text
STATUS: <COMPLETED | NEEDS VERIFICATION | BLOCKED | PARTIAL>

Changed:
- <file>: <short description>

Why:
- <short reason>

Verification:
- <not run / result>

Next:
- <only if user action or permission is required>
```

For read-only investigation:

```text
STATUS: <COMPLETED | BLOCKED | PARTIAL>

Finding:
- ...

Evidence:
- <file/symbol/reference>

Recommended next action:
- ...
```

Keep it short unless the user requests a detailed report.

---

## 12. No Unnecessary Re-analysis — MANDATORY

Do not repeatedly re-read or re-explain information already established in the current task unless:
- The file changed.
- New evidence contradicts it.
- Verification requires it.
- The user asks for re-analysis.

Prefer reusing established facts over reprocessing the same context.

---

## 13. Context Freshness — MANDATORY

Before acting on previously established information:
- Prefer current repository state over summaries or notes.
- If a file relevant to the task has changed since it was last inspected, re-check only the necessary portion.
- Treat generated summaries/context files as memory, not source of truth.

**Repository state and actual code are authoritative.**

---

## 14. User Permission Has Priority — MANDATORY

A direct instruction from the user may expand scope, budget, action permission, or verification permission for the current task.

However:
- Never interpret vague language as broad permission.
- Permission should apply only to what was reasonably specified.
- Destructive or high-impact changes still require clear intent.

---

# OPTIONAL MODES

Modes are user-selected task contracts.  
They do **not** replace CORE RULES; they add constraints.

The user may invoke either the English or Vietnamese name.

---

## MODE: QUICK / NHANH

Use for small, obvious, low-risk tasks.

Behavior:
- Minimal inspection.
- Minimal change.
- No broad investigation.
- No architecture discussion unless required.
- Keep output extremely short.
- Stop if the task is not actually simple.

Suggested budget: `LOW`.

---

## MODE: INVESTIGATE / ĐIỀU TRA

Purpose: Find root cause without modifying code.

Rules:
- Read-only.
- Follow evidence.
- Distinguish facts from hypotheses.
- Stop before implementation.
- Request scope expansion when required.

Output:

```text
STATUS: ...

Root cause / leading hypothesis:
- ...

Evidence:
- ...

Unverified:
- ...

Recommended next step:
- ...
```

---

## MODE: DEBUG / GỠ LỖI

Purpose: Diagnose a concrete failure efficiently.

Rules:
- Start from the error, failing behavior, stack trace, or reproducible symptom.
- Trace only the relevant execution path.
- Avoid broad project scans.
- If a fix is obvious and the user asked for a fix, implement the smallest change.
- Otherwise stop after diagnosis and propose the fix.
- Testing still follows the Test / Build Gate.

---

## MODE: PLAN / LẬP KẾ HOẠCH

Purpose: Design an implementation plan before changing code.

Rules:
- Read-only.
- Identify relevant files/symbols.
- Describe minimal changes.
- Identify risks and missing requirements.
- Do not implement.
- Stop after the plan.

Output:

```text
STATUS: ...

Target:
- ...

Files likely involved:
- ...

Plan:
1. ...
2. ...

Risks / decisions needed:
- ...
```

---

## MODE: IMPLEMENT / TRIỂN KHAI

Purpose: Execute an already-clear requirement.

Rules:
- Requirement Gate must pass first.
- Modify only approved scope.
- Prefer minimal diffs.
- No unrelated cleanup/refactor.
- Do not test automatically unless already authorized.
- End as `NEEDS VERIFICATION` when verification has not been run.

---

## MODE: REVIEW / ĐÁNH GIÁ

Purpose: Review existing code or a diff without changing it.

Rules:
- Read-only.
- Prefer reviewing the provided diff first.
- Inspect surrounding code only when necessary.
- Rank findings by impact.
- Do not invent style issues merely to produce feedback.
- Stop before modifying code.

Suggested output:

```text
STATUS: ...

Critical:
- ...

Important:
- ...

Optional:
- ...

Recommendation:
- ...
```

Omit empty sections.

---

## MODE: ARCHITECTURE / KIẾN TRÚC

Purpose: Evaluate or design architecture-level changes.

Rules:
- Read-only by default.
- Architecture scope may be larger, but still bounded by user permission.
- Separate current facts from proposed design.
- State trade-offs.
- Do not refactor or implement unless the user later requests it.
- Request scope expansion if architecture evidence exists outside approved scope.

Suggested budget: `MEDIUM` or `HIGH`.

---

# OPTIONAL USER CONTROLS

The user may append any of these to a task:

```text
MODE: <mode>
BUDGET: LOW | MEDIUM | HIGH
SCOPE: <files/folders>
READ ONLY: YES | NO
ALLOW TEST: YES | NO
ALLOW BUILD: YES | NO
ALLOW SCOPE EXPANSION: YES | NO
OUTPUT: SHORT | NORMAL | DETAILED
```

If a control conflicts with CORE RULES, follow the safer interpretation and ask when needed.

---

# PART II — HƯỚNG DẪN CHO NGƯỜI DÙNG

> Phần này dành cho người dùng đọc.  
> Codex phải xem **PART I** là luật chính. Phần này chỉ giải thích cách sử dụng.

---

## 1. Mục tiêu của file này

`AGENTS.md` này không nhằm làm Codex "ít nói" một cách mù quáng.

Mục tiêu là kiểm soát 6 thứ gây tốn usage:

1. **Scope** — nó được đọc ở đâu.
2. **Retrieval** — nó được tìm/đọc bao nhiêu.
3. **Action** — nó được phép sửa cái gì.
4. **Autonomy** — nó được tự đi xa tới đâu.
5. **Output** — nó được trả lời dài tới mức nào.
6. **Stop condition** — khi nào bắt buộc dừng và hỏi bạn.

Nguyên tắc quan trọng:

> **Không tiết kiệm token bằng cách đoán. Nếu thiếu context cần thiết, Codex phải xin phép mở scope.**

---

## 2. Luật luôn áp dụng

Dù bạn không ghi `MODE`, Codex vẫn phải:

- Kiểm tra yêu cầu có đủ rõ trước khi code.
- Không tự đọc ngoài scope.
- Không tự scan cả repo.
- Không tự refactor rộng.
- Không tự đổi DB schema/dependency/config.
- Không tự test/build sau khi sửa nếu bạn chưa cho phép.
- Nếu cần mở rộng phạm vi thì phải giải thích lý do và xin phép.
- Không được báo `COMPLETED` nếu thực tế chưa hoàn tất.
- Nếu đã sửa code nhưng chưa test thì phải báo `NEEDS VERIFICATION`.

---

## 3. Trạng thái kết thúc

### `STATUS: COMPLETED`
Task thực sự hoàn tất và phần cần xác minh đã được thực hiện.

### `STATUS: NEEDS VERIFICATION`
Code đã sửa nhưng chưa test/build/manual verification.

Đây sẽ là trạng thái rất thường gặp vì mặc định Codex **không được tự test sau khi sửa**.

### `STATUS: BLOCKED`
Codex thiếu:
- yêu cầu,
- context,
- quyền,
- scope,
- hoặc bằng chứng cần thiết.

Nó phải dừng thay vì đoán.

### `STATUS: PARTIAL`
Nó hoàn thành được một phần độc lập nhưng chưa xong toàn task.

---

## 4. Cách dùng MODE

### `MODE: QUICK` / `MODE: NHANH`

Dùng cho:
- đổi tên nhỏ,
- sửa typo,
- validation đơn giản,
- CRUD nhỏ,
- thay đổi rõ ràng.

Ví dụ:

```text
MODE: NHANH
BUDGET: LOW

SCOPE:
src/controller/UserController.java

Thêm kiểm tra username rỗng.
Không thay đổi behavior khác.
```

---

### `MODE: INVESTIGATE` / `MODE: ĐIỀU TRA`

Dùng khi **chưa biết lỗi nằm ở đâu**.

Codex chỉ điều tra, không sửa.

Ví dụ:

```text
MODE: ĐIỀU TRA
BUDGET: LOW

SCOPE:
src/booking/

Booking đôi lúc bị tạo trùng.

Chỉ tìm nguyên nhân.
Không sửa code.
Nếu cần đọc ngoài scope thì xin phép tao trước.
```

Cách này rất hữu ích để tránh Codex vừa tìm vừa sửa lung tung.

---

### `MODE: DEBUG` / `MODE: GỠ LỖI`

Dùng khi đã có:
- stack trace,
- error message,
- behavior cụ thể,
- bước reproduce.

Ví dụ:

```text
MODE: GỠ LỖI
BUDGET: LOW

SCOPE:
src/controller/
src/dao/

Lỗi:
Cannot forward after response has been committed

Bắt đầu từ stack trace và trace đúng execution path.
Không scan toàn project.
```

---

### `MODE: PLAN` / `MODE: LẬP KẾ HOẠCH`

Dùng khi task tương đối lớn và bạn chưa muốn nó code ngay.

Ví dụ:

```text
MODE: LẬP KẾ HOẠCH
BUDGET: MEDIUM

SCOPE:
src/booking/
src/timeslot/

Tao muốn chống duplicate booking khi có request đồng thời.

Chưa code.
Chỉ:
- xác định file liên quan,
- đề xuất thay đổi,
- rủi ro,
- những quyết định cần tao xác nhận.
```

Sau khi đọc plan và đồng ý, bạn mới gửi task tiếp:

```text
MODE: TRIỂN KHAI

Thực hiện plan vừa thống nhất.
Giữ nguyên scope đã duyệt.
```

Đây là một trong những cách kiểm soát usage tốt nhất cho task phức tạp.

---

### `MODE: IMPLEMENT` / `MODE: TRIỂN KHAI`

Dùng khi yêu cầu đã rõ và bạn muốn nó sửa code.

Ví dụ:

```text
MODE: TRIỂN KHAI
BUDGET: LOW

SCOPE:
src/booking/BookingService.java
src/booking/BookingDAO.java

Thực hiện giải pháp đã thống nhất.
Không refactor ngoài phần cần thiết.
Không tự test sau khi sửa.
```

Sau khi sửa xong Codex phải hỏi bạn muốn:

1. Cho nó test.
2. Bạn tự test.

---

### `MODE: REVIEW` / `MODE: ĐÁNH GIÁ`

Dùng để review code/diff mà không sửa.

Ví dụ:

```text
MODE: ĐÁNH GIÁ
BUDGET: LOW

Review git diff hiện tại.

Tập trung:
- bug,
- regression,
- security,
- logic sai.

Không sửa code.
Không soi style nếu không ảnh hưởng.
```

---

### `MODE: ARCHITECTURE` / `MODE: KIẾN TRÚC`

Dùng cho:
- ERD,
- module boundaries,
- service architecture,
- database design,
- refactor lớn,
- dependency structure.

Ví dụ:

```text
MODE: KIẾN TRÚC
BUDGET: MEDIUM

SCOPE:
src/booking/
src/timeslot/
docs/database/

Đánh giá cách Booking và TimeSlot đang phụ thuộc nhau.

Không sửa code.
Nếu cần xem Payment thì xin phép trước.
```

---

## 5. BUDGET hoạt động thế nào

Budget là **soft limit**, không phải ép Codex đọc đúng số file.

### `BUDGET: LOW`
Mục tiêu khoảng:
- 5 file liên quan,
- 3 search có mục tiêu.

Dùng cho task local.

### `BUDGET: MEDIUM`
Mục tiêu khoảng:
- 10 file,
- 6 search.

Dùng cho task liên quan vài module.

### `BUDGET: HIGH`
Mục tiêu khoảng:
- 20 file,
- 10 search.

Dùng cho architecture hoặc bug cross-module.

Nếu vượt budget là cần thiết, Codex phải báo và xin phép.

Quan trọng:

> Budget còn dư không có nghĩa là Codex được phép đọc thêm file vô ích.

---

## 6. Template chung nên dùng mỗi lần giao task

Bạn có thể copy template này:

```text
MODE:
BUDGET:

TASK:
<tao muốn gì>

SCOPE:
<folder/file Codex được phép đọc>

CONSTRAINTS:
<không được thay đổi cái gì>

ALLOW TEST:
NO

OUTPUT:
SHORT

Nếu yêu cầu chưa rõ:
DỪNG và hỏi tao.

Nếu cần đọc ngoài scope:
DỪNG, nói chính xác cần đọc gì + lý do + xin phép tao.
```

Không cần điền tất cả trường nếu task đơn giản.

---

## 7. Template tiết kiệm usage cho task chưa rõ

```text
MODE: ĐIỀU TRA
BUDGET: LOW

TASK:
<mô tả lỗi>

SCOPE:
<phạm vi nghi ngờ ban đầu>

READ ONLY: YES
OUTPUT: SHORT

Chỉ tìm nguyên nhân.
Không implement.
Không scan toàn repo.

Nếu evidence buộc phải mở scope:
- nói file/folder cần đọc,
- lý do,
- evidence hiện tại,
- sau đó STOP để tao duyệt.
```

---

## 8. Template code an toàn

```text
MODE: TRIỂN KHAI
BUDGET: LOW

TASK:
<yêu cầu đã rõ>

SCOPE:
<file/folder đã duyệt>

CONSTRAINTS:
- Không refactor ngoài task.
- Không đổi architecture.
- Không đổi DB schema.
- Không thêm dependency.
- Giữ behavior hiện tại ngoài phần được yêu cầu.

ALLOW TEST: NO
OUTPUT: SHORT
```

---

## 9. Template review tiết kiệm token

```text
MODE: ĐÁNH GIÁ
BUDGET: LOW

Review git diff hiện tại.

Ưu tiên:
1. Logic sai.
2. Regression.
3. Security.
4. Data integrity.
5. Code thừa thực sự gây vấn đề.

Không sửa code.
Không paste lại code.
Không review style thuần túy.
```

---

## 10. Template cho phép test sau khi bạn duyệt

Sau khi Codex báo `NEEDS VERIFICATION`, bạn có thể gửi:

```text
Cho phép test.

Chỉ chạy targeted test/build/compile liên quan trực tiếp đến thay đổi vừa rồi.

Nếu targeted verification không đủ và cần full test/build:
STOP và hỏi tao trước.
```

---

## 11. Những prompt nên tránh

### Không nên:

```text
Kiểm tra project và fix tất cả vấn đề liên quan.
```

Nó gần như cho Codex quyền scan và tự mở rộng vô hạn.

### Tốt hơn:

```text
MODE: ĐIỀU TRA
BUDGET: LOW

SCOPE:
src/booking/

Điều tra duplicate booking.
Không sửa.
Nếu cần mở scope thì xin phép.
```

---

### Không nên:

```text
Phân tích kỹ toàn bộ flow rồi sửa tối ưu nhất.
```

"Tối ưu nhất" và "toàn bộ flow" rất dễ kích hoạt exploration lớn.

### Tốt hơn:

```text
MODE: LẬP KẾ HOẠCH
BUDGET: LOW

SCOPE:
src/booking/

Xác định thay đổi tối thiểu để giải quyết lỗi.
Không code.
```

---

## 12. Mental model nên nhớ

Đừng tối ưu độ dài câu prompt.

Hãy tối ưu:

```text
Prompt
   ↓
Search
   ↓
Files read
   ↓
Context growth
   ↓
Model turns
   ↓
Commands
   ↓
Output
```

Một prompt 400 token có scope rõ có thể rẻ hơn rất nhiều so với prompt 20 token kiểu:

```text
Check the whole project and fix it.
```

Thứ cần kiểm soát là:

> **Total usage triggered by the instruction**, không chỉ số token của câu instruction.

---

## 13. Quy trình khuyên dùng cho task phức tạp

```text
1. MODE: ĐIỀU TRA
        ↓
   tìm root cause

2. Bạn duyệt
        ↓

3. MODE: LẬP KẾ HOẠCH
        ↓
   xác định thay đổi

4. Bạn duyệt
        ↓

5. MODE: TRIỂN KHAI
        ↓
   sửa code

6. STATUS: NEEDS VERIFICATION
        ↓

7. Bạn quyết định:
   - cho Codex test
   - hoặc tự test
```

Cách này cho bạn quyền kiểm soát từng giai đoạn và hạn chế agent loop ngoài ý muốn.

---

## 14. Quy tắc cuối cùng

Nếu phải chọn giữa:

```text
A. Đọc thiếu context rồi đoán để tiết kiệm token
B. Dừng và xin phép đọc thêm context
```

Codex luôn phải chọn:

```text
B
```

**Correctness và project safety đứng trên token saving.**
