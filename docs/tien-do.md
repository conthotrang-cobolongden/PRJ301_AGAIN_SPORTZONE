# Nhật ký tiến độ — SportZone Management

**Môn:** PRJ301 — Assignment cá nhân  
**Sinh viên:** Nguyễn Hoàng Tùng (SE203366)  
**Hệ thống:** Quản lý tổ hợp sân Tennis + Pickleball  

---

## Tổng quan tiến độ

| Hạng mục | Trạng thái | Ghi chú |
|----------|------------|---------|
| Phân tích chức năng Admin / User | **Xong** | `docs/admin-user-features.md` — đã khớp ERD chốt |
| Schema + seed SQL Server | **Xong** | `sql/01`, `sql/02` — comment khớp 1:0..1 invoice |
| Logic ERD (tài liệu) | **Xong — chốt 14/08/2026** | `docs/logic-erd.md` |
| Ảnh ERD nộp bài | **Đang làm** | `docs/erd.png` — tự vẽ theo bản chốt; còn thiếu Services + 2 đường nối |
| Context diagram | Chưa có file riêng | Flow đã tóm tắt trong features + logic-erd |
| Java web (Servlet / JSP / Filter) | **P1 xong (17/08)** | Scaffold BE + FE layout + `/health`; WAR đã build. Chi tiết ở mục 17/08 bên dưới |
| Phase 2 — Auth & phân quyền | **Xong (17/08)** | Code + smoke test 6/6 DoD pass trên Tomcat. Kế hoạch: [`phase-2-auth.md`](phase-2-auth.md) |
| Phase 3+4 — Xem sân & Hồ sơ (U4–U9) | **Code xong (17/08), chờ test** | BE + FE theo demo kit đã duyệt; compile sạch. Chi tiết mục bên dưới |
| CRUD + phân quyền + thanh toán | Chưa bắt đầu | Từ P5 trở đi theo kế hoạch 6 phase |

**Ước lượng:** phân tích + database ~ **35%** assignment. Hôm nay chốt xong phần mô hình dữ liệu trên giấy.

---

## 2026-08-17 (đêm) — Hoàn tất code Phase 3+4 (Xem sân & Hồ sơ, U4–U9)

### Mục tiêu

Phase 1 của kế hoạch 6 phase (đã duyệt): P3 xem sân/slot + P4 hồ sơ/mật khẩu, FE restyle theo demo kit `design-system/sportzone/preview/design-review-kit.html` (bản duyệt: layout cũ EliteAuto + màu SportZone).

### Đã làm

| Phần | Nội dung |
|------|----------|
| DAO | `CourtDAO` (JOIN CourtTypes, filter loại, đếm theo loại), `TimeSlotDAO` (slot theo sân+ngày, CRUD nền cho admin P7), `AccountDAO` +4 method profile |
| Service | `ProfileService` (U5 validate + check trùng loại trừ mình; U6 đổi mật khẩu) |
| Servlet | `/courts`, `/court?id=&date=` (kẹp ngày hôm nay→+6, đánh dấu slot đã qua, sinh dãy pill chọn ngày), `/profile`, `/password`; `HomeServlet` lấy số sân thật |
| FE | Refresh `app.css` theo demo kit (nút pill, filter pill, date pill, input nền kem, card bo lớn, hero card, footer card); restyle `court/list+detail`, `home/index`, `account/profile+password`, `footer`; cập nhật `MASTER.md` |

Compile: Ant + JDK 8 → **BUILD SUCCESSFUL**, 0 lỗi. Chi tiết file trong `rules/LOG-LAM-VIEC.md` entry mới nhất.

### Checklist test (chạy Tomcat)

1. `/home` — hero mới, số sân Tennis/Pickleball đúng seed (5/5).
2. `/courts` — 10 sân; filter Tennis (5) / Pickleball (5); Court 5 hiện badge Bảo trì, nút "Xem chi tiết" (không phải "Đặt sân").
3. `/court?id=1` — dãy pill 7 ngày, slot hôm nay đã qua giờ mờ đi; slot trống hiện giá; bấm slot → `/booking/create?...` (404 — đúng, P5 chưa làm).
4. `/court?id=5` — cảnh báo bảo trì, không có nút đặt.
5. Login `user1@sportzone.local` / `User@123` → `/profile` sửa tên/email/phone (trùng email user2 phải báo lỗi); `/password` đổi mật khẩu (sai mật khẩu cũ báo lỗi đúng field).

### Việc tiếp theo

Phase 2 của kế hoạch — **P5 đặt & hủy sân (U10–U13)**: BookingService transaction 8 bước, chống trùng slot bằng unique index, policy hủy (Pending/Confirmed, trước giờ ≥2h, chưa Paid).

---

## 2026-08-17 (chiều) — Hoàn tất Phase 2 (Auth & phân quyền)

### Mục tiêu ngày

Code A1 A2 U1 U2 U3 theo đúng quán triệt trong [`phase-2-auth.md`](phase-2-auth.md), compile sạch, smoke test thật trên Tomcat.

### File mới / sửa

| File | Loại | Vai trò |
|------|------|---------|
| `dao/AccountDAO.java` | Mới | findByEmail / findById (JOIN Roles), existsByEmail / existsByPhone, insert, updateLastLogin; `mapAccount` dùng chung |
| `service/AuthService.java` | Mới | login (hash + so khớp), register (validate + check trùng); inner class `RegisterResult` |
| `util/SessionUtil.java` | Mới | `storeLoggedInAccount` (xóa hash trước khi lưu session), `isLoggedIn` |
| `controller/LoginServlet.java` | Mới | GET form, POST login, redirect theo `redirectAfterLogin` |
| `controller/RegisterServlet.java` | Mới | GET form, POST register, catch SQLState 23000 chống race |
| `controller/LogoutServlet.java` | Mới | invalidate session, redirect `/home` |
| `filter/AuthFilter.java` | Mới | Chặn URL cần login, lưu target GET vào session |
| `filter/RoleFilter.java` | Mới | `/admin/*` chỉ Admin, sai role → 403 |
| `views/auth/login.jsp`, `register.jsp` | Mới | Form + CSRF + lỗi field + JS validate client |
| `model/Account.java` | Sửa | Thêm `roleName` (không phải cột DB) |
| `util/AppConstants.java` | Sửa | Thêm key session frozen / redirect, role ID, attr fieldErrors |
| `util/Views.java` | Sửa | Thêm LOGIN, REGISTER |
| `web.xml` | Sửa | 3 servlet + 2 filter; thứ tự Encoding → Auth → Role; AuthFilter khai sẵn pattern P3–P8 |
| `views/layout/header.jsp` | Sửa | Ẩn link Dashboard Admin tới P8; thêm banner Frozen |

### Smoke test trên Tomcat 9 (deploy qua `conf/Catalina/localhost/SportZoneManagement.xml` → `build/web`)

| # | DoD | Kết quả |
|---|-----|---------|
| 1 | 4 account seed login đúng; Frozen login được kèm banner khóa | Pass — admin/user1/user2 login 302 về /home; user3 Frozen login được, banner hiện |
| 2 | Sai mật khẩu hoặc sai email ra cùng thông báo chung | Pass — cả hai case đều "Email hoặc mật khẩu không đúng." |
| 3 | Đăng ký mới; trùng email/phone báo lỗi đúng field; sai định dạng bị chặn | Pass — register hợp lệ 302 + tự login; 4 lỗi validate trả đúng field |
| 4 | User gõ `/admin` nhận 403; chưa login bị đẩy về `/login`, login xong trả về đúng trang định vào | Pass — user1 GET /admin = 403; /bookings → /login → login → 302 về /bookings |
| 5 | Logout xóa session, vào lại trang cần login bị chặn | Pass — logout 302 /home, sau đó /bookings bị chặn về /login |
| 6 | POST không có CSRF token bị từ chối | Pass — 403 |

Compile: 28 file nguồn, 29 class, 0 lỗi 0 warning (JDK 8 + servlet-api Tomcat 9). Tài khoản test tạo khi smoke đã xóa khỏi DB.

### Ghi chú kỹ thuật

- `build/web` được cập nhật thủ công (copy classes + web.xml + JSP mới) vì chưa chạy Ant build lại; Tomcat tự reload khi web.xml đổi. Lần build NetBeans kế tiếp sẽ ghi đè đồng bộ.
- `/bookings`, `/profile` hiện trả 404 khi đã login — đúng dự kiến, servlet thuộc P4/P5 chưa tồn tại. AuthFilter chỉ làm nhiệm vụ chặn.

### Việc tiếp theo

1. P3 (xem sân / slot) và P4 (hồ sơ user) — có thể song song sau P2.
2. Trả nợ ảnh ERD (P0).

---

## 2026-08-17 — Hoàn tất Phase 1 (nền tảng project)

### Mục tiêu ngày

Scaffold app Java web theo `implementation-plan.md`, đối chiếu Definition of Done của P1.

### Đã làm — đối chiếu DoD của P1

| Yêu cầu P1 | Trạng thái | File |
|------------|-----------|------|
| WAR chạy được, có JSTL + mssql-jdbc | Xong | `web/WEB-INF/lib/` (jstl-1.2, mssql-jdbc-12.4.2.jre8), `dist/SportZoneManagement.war` đã build 07:53 |
| `DBUtils.getConnection()` đọc `db.properties`, không hard-code password | Xong | `dao/DBUtils.java` + `src/java/db.properties`; hỗ trợ SQL auth lẫn `integratedSecurity` |
| `PasswordUtil.sha256Hex` ra đúng hash seed | Xong | `util/PasswordUtil.java`; SHA-256(`Admin@123`) = `e86f78a8...` khớp `sql/02` |
| `CsrfUtil`, `Validation`, `DateUtil` | Xong | `util/CsrfUtil.java`, `util/Validation.java` (email, phone VN, length, range), `util/DateUtil.java` |
| `EncodingFilter` UTF-8 | Xong | `filter/EncodingFilter.java`, map `/*` trong `web.xml` |
| 9 model POJO khớp 9 bảng | Xong | `model/`: Account, Role, Court, CourtType, TimeSlot, Booking, BookingService, Service, Invoice |
| Trang lỗi 403 / 404 / 500 | Xong | `web/WEB-INF/views/error/*.jsp`, khai báo trong `web.xml` |
| `/health` in DB OK + hash khớp seed | Xong | `controller/HealthServlet.java` + `util/HealthCheck.java` (ping DB, đếm bảng, so hash) |
| Layout header/footer + home tĩnh + `app.css` | Xong | `views/layout/header.jsp`, `footer.jsp`, `views/home/index.jsp`, `assets/css/app.css` — Bootstrap 5 CDN, UI tiếng Việt, nav theo role |
| `index.jsp` redirect về `/home` | Xong | `web/index.jsp` |

### Lệch so với plan gốc (đã chốt xử lý)

1. **Build:** plan ghi Maven WAR, thực tế là **NetBeans Ant** (`build.xml` + `nbproject/`). Chốt giữ NetBeans; đã sửa lại `implementation-plan.md` mục 2.2.
2. **Servlet API:** dùng `javax.servlet` + web-app 3.1 (Tomcat 9) — đúng đề xuất D2.

### Nợ / việc còn lại

1. **P0 ảnh ERD** vẫn chưa sửa (`erd.png` từ 13/08): còn thiếu entity Services + 2 đường nối (TimeSlots—Bookings, Services—BookingServices).
2. **Xác nhận chạy thật:** WAR đã build nhưng chưa deploy Tomcat kiểm tra `/` và `/health` trong nhật ký này — cần tự chạy trước khi vào P2.
3. Key Decisions D1–D10 thực tế đã chọn theo phương án đề xuất; chưa ai phản đối thì coi như chốt.

### Việc tiếp theo

1. Quán triệt Phase 2 (Auth & phân quyền) trong [`phase-2-auth.md`](phase-2-auth.md) rồi mới code.
2. Trả nợ ảnh ERD (P0) song song.

---

## 2026-08-14 — Chốt ERD và quán triệt tài liệu

### Mục tiêu ngày

Đối chiếu cardinality với schema, chốt ERD, rồi chỉnh mọi tài liệu cho **một bản duy nhất** — không còn chỗ ghi 1:1 invoice lẫn 1:0..1.

### Quyết định đã chốt

| # | Quan hệ | Cardinality |
|---|---------|-------------|
| 1 | Roles → Accounts | 1 : 0..N |
| 2 | CourtTypes → Courts | 1 : 1..N (chỉ Tennis + Pickleball, không thêm loại) |
| 3 | Courts → TimeSlots | 1 : 0..N |
| 4 | Accounts → Bookings | 1 : 0..N |
| 5 | Courts → Bookings | 1 : 0..N |
| 6 | TimeSlots → Bookings | 1 : 0..N (active ≈ 0..1) |
| 7 | Bookings → Invoices | **1 : 0..1** |
| 8 | Bookings → BookingServices | 1 : 0..N |
| 9 | Services → BookingServices | 1 : 0..N |

Quy tắc invoice (khớp A27):

1. U10 tạo Booking (`Pending`) — **không** sinh hóa đơn.  
2. A27 lập hóa đơn khi bắt đầu thu — mặc định `Unpaid`.  
3. Hết hạn + đã có HĐ → giữ **Unpaid**. Chưa có HĐ → xử lý trên Booking (`Cancelled`), không có dòng Unpaid.  
4. Admin chốt `Paid` + method. Hủy booking kèm HĐ → invoice `Cancelled`.  
5. `Unpaid` nằm ở `Invoices.PaymentStatus`, không ở `Bookings.Status`.

### Việc đã làm

1. So tài liệu cũ với bản chốt: v1 lẫn “1—1” và “tối đa một hóa đơn”; mermaid CourtTypes là 0..N.  
2. Đánh dấu bản cũ **đã bỏ**: `docs/logic-erd.v1-deprecated.md`.  
3. Viết lại `docs/logic-erd.md` — 9 quan hệ, luồng invoice, hướng dẫn vẽ `erd.png`.  
4. Khớp `docs/admin-user-features.md`: A27, U10, U14–U17, mục 4 trạng thái thanh toán.  
5. Khớp `sql/README.md` (bảng cardinality + quy tắc invoice).  
6. Sửa comment schema `1:1` → `1:0..1`; ghi chú seed: invoice là tùy chọn.  
7. **Không** sửa `docs/erd.png` (tự chỉnh).

### File thay đổi hôm nay

| File | Loại | Nội dung |
|------|------|----------|
| `docs/logic-erd.md` | Sửa | Bản ERD hiện hành |
| `docs/logic-erd.v1-deprecated.md` | Thêm | Bản cũ, banner đã bỏ |
| `docs/admin-user-features.md` | Sửa | Invoice 1:0..1, Unpaid, A27/U10 |
| `sql/README.md` | Sửa | Cardinality + invoice rule |
| `sql/01_SportZoneDB_Schema.sql` | Sửa comment | `1:0..1`, tạo HĐ khi thu |
| `sql/02_SportZoneDB_Seed.sql` | Sửa comment | Booking không bắt buộc có HĐ |
| `docs/erd.png` | Có sẵn, chưa sửa | Cần vẽ thêm Services + 2 quan hệ |
| `docs/tien-do.md` | Thêm | File này |

### Việc còn lại (ảnh ERD)

Trên `docs/erd.png` cần:

- Thêm entity **Services**  
- Nối **TimeSlots — Bookings** (`1 — 0..N`, ghi chú active ≈ 0..1)  
- Nối **Services — BookingServices**  
- Bookings–Invoices vẽ **`o|` (0..1)**, không vẽ 1—1  
- CourtTypes–Courts giữ **1 — 1..N**

### Việc ngày mai / tiếp theo

1. Hoàn tất ảnh ERD theo checklist trên.  
2. (Tuỳ chọn) Context diagram ra file riêng nếu đề bài yêu cầu.  
3. Bắt đầu Java web: project structure, `DBUtils`, login + Role filter.  
4. Seed đã có 3 booking mẫu kèm invoice — đủ để test màn thanh toán sau này.

---

*Cập nhật lần cuối: 2026-08-17.*
