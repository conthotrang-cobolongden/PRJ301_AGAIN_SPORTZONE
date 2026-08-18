# Quy tắc làm việc chung — SportZone Management

**Dự án:** PRJ301 — Quản lý tổ hợp sân Tennis + Pickleball (SportZone)
**Áp dụng cho:** 2 model AI làm việc song song trên cùng repo này
**Ngày ban hành:** 2026-08-17

> Bất kỳ model nào nhận task trong repo này **phải đọc file này trước khi làm**,
> đọc xong mới được đụng vào code / giao diện. Làm xong **bắt buộc** báo cáo theo mục 3.

---

## 1. Phân công vai trò

| Model | Vai trò | Phạm vi phụ trách | Được sửa | Không được sửa |
|-------|---------|-------------------|----------|----------------|
| **Qwen** | Thiết kế UI/UX | Giao diện, layout, màu sắc, typography, CSS, cấu trúc JSP (phần trình bày), design spec | `web/assets/css/*`, `web/assets/img/*`, markup/trình bày trong JSP, `design-system/*`, tài liệu design | Logic Java (Servlet, DAO, Service, Filter), nghiệp vụ, SQL |
| **Qwen** | Xử lý logic code | Java backend + logic trong JSP: Servlet, DAO, Service, Filter, util, validate, transaction, SQL | `src/java/**`, `web.xml`, phần logic JSTL/scriptlet trong JSP, `sql/*` | Tự ý đổi thiết kế giao diện (màu, layout, spacing) khi chưa có spec của Qwen |

**Nguyên tắc ranh giới:**
updating...
---

## 2. Quy trình làm việc (bắt buộc với cả 2)

Mỗi task đều đi qua 4 bước:

1. **Nhận task** — đọc file này + `docs/implementation-plan.md` + log (`rules/LOG-LAM-VIEC.md`) để biết người kia đã làm gì, tránh giẫm chân.
2. **Làm** — đúng phạm vi vai trò ở mục 1.
3. **Báo cáo** — ghi entry vào `rules/LOG-LAM-VIEC.md` theo template mục 3, **ngay sau khi xong** (không để dồn).
4. **Note vào code** — mọi đoạn code/file mình tạo hoặc sửa phải có ghi chú rõ ràng theo quy tắc mục 4.

Không được nhảy cóc: không báo cáo = coi như chưa làm xong.

---

## 3. Quy tắc báo cáo tiến độ

### 3.1 Ghi ở đâu

- **`rules/LOG-LAM-VIEC.md`** — log chung của 2 model, entry mới nhất trên cùng. Đây là nơi bắt buộc.
- `docs/tien-do.md` — nhật ký tiến độ "chính chủ" của dự án (cho giảng viên/chủ repo). Chỉ cập nhật khi hoàn tất một mốc lớn (xong 1 phase, 1 màn hình trọn vẹn), và ghi tóm tắt từ log sang.

### 3.2 Template entry (copy nguyên mẫu này)

```markdown
## [YYYY-MM-DD HH:MM] — <QWEN | DEEPSEEK> — <tên task ngắn>

**Task:** mô tả yêu cầu nhận được (1–2 dòng)
**Trạng thái:** Xong | Xong một phần | Bị chặn

### Đã làm
- [ ] gạch đầu dòng từng việc cụ thể, kèm đường dẫn file
- [ ] ...

### File đã tạo / sửa
| File | Loại (Mới/Sửa) | Tóm tắt thay đổi |
|------|----------------|------------------|

### Chưa làm / tồn đọng
- ...

### Ghi chú cho model còn lại
- (ví dụ: Deepseek cần bind thêm biến `courtList` vào `court/list.jsp`;
  Qwen đã chừa sẵn `<c:forEach>` chỗ dòng ~40)

### Cần người dùng quyết định (nếu có)
- ...
```

### 3.3 Quy tắc ghi log

- Trung thực: test chưa chạy / chưa verify thì ghi rõ, không báo cáo "xong" khi chưa xong.
- Entry của ai người đó ghi, không sửa entry của người kia (chỉ được thêm entry mới phản hồi).
- Mục **"Ghi chú cho model còn lại"** là kênh bàn giao chính — viết cụ thể đến mức người kia không cần hỏi lại.

---

## 4. Quy tắc ghi chú trong code

Mục tiêu: mở bất kỳ file nào lên cũng hiểu **ai viết, viết cho task nào, tại sao viết vậy**.

### 4.1 Chung cho cả 2

- Comment giải thích **ràng buộc/quy tắc nghiệp vụ** mà bản thân code không tự nói lên được (không comment kiểu "dòng này tăng i lên 1").
- Mọi hàm/class/khối giao diện quan trọng gắn **tag chức năng** theo ID trong `docs/admin-user-features.md` (ví dụ `A27`, `U10`, `U13`) để truy vết ngược về yêu cầu.
- Không ghi nguồn gốc ("copy từ StackOverflow", "AI sinh ra") vào comment.

### 4.2 Với Qwen (Java / JSP logic / SQL)

- **Class:** Javadoc ngắn nêu nhiệm vụ + tag chức năng.
  ```java
  /**
   * Tạo booking mới — KHÔNG sinh hóa đơn (rule U10).
   * Tag: U10, U13
   */
  ```
- **Method phức tạp / có transaction:** comment từng bước nghiệp vụ + lý do (ví dụ "bắt unique index conflict thay vì check-then-act để chống double-book").
- **Rule khóa cứng** (mục 6 bên dưới): tại đúng dòng code thực thi rule, comment trích số rule. Ví dụ: `// Rule 3: Unpaid/Paid/Cancelled chỉ nằm trên Invoices.PaymentStatus`.
- **SQL:** comment đầu mỗi script/bảng nêu mục đích + cardinality liên quan (giữ thói quen đang có trong `sql/`).

### 4.3 Với Qwen (CSS / JSP markup / design spec)

- **`app.css`:** chia section bằng block comment (`/* ===== 1. DESIGN TOKENS ===== */`), mỗi token màu/font phải comment tên biến design-system tương ứng trong `design-system/sportzone/MASTER.md`.
- **JSP:** comment đánh dấu từng khối layout (`<!-- ===== HEADER: nav theo role (U2/U3) ===== -->`) và comment chỗ nào **chờ Deepseek bind dữ liệu** (`<!-- DEEPSEEK: bind danh sách sân vào c:forEach bên dưới, biến courtList -->`).
- **Design spec** (`design-system/sportzone/`): ghi rõ spec cho trang nào, áp dụng cho file JSP nào, quyết định màu/font/layout lấy từ kết quả search nào của skill.

---

## 5. Quy trình bàn giao thiết kế → code

Luồng chuẩn khi làm một màn hình / phase:

```text
Người dùng giao việc
   │
   ├─► QWEN:
   │     1. Chạy skill ui-ux-pro-max lấy design system / tra cứu UX (mục 6)
   │     2. Viết spec vào design-system/sportzone/pages/<ten-trang>.md
   │        (layout, màu, font, component, trạng thái: empty/loading/error,
   │         responsive breakpoint, tên CSS class sẽ dùng)
   │     3. (Nếu được giao) dựng luôn markup JSP + CSS theo spec
   │     4. Báo cáo vào LOG, tag "Ghi chú cho Deepseek" nêu rõ biến cần bind
   │
   └─► QWEN:
         1. Đọc spec của Qwen + LOG trước khi code
         2. Viết Servlet/DAO/Service + bind dữ liệu đúng chỗ Qwen đánh dấu
         3. KHÔNG đổi class/layout; nếu spec bất khả thi (thiếu dữ liệu, trùng
            cấu trúc) → ghi LOG mục "Cần người dùng quyết định" hoặc "Ghi chú
            cho Qwen", không tự chế giao diện khác
         4. Báo cáo vào LOG
```

**Thứ tự ưu tiên khi mâu thuẫn:**
nghiệp vụ (`implementation-plan.md` mục 3) > design spec > thói quen cá nhân của model.

---

## 6. Điều KHÔNG ĐƯỢC VI PHẠM (ràng buộc cứng của dự án)

Trích từ `docs/implementation-plan.md` — cả 2 model phải giữ nguyên khi thiết kế lẫn code:

1. U10 **không** tạo Invoice; chỉ insert `Bookings` + `BookingServices`. Slot → `Booked`.
2. A27 tạo tối đa 1 hóa đơn / booking; mặc định `Unpaid`, `PaymentMethod = NULL`.
3. `Unpaid`/`Paid`/`Cancelled` **chỉ** nằm trên `Invoices.PaymentStatus`.
4. Hết hạn + đã có hóa đơn → giữ `Unpaid`; chưa có hóa đơn → hủy booking, không invent dòng Unpaid.
5. Hủy booking có hóa đơn → invoice `Cancelled` trong cùng transaction.
6. `Bookings.CourtID` phải khớp `TimeSlots.CourtID`.
7. Snapshot giá khi đặt (`CourtPriceAtOrder`, `PriceAtOrder`, `TotalAmount`).
8. Chống double-book bằng transaction + unique index, không check-then-act.
9. Account `Frozen` đăng nhập được nhưng không đặt sân / không báo chuyển khoản.
10. Admin không tạo/xóa loại sân, không tạo/xóa sân.
11. User không Confirm / Complete / NoShow.

**Ràng buộc công nghệ (không tự đổi):**

- Stack: Tomcat 9 + `javax.servlet` + JSP/JSTL + **Bootstrap 5 CDN** + `assets/css/app.css`. Không thêm framework JS/CSS mới (không React, không Tailwind build) trừ khi người dùng duyệt.
- UI **tiếng Việt**, mã status giữ tiếng Anh trong DB (Pending, Confirmed, …) — badge hiển thị có thể kèm nhãn Việt.
- Không làm: payment gateway, upload file, WebSocket, REST JSON cho UI.
- CSRF hidden field trên **mọi** form POST — Qwen khi dựng form phải luôn chừa hidden field; Deepseek phải validate.

---

## 7. Cách Qwen dùng skill thiết kế (ui-ux-pro-max)

Skill đã cài tại workspace: `.zcode/skills/` (7 skill: `ui-ux-pro-max`, `design`, `design-system`, `ui-styling`, `brand`, `banner-design`, `slides`).

Lệnh tra cứu chính (chạy từ thư mục gốc project):

```bash
# Design system cho trang mới (bắt buộc với trang/phase mới)
python ".zcode/skills/ui-ux-pro-max/scripts/search.py" "<từ khóa sản phẩm>" --design-system -p "SportZone"

# Lưu design system thành nguồn chuẩn (Master) — chỉ chạy 1 lần, không --force nếu chưa hỏi người dùng
python ".zcode/skills/ui-ux-pro-max/scripts/search.py" "<từ khóa>" --design-system --persist -p "SportZone" --output-dir "."

# Tra cứu chuyên sâu theo domain: style | color | typography | ux | chart | icons | landing | product
python ".zcode/skills/ui-ux-pro-max/scripts/search.py" "<từ khóa>" --domain ux

# Lưu ý: stack của project là JSP + Bootstrap 5 (gần html-tailwind nhất nhưng KHÔNG phải Tailwind)
# → chỉ dùng kết quả stack như tham khảo ý tưởng, hiện thực bằng Bootstrap 5 + CSS thuần
```

Kết quả search phải được **chép lại quyết định cuối cùng vào design spec** (mục 5), không để trôi trong log terminal.

---

## 8. Quy ước đặt tên & cấu trúc sản phẩm design

```text
design-system/
  sportzone/
    MASTER.md              # nguồn chuẩn toàn cục: màu, font, spacing, badge status, nút, form
    pages/
      home.md              # spec từng trang (U19)
      login-register.md    # (U1, U2)
      courts.md            # (U7, U8, U9)
      booking.md           # (U10–U13)
      invoices.md          # (U14–U18)
      profile.md           # (U4–U6)
      admin-dashboard.md   # (A3)
      admin-catalog.md     # (A4–A18)
      admin-operations.md  # (A19–A33)
```

- Badge status phải thống nhất toàn app (màu nào cho Pending/Confirmed/Completed/Cancelled/NoShow, Unpaid/Paid) — định nghĩa một lần trong `MASTER.md`, mọi trang dùng lại.
- Tên CSS class thêm mới prefix `sz-` (SportZone) để không đụng class Bootstrap.

---

## 9. Checklist trước khi báo cáo "Xong"

**Qwen:**
- [ ] Spec/markup đủ trạng thái: có dữ liệu, rỗng (empty state), lỗi, loading (nếu có)
- [ ] Responsive ít nhất 3 mốc: mobile (~375px), tablet (~768px), desktop (~1200px)
- [ ] Contrast chữ ≥ 4.5:1; nút/bấm được có hover/focus state
- [ ] Form có chỗ cho CSRF hidden field + hiển thị lỗi field
- [ ] Đã ghi entry LOG + ghi chú chỗ chờ Deepseek bind

**Qwen:**
- [ ] Compile sạch, không warning
- [ ] Test thật trên Tomcat theo Definition of Done của phase (không nói "chắc là chạy")
- [ ] CSRF validate mọi POST; ownership check (user không thấy dữ liệu người khác)
- [ ] Comment + tag chức năng theo mục 4
- [ ] Đã ghi entry LOG + ghi chú cho Qwen nếu có đụng markup

---

*File này là hợp đồng làm việc của model. Muốn sửa quy tắc → hỏi người dùng trước.*
