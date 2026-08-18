# Implementation Plan — SportZone Management

**Môn:** PRJ301 — Assignment cá nhân  
**Sinh viên:** Nguyễn Hoàng Tùng (SE203366)  
**Hệ thống:** Quản lý tổ hợp sân Tennis + Pickleball  
**Trạng thái hiện hành (18/08):** working copy mới phải làm lại từ P1; code P1–P4 ngày 17/08 chỉ còn là lịch sử tham khảo. Hiện trạng chi tiết: [`../rules/new-plan-0954.md`](../rules/new-plan-0954.md).
**Ngày soạn:** 2026-08-17  

Tài liệu chức năng: [`admin-user-features.md`](admin-user-features.md)  
ERD chốt v3: [`erd-file/erd.md`](erd-file/erd.md)
Schema: [`../sql/01_SportZoneDB_Schema.sql`](../sql/01_SportZoneDB_Schema.sql)

---

## 1. Mục tiêu

Xây Java web đầy đủ **A1–A33 + U1–U19** trên schema đã chốt, chạy được trên Tomcat + SQL Server, đủ demo và nộp bài.

Phần đã xong (không làm lại): phân tích chức năng, schema, seed, ERD v3 và JSP/design đã giữ lại.
Phần phải làm lại trong working copy mới: P1 trở đi, bắt đầu từ cấu hình DB và nền tảng Java; chi tiết theo `rules/new-plan-0954.md`.

---

## 2. Đề xuất tách FE / BE

### 2.1 Không tách thành 2 app

**Không** làm React/Vue + REST API. PRJ301 chấm Servlet / JSP / Filter / JDBC. Tách 2 codebase tốn thời gian và lệch đề.

**Làm 1 app Java Web (MVC classic).** Tách FE/BE **trong cùng project**, theo layer và theo từng phase.

| Layer | Vai trò | Công nghệ |
|-------|---------|-----------|
| **BE** | Model, DAO, rule nghiệp vụ, Servlet, Filter | Java 11, JDBC, `javax.servlet` |
| **FE** | Trang, form, layout, CSS | JSP + JSTL + Bootstrap 5 CDN + CSS riêng |

Mỗi phase: **BE xong và test được (DAO + Servlet trả data)** rồi mới làm JSP của phase đó. Không viết JSP khi servlet chưa có.

### 2.2 Stack đề xuất

| Hạng mục | Chọn | Lý do |
|----------|------|--------|
| Runtime | Tomcat 9 + `javax.servlet` | Phổ biến PRJ301 / NetBeans |
| Build | NetBeans Ant WAR (`build.xml` + `nbproject`) | Thực tế đang dùng; jar JDBC + JSTL đặt tay trong `web/WEB-INF/lib` |
| DB | SQL Server đã có (`SportZoneDB`) | Seed sẵn |
| Hash | SHA-256 hex UTF-8, không salt | Khớp seed hiện tại |
| Session | HttpSession (`account`, `roleName`) | Đủ cho assignment |
| CSRF | Token trong session, hidden field mọi form POST | Bảo vệ đổi mật khẩu / đặt / hủy |
| UI | Bootstrap 5 CDN + `assets/css/app.css` | Nhanh, đủ đẹp nộp bài |
| JS | Tối thiểu (validate client, confirm dialog) | Không SPA |
| Ảnh sân | URL tĩnh / placeholder, admin sửa `ImageURL` | Không upload file (tránh scope creep) |

**Không làm:** cổng thanh toán online, WebSocket, JWT, REST JSON cho UI.

### 2.3 Cấu trúc thư mục đề xuất

```text
src/java/com/sportzone/      (NetBeans: src/java, không phải src/main/java)
  model/          Account, Court, TimeSlot, Booking, Invoice, Service, ...
  dao/            *DAO + DBUtils
  service/        BookingService, InvoiceService, AuthService  (rule nghiệp vụ)
  controller/     *Servlet (auth, user, admin)
  filter/         AuthFilter, RoleFilter, EncodingFilter
  util/           PasswordUtil, CsrfUtil, Validation, DateUtil
src/java/com/sportzone/dao/db.properties  (classpath, không commit password thật khi nộp)

web/                         (NetBeans web root)
  WEB-INF/
    web.xml
    views/
      layout/     header.jsp, footer.jsp, sidebar-admin.jsp
      auth/       login.jsp, register.jsp
      home/       index.jsp
      court/      list.jsp, detail.jsp
      booking/    form.jsp, list.jsp, detail.jsp
      invoice/    list.jsp, detail.jsp, print.jsp
      account/    profile.jsp, password.jsp
      admin/      dashboard.jsp, users.jsp, ...
      error/      403.jsp, 404.jsp, 500.jsp
  assets/css/app.css
  assets/img/
```

JSP nằm dưới `WEB-INF/views/` — client không gọi JSP trực tiếp, chỉ qua servlet.

### 2.4 URL map (chốt khi implement)

| URL | Role | Chức năng |
|-----|------|-----------|
| `GET /` `GET /home` | Public | U19 |
| `GET/POST /login` `GET /logout` | Public | A1 A2 U2 U3 |
| `GET/POST /register` | Public | U1 |
| `GET /courts` `GET /court?id=` | Public | U7 U8 |
| `GET /slots?courtId=&date=` | Public | U9 |
| `GET/POST /profile` `GET/POST /password` | User/Admin | U4 U5 U6 |
| `GET/POST /booking/create` | User | U10 |
| `GET /bookings` `GET /booking?id=` | User | U11 U12 |
| `POST /booking/cancel` | User | U13 |
| `GET /invoices` `GET /invoice?id=` | User | U14 U15 U16 U18 |
| `POST /invoice/notice` | User | U17 |
| `GET /admin` | Admin | A3 |
| `GET/POST /admin/users` … | Admin | A4–A7 |
| `GET/POST /admin/courts` … | Admin | A8–A10 |
| `GET/POST /admin/slots` … | Admin | A11–A14 |
| `GET/POST /admin/services` … | Admin | A15–A18 |
| `GET/POST /admin/bookings` … | Admin | A19–A24 |
| `GET/POST /admin/invoices` … | Admin | A25–A31 |
| `GET /admin/reports` | Admin | A32 A33 |

---

## 3. Quy tắc nghiệp vụ khóa cứng (mọi phase phải giữ)

1. **U10 không tạo Invoice.** Chỉ insert `Bookings` + `BookingServices` (nếu có). Slot → `Booked`.
2. **A27** tạo tối đa 1 HĐ / booking (`UNIQUE BookingID`). Mặc định `Unpaid`, `PaymentMethod = NULL`.
3. `Unpaid` / `Paid` / `Cancelled` **chỉ** nằm trên `Invoices.PaymentStatus`.
4. Hết hạn + đã có HĐ → giữ `Unpaid`. Chưa có HĐ → hủy booking, không invent dòng Unpaid.
5. Hủy booking (A23 / U13): nếu có HĐ thì set invoice `Cancelled` (A30) trong cùng transaction.
6. `Bookings` **không có `CourtID`**. Sân của booking luôn suy ra qua `Bookings.TimeSlotID → TimeSlots.CourtID`; backend không được tin `courtId` từ form để ghi booking.
7. Snapshot giá: `CourtPriceAtOrder`, `PriceAtOrder`, `TotalAmount = CourtPriceAtOrder + ServiceTotalAtOrder`.
8. Chống double-book: transaction + bắt lỗi unique index `UQ_Bookings_Slot_Active`.
9. Account `Frozen` đăng nhập được (xem hồ sơ / HĐ cũ) nhưng **không** đặt / không báo chuyển khoản.
10. CourtTypes + số sân cố định. Admin **không** tạo/xóa loại sân, **không** tạo/xóa sân.
11. User không Confirm / Complete / NoShow.

### Chính sách hủy (đề xuất — cần duyệt)

| Điều kiện | Kết quả |
|-----------|---------|
| Status `Pending` hoặc `Confirmed` | Được hủy |
| `Completed` / `Cancelled` / `NoShow` | Không |
| Slot đã bắt đầu (`SlotDate + StartTime <= now`) | Không |
| Còn ≥ **2 giờ** trước giờ bắt đầu | User hủy được |
| < 2 giờ | Chỉ Admin hủy (A23) |

Có HĐ `Paid` rồi thì User **không** tự hủy; Admin hủy thì HĐ → `Cancelled` (không hoàn tiền trong scope bài — ghi note).

---

## 4. Các phase

Ước lượng ~**8–10 ngày làm việc**. Mỗi phase có **Definition of Done**. Làm tuần tự; không nhảy phase trừ khi phase trước Done.

```text
P0 Ảnh ERD
 └─ P1 Nền tảng (BE skeleton + FE layout)
      └─ P2 Auth
           ├─ P3 Xem sân / slot          (public)
           └─ P4 Hồ sơ user
                └─ P5 Đặt / hủy sân      (cốt lõi)
                     ├─ P6 Hóa đơn user
                     └─ P7 Admin catalog (user / sân / slot / DV)
                          └─ P8 Admin vận hành (booking + HĐ + báo cáo)
                               └─ P9 Cứng hóa + nộp bài
```

P3 và P4 có thể song song sau P2. P6 và P7 có thể song song sau P5.

---

### Phase 0 — Đóng tài liệu nộp bài

**Mục tiêu:** Ảnh ERD khớp bản chốt 14/08.

**Việc**

- Sửa `docs/erd.png`:
  - Thêm entity **Services**
  - Nối TimeSlots — Bookings (`1 — 0..N`, ghi chú *active ≈ 0..1*)
  - Nối Services — BookingServices
  - Bookings — Invoices vẽ **0..1** (`o|`), không vẽ 1—1
  - CourtTypes — Courts giữ **1 — 1..N**
- Context diagram: **chỉ vẽ nếu đề bắt buộc**. Flow đã có trong features.

**Done khi:** ảnh đủ 9 entity + 9 quan hệ, cardinality đúng.

**Không viết Java.**

---

### Phase 1 — Nền tảng project

**Mục tiêu:** WAR chạy được, nối DB, có layout chung.

#### BE

- NetBeans Ant WAR, `mssql-jdbc` + JSTL trong `web/WEB-INF/lib`
- `DBUtils.getConnection()` đọc `db.properties` (không hard-code password vào class)
- `PasswordUtil.sha256Hex(String)` — phải ra đúng hash seed
- `CsrfUtil`, `Validation` (email, phone VN, required, range)
- `EncodingFilter` UTF-8
- Model POJO khớp 9 bảng
- Trang lỗi 403 / 404 / 500 qua `web.xml`

**Smoke BE:** class nhỏ / servlet `/health` in `DB OK` + hash `Admin@123` khớp seed.

#### FE

- `header.jsp` / `footer.jsp` (logo SportZone, nav theo role)
- `app.css` (màu sport, bảng, badge status)
- Home tĩnh tạm (U19 khung): 2 loại sân, CTA đăng nhập / xem sân

**Done khi:** deploy Tomcat, mở `/` thấy layout, `/health` nối được `SportZoneDB`.

---

### Phase 2 — Auth & phân quyền — A1 A2 U1 U2 U3

#### BE

- `AccountDAO`: findByEmail, insert, updateLastLogin
- `AuthService`: login (hash + so sánh), register (email/phone unique, role = User, status = Active)
- `LoginServlet`, `LogoutServlet`, `RegisterServlet`
- Session: `account` (object), không lưu password
- `AuthFilter`: chặn URL cần login → redirect `/login`
- `RoleFilter`: `/admin/*` chỉ Admin
- Frozen: login **được**; flag `frozen` trên session để P5 chặn đặt sân
- Register: validate server **và** client (U1)

#### FE

- `login.jsp`, `register.jsp` (CSRF, thông báo lỗi field)
- Nav: Guest = Login/Register; User/Admin = tên + Logout
- Redirect sau login: Admin → `/admin`, User → `/home`

**Done khi:** 4 account seed login đúng; Frozen login được nhưng sẽ không đặt được (P5); User gõ `/admin` → 403; logout hết session.

**Chưa làm:** đổi mật khẩu, sửa profile (P4).

---

### Phase 3 — Xem sân & lịch — U7 U8 U9 + một phần U19

#### BE

- `CourtDAO`: list (filter type, ẩn `IsActive=0`), getById
- `TimeSlotDAO`: listByCourtAndDate, only `Available` cho user
- Không cho đặt ở phase này

#### FE

- Home hoàn thiện: 2 card Tennis / Pickleball
- `court/list.jsp`: filter loại, badge status, giá VND
- `court/detail.jsp`: giá, vị trí, mô tả, ảnh (placeholder nếu null)
- Lịch slot theo ngày (date picker): Available / Booked / Closed
- Sân `Maintenance` / `Unavailable`: không hiện nút đặt

**Done khi:** lọc Tennis/Pickleball, xem Tennis Court 5 (Maintenance) không đặt được, xem slot seed theo ngày.

---

### Phase 4 — Hồ sơ cá nhân — U4 U5 U6

#### BE

- Update name / phone / email (unique, không đụng user khác)
- Đổi mật khẩu: nhập mật khẩu cũ, hash mới, CSRF bắt buộc
- Admin cũng dùng chung trang profile (sửa chính mình)

#### FE

- `profile.jsp`, `password.jsp`
- Thông báo thành công / lỗi

**Done khi:** user1 đổi tên + SĐT; đổi pass rồi login lại được; CSRF sai thì reject.

---

### Phase 5 — Đặt sân & hủy — U10 U11 U12 U13  *(cốt lõi)*

#### BE (transaction bắt buộc)

`BookingService.create(accountId, timeSlotId, serviceLines)`:

1. Account Active (không Frozen)
2. Đọc slot theo `timeSlotId`, JOIN sang Court; Court phải `Available`
3. Slot status `Available`, chưa qua giờ; không nhận `courtId` từ client làm nguồn dữ liệu
4. Snapshot `CourtPriceAtOrder`, cộng service (`PriceAtOrder`, `Quantity`)
5. Insert Booking `Pending` — **không** insert Invoice
6. Insert `BookingServices` nếu có
7. Slot → `Booked`
8. Unique index conflict → rollback, báo “khung giờ vừa được đặt”

`BookingService.cancelByUser(...)`:

- Chỉ booking của chính user
- Áp chính sách hủy mục 3
- Status → `Cancelled`, slot → `Available` nếu không còn booking active
- Phase này chưa có HĐ → chưa đụng invoice; P6/P8 bổ sung A30

`BookingDAO`: listMine (upcoming / history), getDetail (kèm services + invoice nếu có)

#### FE

- Form đặt: sân + ngày + slot + checkbox dịch vụ + tổng tiền preview
- `bookings/list.jsp`: tab sắp tới / lịch sử, badge status
- `bookings/detail.jsp`: sân, giờ, tiền, dịch vụ, “chưa có hóa đơn”
- Nút hủy + confirm; ẩn khi ngoài policy

**Done khi:** user1 đặt slot trống → Pending, **không** có dòng Invoices; đặt trùng slot → lỗi; Frozen không đặt; hủy đúng policy trả slot về Available.

---

### Phase 6 — Hóa đơn phía User — U14 U15 U16 U17 U18

Phụ thuộc P5. Admin lập HĐ nằm ở P8; phase này user **đọc** + gửi notice.

#### BE

- `InvoiceDAO.findByAccount`, `findByBooking`
- U16: booking không HĐ → trạng thái hiển thị “Chưa có hóa đơn” (không phải Unpaid)
- U17: ghi `PaymentNote` (“Tôi đã chuyển khoản” + optional mã GD)
  - Nếu **chưa có HĐ**: không tự tạo HĐ; chỉ lưu notice trên booking `Notes` hoặc bảng tạm — **đề xuất:** ghi `Bookings.Notes` prefix `[PAY-NOTICE]` để Admin thấy và bấm A27
  - Nếu **đã có HĐ Unpaid**: ghi `Invoices.PaymentNote`
- User không set `Paid`

#### FE

- `invoices/list.jsp` — chỉ booking đã có HĐ
- `invoices/detail.jsp` — số tiền, method (nếu có), status
- Trên booking detail: badge thanh toán + form “Tôi đã chuyển khoản”

**Done khi:** booking không HĐ không xuất hiện ở `/invoices`; notice hiện trên booking để Admin xử lý.

---

### Phase 7 — Admin catalog — A4–A18

Có thể song song P6.

#### BE

| Nhóm | ID | Hành vi |
|------|-----|---------|
| User | A4–A7 | Search/filter, detail + booking history, Active/Frozen, sửa name/phone/email. Không xóa account. Không đổi Role trên UI (tránh tự hạ admin). |
| Court | A8–A10 | List 10 sân, sửa name/location/price/description/imageURL, đổi Available/Maintenance/Unavailable. Không insert/delete court. Maintenance → (tuỳ chọn) đóng slot tương lai. |
| Slot | A11–A14 | Lọc ngày/sân, tạo slot, sửa giờ nếu chưa Booked, đóng (`Closed`) hoặc xóa nếu chưa Booked. Không xóa slot `Booked`. |
| Service | A15–A18 | CRUD. “Xóa” = `IsActive=0` (không hard-delete nếu đã dùng). |

#### FE

- Sidebar admin
- Bảng + form chuẩn (search, pagination đơn giản hoặc đủ seed)
- Badge status thống nhất (Pending vàng, Confirmed xanh, …)

**Done khi:** freeze user3 (đã seed) / unfreeze; đổi Tennis Court 1 giá; đóng 1 slot; ẩn 1 service.

---

### Phase 8 — Admin vận hành — A3 A19–A33

#### BE — Booking (A19–A24)

Chuyển trạng thái hợp lệ:

```text
Pending    → Confirmed | Cancelled
Confirmed  → Completed | Cancelled | NoShow
Completed  → (dừng)
Cancelled  → (dừng)
NoShow     → (dừng)
```

Hủy / NoShow: trả slot `Available` nếu không còn booking active.  
Hủy + đã có HĐ → invoice `Cancelled` cùng transaction.

#### BE — Invoice (A25–A31)

- A27: booking chưa HĐ → tạo `InvoiceCode` (`INV-yyyyMMdd-xxxx`), `SubTotal = TotalAmount`, `Discount=0`, `Unpaid`
- A27 lần 2 cùng booking → reject
- A28+A29: `Unpaid` → `Paid` + method Cash/BankTransfer + `PaidAt`
- A30: chỉ khi booking Cancelled (hoặc đi kèm A23)
- A31: trang in CSS `@media print` (không cần PDF lib)

#### BE — Dashboard + report (A3 A32 A33)

- A3: số booking hôm nay, sân Available/Maintenance, doanh thu **Paid** hôm nay
- A32: doanh thu Paid theo ngày / tuần / tháng (query `PaidAt`)
- A33: đếm booking theo loại sân + theo status

Doanh thu **chỉ cộng invoice `Paid`**. Unpaid / Cancelled không tính.

#### FE

- Dashboard cards
- Booking list filter (ngày, sân, status, user)
- Booking detail + nút Confirm / Complete / Cancel / NoShow (đúng transition)
- Invoice list/detail + “Lập hóa đơn” + “Xác nhận đã thu”
- Report: chọn khoảng + bảng (chart JS tuỳ chọn, không bắt buộc)
- `invoice/print.jsp`

**Done khi:** đi hết 1 vòng demo:

1. User đặt (không HĐ)  
2. Admin Confirm  
3. User bấm “đã CK”  
4. Admin A27 lập HĐ Unpaid  
5. Admin A28+A29 Paid + BankTransfer  
6. Admin Complete  
7. Dashboard / report thấy tiền  

Và vòng hủy: đặt → Admin hủy → không HĐ; đặt → lập HĐ → hủy → HĐ Cancelled.

---

### Phase 9 — Cứng hóa + nộp bài

#### BE

- CSRF mọi POST
- Validate server mọi form (độ dài cột schema)
- Thông báo lỗi SQL unique (email, phone, slot)
- Không lộ stack trace ra user
- Frozen / role / ownership (user không xem booking người khác)

#### FE

- Empty state, 403/404 thân thiện
- Mobile: bảng scroll, form stacked (Bootstrap)
- Nhãn VI + badge EN status (hoặc VI thống nhất — đề xuất **UI tiếng Việt**, status giữ mã EN trong DB)

#### Tài liệu

- Cập nhật `README.md`: chạy SQL, cấu hình `db.properties`, Tomcat, account demo
- Cập nhật `docs/tien-do.md`

**Done khi:** checklist mục 6 bên dưới pass; sẵn sàng record demo.

---

## 5. Mapping chức năng → phase

### User

| ID | Chức năng | Phase |
|----|-----------|-------|
| U1 Register | 2 |
| U2 U3 Login/Logout | 2 |
| U4 U5 U6 Profile / password | 4 |
| U7 U8 U9 Courts / slots | 3 |
| U10 Create booking (no invoice) | 5 |
| U11 U12 U13 My bookings / cancel | 5 |
| U14–U18 Invoices / notice | 6 |
| U19 Home | 1 khung, 3 hoàn thiện |

### Admin

| ID | Chức năng | Phase |
|----|-----------|-------|
| A1 A2 Login/Logout | 2 (chung) |
| A3 Dashboard | 8 |
| A4–A7 Users | 7 |
| A8–A10 Courts | 7 |
| A11–A14 Slots | 7 |
| A15–A18 Services | 7 |
| A19–A24 Bookings | 8 |
| A25–A31 Invoices | 8 |
| A32 A33 Reports | 8 |

---

## 6. Checklist demo nộp bài

1. Login admin / user1 / user3(Frozen)  
2. Register user mới, validate sai email/phone  
3. Xem sân, lọc Pickleball, xem slot  
4. Đặt sân + 1 dịch vụ → Pending, **không** có HĐ  
5. Đặt trùng slot → báo lỗi  
6. User hủy trước 2 giờ → slot Available  
7. Admin Confirm → Complete  
8. Admin lập HĐ → Unpaid → Paid Cash  
9. User xem HĐ của mình, không thấy HĐ người khác  
10. Hủy booking đã có HĐ → HĐ Cancelled  
11. Freeze user → không đặt được  
12. Report doanh thu chỉ tính Paid  
13. In hóa đơn ra được (print view)

---

## 7. Key Decisions (chờ xác nhận)

| # | Quyết định | Phương án đề xuất | Phương án khác |
|---|------------|-------------------|----------------|
| D1 | Tách FE/BE | 1 WAR, layer BE/FE trong từng phase | 2 app (REST + SPA) — **không khuyến nghị** |
| D2 | Servlet API | Tomcat 9 + `javax.*` | Tomcat 10 + `jakarta.*` |
| D3 | Hash | Giữ SHA-256 khớp seed | Đổi BCrypt + update seed |
| D4 | UI kit | Bootstrap 5 CDN | CSS thuần |
| D5 | Ảnh sân | URL text, không upload | Upload `multipart` |
| D6 | Policy hủy User | ≥ 2 giờ trước giờ chơi | 0 giờ / 1 giờ / chỉ Pending |
| D7 | U17 khi chưa có HĐ | Ghi `Bookings.Notes`, Admin mới A27 | Tự tạo HĐ Unpaid giúp Admin |
| D8 | Hard-delete service/slot | Soft: `IsActive=0` / `Closed` | DELETE nếu chưa dùng |
| D9 | Ngôn ngữ UI | Tiếng Việt, mã status EN trong DB | Toàn EN |
| D10 | Context diagram | Không vẽ trừ khi đề bắt | Vẽ ngay Phase 0 |

---

## 8. Rủi ro

| Rủi ro | Xử lý |
|--------|--------|
| Unique index double-book | Catch SQL exception, không tin check-then-act |
| Client gửi `courtId` không khớp slot | Service bỏ qua `courtId` cho persistence; luôn suy ra Court qua `TimeSlotID` |
| Quên CSRF trên 1 form | Phase 9 grep mọi POST |
| Scope creep (chart, upload, PDF) | Cắt; print CSS thay PDF |
| Tomcat 10 vs 9 nhầm package | Chốt D2 trước khi scaffold |

---

## 9. Thứ tự làm sau khi duyệt

1. Chốt bảng Key Decisions (mục 7) — sửa plan nếu chọn khác.  
2. Phase 0 (ảnh ERD) + Phase 1 (scaffold) trong ngày đầu.  
3. Phase 2 → 5 là đường găng (auth → xem sân → đặt sân).  
4. Phase 6 + 7 song song được.  
5. Phase 8 khép vòng tiền.  
6. Phase 9 rồi dừng feature mới.

---

## 10. Việc cố ý không làm

- Payment gateway (PayOS, VNPay, …)
- Tạo/xóa CourtType hoặc Court
- Chat, email SMTP, reset password qua mail
- App mobile / PWA
- Multi-tenant, nhiều chi nhánh
- Unit test framework đầy đủ (smoke thủ công + checklist mục 6 là đủ bài)

---

*Bản này để duyệt trước khi viết code. Sau khi chốt, cập nhật `docs/tien-do.md` và bắt đầu Phase 0–1.*
