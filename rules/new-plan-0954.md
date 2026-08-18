# BÁO CÁO: NÊN BẮT ĐẦU LẠI TỪ ĐÂU

## 1. Ngữ cảnh đã nắm (tóm tắt)

- **Dự án:** PRJ301 SportZone — quản lý tổ hợp sân Tennis + Pickleball (cố định 2 loại × 5 sân), 2 role Admin/User, chức năng A1–A33 + U1–U19.
- **Stack chốt (không đổi):** Tomcat 9 + `javax.servlet` + JSP/JSTL 1.2 + Bootstrap 5 CDN + SQL Server (`SportZoneDB`, 9 bảng), build NetBeans Ant WAR, JDBC PreparedStatement thuần, SHA-256 hex không salt, CSRF hidden field mọi form POST, JSP nằm dưới `WEB-INF/views/`, khai báo servlet thủ công trong web.xml (không `@WebServlet`).
- **Lộ trình gốc (implementation-plan.md):** P0 ảnh ERD → P1 Nền tảng → P2 Auth → P3 Xem sân/slot + P4 Hồ sơ → P5 Đặt/hủy sân (cốt lõi) → P6 Hóa đơn user + P7 Admin catalog → P8 Admin vận hành → P9 Cứng hóa. 11 quy tắc nghiệp vụ khóa cứng (U10 không tạo Invoice, invoice 1:0..1, snapshot giá, chống double-book bằng filtered unique index, Frozen không được đặt sân, hủy ≥2 giờ...).
- **Rules làm việc:** đọc `QUY-TAC-LAM-VIEC.md` trước khi code, làm xong ghi log vào `LOG-LAM-VIEC.md` theo template, gắn tag chức năng (A27, U10...) trong code, ưu tiên nghiệp vụ > design > thói quen.
- **Design:** `design-system/sportzone/preview/design-review-kit.html` — 15 màn hình (C1–C10 khu khách, A1–A4 admin), theme xanh–gold, token CSS đầy đủ, component library (badge, pill, slot-grid, bảng, invoice-paper, admin shell...).
- **Database:** bản SQL trong `repository\` là bản MỚI (ERD v3): `Bookings` bỏ `CourtID` (sân suy ra qua TimeSlot), `BookingServices` PK ghép, index `UQ_Bookings_Slot_Active` chống trùng giờ. Seed: 4 account (admin/Admin@123, user1+user2/User@123, user3 Frozen), 10 sân, 560 slot 7 ngày × 8 khung giờ.

## 2. Hiện trạng codebase

**Nơi làm việc (mày đã chọn): `repository\PRJ301_AGAIN_SPORTZONE\`**

Còn:
- ✅ 16 trang JSP đầy đủ cho P1–P6 (home, login, register, court list/detail, booking create/list/detail, invoice list/detail, profile, password, 403/404/500, layout header/footer) — đây là "hợp đồng" backend phải tuân theo (tên attribute, URL, form field đã chốt trong JSP).
- ✅ Design kit, docs bản mới (ERD v3, tien-do mới nhất), rules, SQL bản mới, skeleton NetBeans (build.xml, nbproject), 2 jar (jstl-1.2, mssql-jdbc-12.4.2.jre8).

Mất hoàn toàn:
- ❌ Toàn bộ `src/java` (controller, dao, model, service, filter, util) — kể cả P1+P2.
- ❌ `web.xml`, `db.properties`, `assets/css/app.css` (header.jsp vẫn link tới app.css → sẽ 404 CSS).

Tham khảo: repo git cũ ở root (HEAD `58bf45f`) còn nguyên code P1+P2 đã test pass — chỉ dùng để đối chiếu, không restore.

## 3. Kết luận: BẮT ĐẦU LẠI TỪ PHASE 1 (mày đã chốt)

Làm trong `repository\PRJ301_AGAIN_SPORTZONE\SportZoneManagement\SportZoneManagement\`. Việc đầu tiên nên làm: `git init` tại `repository\PRJ301_AGAIN_SPORTZONE` để commit theo từng phase/file (phục vụ việc mày duyệt từng file).

**Thứ tự file đề xuất cho P1 (mỗi file trình mày duyệt riêng, xong mới qua file kế):**

| # | File | Vai trò |
|---|---|---|
| 1 | `src/java/com/sportzone/dao/db.properties` | Connection string SQL Server |
| 2 | `dao/DBUtils.java` | `getConnection()` đọc db.properties |
| 3 | `util/PasswordUtil.java` | SHA-256 hex UTF-8 — phải khớp hash seed |
| 4 | `util/AppConstants.java` | Hằng số role/status/session key |
| 5 | `util/Views.java` | Đường dẫn JSP tập trung |
| 6 | `util/CsrfUtil.java` | Token session + validate |
| 7 | `util/Validation.java` | Validate email/phone/password |
| 8 | `util/DateUtil.java` | Xử lý ngày/giờ cho slot |
| 9 | `util/SessionUtil.java` | Đọc account/role/frozen từ session |
| 10 | 9 file `model/` | Account, Role, Court, CourtType, TimeSlot, Service, Booking, BookingService, Invoice (theo ERD v3 — Booking KHÔNG có courtId) |
| 11 | `filter/EncodingFilter.java` | UTF-8 `/*` |
| 12 | `web/WEB-INF/web.xml` | Khai báo filter/servlet/error-page/session-timeout 30 |
| 13 | `controller/HealthServlet.java` | `/health` kiểm tra nối DB |
| 14 | `web/assets/css/app.css` | Viết lại từ token trong design-review-kit.html |
| 15 | Rà soát `layout/header.jsp` + `footer.jsp` | Khớp contract session (`account`, `roleName`, `frozen`, `csrfToken`) |

**Definition of Done P1:** build WAR sạch, deploy Tomcat 9, `/health` trả OK + nối được `SportZoneDB`, layout home render đúng design (không 404 CSS), hash của `Admin@123`/`User@123` khớp seed.

## 4. Lộ trình tiếp theo (sau P1, vẫn nguyên tắc từng file một)

- **P2 Auth:** AccountDAO → AuthService → Login/Register/LogoutServlet → AuthFilter → RoleFilter. Done: 4 account seed login đúng, User vào `/admin` bị 403, CSRF sai bị chặn.
- **P3 Xem sân (U7–U9):** CourtDAO, TimeSlotDAO, `/courts`, `/court`.
- **P4 Hồ sơ (U4–U6):** ProfileService, `/profile`, `/password`.
- **P5 Đặt/hủy (U10–U13, cốt lõi):** BookingService transaction 8 bước, chống double-book, policy hủy ≥2 giờ.
- **P6 → P7 → P8 → P9:** theo implementation-plan.md.

Mỗi phase: tao trình từng file → mày duyệt → ghi log `LOG-LAM-VIEC.md` → commit. Không code quá 1 file mỗi lượt chờ duyệt.

## 5. Lưu ý quan trọng khi code lại

1. **JSP là hợp đồng cứng:** 16 JSP còn lại đã chốt tên attribute/URL/field (vd `fieldErrors` key `name` ở profile nhưng `fullName` ở register; `paymentStatus = NULL` hiện "Chưa có hóa đơn"; `dateOptions` 7 ngày...). Backend viết ra phải khớp đúng, không được bắt JSP đổi theo.
2. **ERD v3:** `Bookings` không có `CourtID` — mọi truy vấn sân của booking phải JOIN qua `TimeSlots`. Code P1+P2 cũ trong git dựa schema cũ, khi tham khảo phải tỉnh táo điểm này.
3. **app.css phải viết lại từ design kit** (1371 dòng HTML chứa toàn bộ token + component CSS) — đây là việc thuộc P1 vì mọi trang đang link tới nó.
4. **Seed chỉ có 8 khung giờ/ngày** (06–09, 16–21) trong khi design A4 vẽ 16 giờ 06–22 — demo vẫn chạy đúng, không cần sửa DB.
5. **db.properties cũ dùng user `sportzone`/`SportZone@123`** — cần chắc chắn login này tồn tại trong SQL Server của mày, nếu không thì đổi sang SA.

## Hành động ngay sau khi duyệt

KHÔNG code gì thêm (theo yêu cầu của mày). Chờ mày ra lệnh bắt đầu file đầu tiên của P1 (đề xuất: `db.properties` + `DBUtils.java`).