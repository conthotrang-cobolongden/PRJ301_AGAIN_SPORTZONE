# Log làm việc chung — Qwen (UI) & Deepseek (Logic)

> Entry mới nhất nằm trên cùng. Ghi theo template trong `rules/QUY-TAC-LAM-VIEC.md` mục 3.2.
> Ai làm người đó ghi, không sửa entry của người kia.

---
## [2026-08-23 16:08] — CODEX — Hoàn tất Phase 4 Hồ sơ cá nhân

**Task:** Triển khai Phase 4 theo Code Guidance Mode: U4 xem hồ sơ, U5 cập nhật hồ sơ và U6 đổi mật khẩu dùng chung cho User/Admin.

**Trạng thái:** Xong — targeted tests, Clean and Build và 12/12 Tomcat smoke scenarios đều pass.

### Đã làm
- [x] Mở rộng `AccountDAO`: kiểm tra email/phone loại trừ account hiện tại, cập nhật hồ sơ và password hash.
- [x] Tạo `ProfileService`: chuẩn hóa dữ liệu, validate server, kiểm tra unique và xác minh mật khẩu hiện tại.
- [x] Tạo `ProfileServlet` cho `GET/POST /profile`; lấy account ID từ session, đồng bộ session sau cập nhật và dùng PRG redirect.
- [x] Tạo `PasswordServlet` cho `GET/POST /password`; CSRF bắt buộc, không giữ password trong request/session và giữ đăng nhập sau khi đổi mật khẩu.
- [x] Admin, User và Frozen User dùng chung giao diện profile; không cho sửa role/status.
- [x] Đồng bộ giới hạn form với schema: email 100 ký tự, password 8–64 ký tự.
- [x] Không truyền `LocalDateTime` vào `fmt:formatDate`; Servlet bind chuỗi `memberSince`.
- [x] Thêm test DAO, Service, Servlet, routing và JSP contract.
- [x] Clean and Build thành công: 36 source files, WAR 2,113,915 bytes.
- [x] Smoke 12/12: auth redirect, profile GET, CSRF 403, duplicate email, update profile, sai mật khẩu cũ, đổi mật khẩu, đăng nhập lại, khôi phục mật khẩu seed, Admin profile và Frozen profile.

### File đã tạo / sửa
| File | Loại | Tóm tắt thay đổi |
|------|------|------------------|
| `SportZoneManagement/src/java/dao/AccountDAO.java` | Sửa | Thêm persistence cho U5/U6 |
| `SportZoneManagement/src/java/service/ProfileService.java` | Mới | Nghiệp vụ profile và đổi mật khẩu |
| `SportZoneManagement/src/java/controller/ProfileServlet.java` | Mới | `GET/POST /profile` |
| `SportZoneManagement/src/java/controller/PasswordServlet.java` | Mới | `GET/POST /password` |
| `SportZoneManagement/web/WEB-INF/web.xml` | Sửa | Khai báo và mapping hai Servlet Phase 4 |
| `SportZoneManagement/web/WEB-INF/views/account/profile.jsp` | Sửa | Schema maxlength và memberSince |
| `SportZoneManagement/web/WEB-INF/views/account/password.jsp` | Sửa | Password maxlength 64 |
| `SportZoneManagement/test/dao/ProfileAccountDAOContractTest.java` | Mới | DAO contract U5/U6 |
| `SportZoneManagement/test/service/ProfileServiceTest.java` | Mới | Test nghiệp vụ profile/password |
| `SportZoneManagement/test/controller/Phase4ServletTest.java` | Mới | Test HTTP/session/CSRF contract |
| `SportZoneManagement/test/utils/Phase4WebContractTest.java` | Mới | Test web.xml |
| `SportZoneManagement/test/utils/Phase4JspContractTest.java` | Mới | Test JSP contract |
| `SportZoneManagement/test/utils/Phase4WebBatchTest.java` | Mới | Chạy gộp test web Phase 4 |

### Chưa làm / tồn đọng
- `/health` đang trả 503 vì database có 10 bảng trong khi baseline HealthCheck kỳ vọng 9; response vẫn xác nhận `database=OK` và `passwordHash=OK`. Điều tra riêng, không thuộc lỗi Phase 4.
- Phase 5 đặt và hủy sân U10–U13 là hạng mục tiếp theo.

### Cần người dùng quyết định
- Không.

---

## [2026-08-23 13:36] — CODEX — Ban hành UI Rebuild Guardrails

**Task:** Xây bộ quy tắc cho phép rebuild giao diện nhưng không làm ảnh hưởng chức năng/backend hiện hành.
**Trạng thái:** Xong — chốt phương án Contract-Preserving UI Rebuild và thêm cổng bắt buộc vào AGENTS.md.

### Đã làm
- [x] Tạo `rules/UI-REBUILD-GUARDRAILS.md` khóa URL, HTTP method, form/input, CSRF, attribute/EL, JSTL nghiệp vụ, JavaScript hook và security contract.
- [x] Quy định rõ phần UI được phép thay đổi và backend/SQL/web.xml/build mặc định bị khóa.
- [x] Thêm Contract Snapshot, quy trình static comparison, runtime smoke và Contract Change Gate.
- [x] Thêm mục `UI Rebuild Safety — MANDATORY` vào `rules/AGENTS.md` để mọi task UI bắt buộc đọc guardrails.

### File đã tạo / sửa
| File | Loại | Tóm tắt thay đổi |
|------|------|------------------|
| `rules/UI-REBUILD-GUARDRAILS.md` | Mới | Contract và checklist an toàn khi rebuild UI |
| `rules/AGENTS.md` | Sửa | Bắt buộc áp dụng UI guardrails |
| `rules/LOG-LAM-VIEC.md` | Sửa | Ghi nhận task |

### Chưa làm / tồn đọng
- Chưa áp dụng guardrails cho một trang cụ thể; mỗi trang cần Contract Snapshot riêng trước khi rebuild.

### Ghi chú cho model còn lại
- UI-only task không được đổi backend contract. Nếu bắt buộc đổi, phải dừng và xin duyệt theo Contract Change Gate.

### Cần người dùng quyết định
- Không.

---

## [2026-08-23 13:22] — CODEX — Hoàn tất Phase 3 Xem sân & lịch

**Task:** Triển khai Phase 3 theo Code Guidance Mode: U7 xem/lọc danh sách sân, U8 xem chi tiết sân, U9 xem lịch slot theo ngày và phần số lượng sân của U19.
**Trạng thái:** Xong — người dùng đã áp dụng code, xác nhận targeted tests, Clean and Build và Tomcat smoke đều pass.

### Đã làm
- [x] Tạo `CourtDAO` đọc sân active, JOIN `CourtTypes`, filter Tennis/Pickleball, xem được sân Maintenance và đếm sân cho `/home`.
- [x] Tạo `TimeSlotDAO` đọc đủ `Available`, `Booked`, `Closed` theo sân + ngày và sắp xếp theo giờ bắt đầu.
- [x] Tạo `CourtsServlet` cho public `GET /courts`; whitelist filter và bind đúng contract JSP.
- [x] Tạo `CourtDetailServlet` cho public `GET /court`; validate ID, clamp ngày `[hôm nay, hôm nay + 6]`, sinh 7 `DateOption` và đánh dấu slot đã qua bằng `Clock`.
- [x] Thay count hard-code trên `HomeServlet` bằng dữ liệu thật từ `CourtDAO`.
- [x] Bổ sung JavaBeans alias `courtId`, `imageUrl`, `timeSlotId` để JSP EL đọc đúng.
- [x] Sửa `court/detail.jsp` không truyền `LocalTime` vào `fmt:formatDate`; giữ nguyên layout/CSS.
- [x] Thêm mapping `/courts`, `/court` trong `web.xml`; hai route giữ public, không thêm vào `AuthFilter`.
- [x] Cấu hình project dùng JDK 8 và JDBC JAR trên compile/test runtime classpath.
- [x] Thực hiện các vòng RED → GREEN cho model/view model, DAO, servlet, routing và JSP contract; người dùng xác nhận test và smoke runtime pass.

### File đã tạo / sửa
| File | Loại | Tóm tắt thay đổi |
|------|------|------------------|
| `SportZoneManagement/src/java/dao/CourtDAO.java` | Mới | U7/U8/U19 — list, filter, detail và count sân active |
| `SportZoneManagement/src/java/dao/TimeSlotDAO.java` | Mới | U9 — slot theo sân + ngày, đủ ba trạng thái |
| `SportZoneManagement/src/java/controller/CourtsServlet.java` | Mới | Public `GET /courts` |
| `SportZoneManagement/src/java/controller/CourtDetailServlet.java` | Mới | Public `GET /court`, date window và past-slot |
| `SportZoneManagement/src/java/controller/HomeServlet.java` | Sửa | Count Tennis/Pickleball từ database |
| `SportZoneManagement/src/java/model/Court.java` | Sửa | JavaBeans alias cho JSP EL |
| `SportZoneManagement/src/java/model/TimeSlot.java` | Sửa | Alias `timeSlotId`, giữ view flag `past` |
| `SportZoneManagement/src/java/model/DateOption.java` | Mới | View model cho dãy 7 ngày |
| `SportZoneManagement/web/WEB-INF/web.xml` | Sửa | Khai báo/mapping hai servlet Phase 3 |
| `SportZoneManagement/web/WEB-INF/views/court/detail.jsp` | Sửa | Render `LocalTime` an toàn bằng `c:out` |
| `SportZoneManagement/nbproject/*` | Sửa | Explicit JDK 8 và JDBC compile/test classpath |
| `SportZoneManagement/test/**` | Mới/Sửa | Contract/integration tests Phase 3 |

### Chưa làm / tồn đọng
- Phase 4 hồ sơ cá nhân và đổi mật khẩu (U4–U6) là hạng mục tiếp theo.
- Link `/booking/create` là điểm nối cho Phase 5; Phase 3 chưa triển khai đặt sân.

### Ghi chú cho model còn lại
- `TimeSlotDAO` Phase 3 chỉ đọc lịch; chưa thêm CRUD admin hoặc transaction method của Phase 5/P7.
- `selectedDateObj` là `java.sql.Date` cho JSTL; `slot.startTime` là `LocalTime` và JSP render bằng `c:out`.

### Cần người dùng quyết định
- Không.

---

## [2026-08-22 23:23] — CODEX — Hoàn tất Phase 2 Auth & phân quyền

**Task:** Triển khai A1, A2, U1, U2, U3 trên branch `codex/phase-2-auth` theo kế hoạch TDD.
**Trạng thái:** Xong — compile, 12 contract tests, WAR deploy và 11/11 smoke scenarios đều pass.

### Đã làm
- [x] Thêm đăng nhập, đăng ký, đăng xuất; CSRF cho POST và session không lưu password hash.
- [x] Thêm `AuthFilter`, `RoleFilter`, saved GET target và Admin authorization 403.
- [x] Thêm Account DAO/AuthService, mapping servlet/filter và chuẩn hóa lỗi login JSP.
- [x] Verify JDK 8: `ant clean compile-test` thành công; 12/12 test pass (`DBUtils`, `AccountDAO`, foundation/web/health, auth web, model, encoding/auth/role filter, service, controller).
- [x] `ant dist` và deploy Tomcat 9 thành công; WAR `2,058,025` byte.
- [x] User xác nhận 11/11 smoke scenarios pass: 4 seed login, Frozen banner, generic login error, CSRF 403, đăng ký/duplicate, saved redirect, Admin 403 và logout.

### File đã tạo / sửa
- `SportZoneManagement/src/java/dao/AccountDAO.java`
- `SportZoneManagement/src/java/service/AuthService.java`
- `SportZoneManagement/src/java/controller/LoginServlet.java`
- `SportZoneManagement/src/java/controller/RegisterServlet.java`
- `SportZoneManagement/src/java/controller/LogoutServlet.java`
- `SportZoneManagement/src/java/filter/AuthFilter.java`
- `SportZoneManagement/src/java/filter/RoleFilter.java`
- `SportZoneManagement/test/dao/AccountDAOContractTest.java`
- `SportZoneManagement/test/service/AuthServiceTest.java`
- `SportZoneManagement/test/controller/AuthServletTest.java`
- `SportZoneManagement/test/filter/AuthFilterTest.java`
- `SportZoneManagement/test/filter/RoleFilterTest.java`
- `SportZoneManagement/test/utils/AuthWebContractTest.java`
- `SportZoneManagement/web/WEB-INF/web.xml`
- `SportZoneManagement/web/WEB-INF/views/auth/login.jsp`

### Chưa làm / tồn đọng
- Không còn lỗi Phase 2 đã biết; Phase 3/4 là hạng mục tiếp theo.

---

## [2026-08-18] — CODEX — Rút gọn package và cấu hình DB theo yêu cầu

**Task:** Bỏ `com.sportzone`, dùng package ngắn và chuyển cấu hình kết nối vào thẳng `DBUtils.java`.
**Trạng thái:** Xong — Ant build, 6 targeted tests và Tomcat smoke đều pass.

### Đã làm
- [x] Chuyển source sang `dao`, `utils`, `controller`, `filter`, `model`.
- [x] Chuyển test sang package tương ứng và đổi test DB thành `DBUtilsContractTest`.
- [x] Xóa toàn bộ file cấu hình DB riêng; `dao.DBUtils` dùng trực tiếp `sa/12345` cho local SQL Server.
- [x] Đồng bộ class name trong `web.xml`, tài liệu kế hoạch và tài liệu Phase 2.
- [x] Build 21 source bằng JDK 8; chạy 6 test; smoke `/`, `/home`, `/health`, CSS đều HTTP 200.

### Lưu ý
- Credential DB hiện được Git track trong Java theo yêu cầu; cấu hình này chỉ dành cho môi trường học tập/local.

---

## [2026-08-18 23:27] — CODEX — Hoàn tất Phase 1 foundation trên branch feature/foundation

**Task:** Dựng lại toàn bộ nền tảng P1 theo ERD v3 và `CODING_CONVENTION.md`, build WAR và smoke test thật trên Tomcat 9.
**Trạng thái:** Xong — targeted tests, Ant build JDK 8 và runtime smoke đều pass.

### Đã làm
- [x] Tạo cấu hình DB classpath + `DBUtils`; kết nối thật `SportZoneDB` bằng JDBC.
- [x] Tạo utility nền tảng: password SHA-256, constants, view paths, validation, date/time, CSRF và session.
- [x] Tạo 9 model khớp ERD v3; `Booking` không có `courtID`, `BookingService` dùng khóa ghép.
- [x] Tạo `EncodingFilter`, `HomeServlet`, `HealthServlet`, `HealthCheck` và `web.xml` Servlet 3.1.
- [x] Khôi phục `app.css` từ design review kit và bổ sung contract class đang dùng trong 17 JSP.
- [x] Xóa scriptlet redirect khỏi `index.jsp`, thay bằng `<jsp:forward>`.
- [x] Build WAR bằng NetBeans Ant + JDK 1.8.0_172; smoke Tomcat 9 trên base sạch.

### File đã tạo / sửa
| File | Loại | Tóm tắt thay đổi |
|------|------|------------------|
| `SportZoneManagement/src/java/dao/DBUtils.java` | Mới | Nạp driver, mở connection |
| `SportZoneManagement/src/java/utils/*.java` | Mới | 8 utility nền tảng, gồm `HealthCheck` |
| `SportZoneManagement/src/java/model/*.java` | Mới | 9 model ERD v3 |
| `SportZoneManagement/src/java/filter/EncodingFilter.java` | Mới | UTF-8 cho toàn app |
| `SportZoneManagement/src/java/controller/HomeServlet.java` | Mới | `/home` U19 |
| `SportZoneManagement/src/java/controller/HealthServlet.java` | Mới | `/health` kiểm tra DB/schema/hash |
| `SportZoneManagement/web/WEB-INF/web.xml` | Mới | Filter, servlet, error page, timeout 30 phút |
| `SportZoneManagement/web/assets/css/app.css` | Mới | Design system xanh–gold và component JSP |
| `SportZoneManagement/web/index.jsp` | Sửa | Forward `/home` không dùng scriptlet |
| `SportZoneManagement/test/**` | Mới | 6 targeted contract/integration test |
| `.gitignore` | Sửa | Bỏ qua worktree và output NetBeans |

### Chưa làm / tồn đọng
- Chưa triển khai P2 Auth; đây là phase tiếp theo sau khi review/merge P1.
- Chưa push hoặc merge branch.
- Cấu hình kết nối sau refactor nằm trực tiếp trong `dao/DBUtils.java` theo yêu cầu mới.

### Ghi chú cho model còn lại
- Dùng `SessionUtil` với đúng key `account`, `roleName`, `frozen`, `csrfToken`; không lưu `passwordHash` trong session.
- Mọi truy vấn sân của Booking phải JOIN qua `TimeSlots`; không thêm `courtID` vào Booking.

### Cần người dùng quyết định
- Review/merge `feature/foundation`, sau đó mở `feature/auth` từ `dev` đã nhận P1.

---

## [2026-08-18 22:44] — CODEX — Đồng bộ tài liệu theo hiện trạng mới và ERD v3

**Task:** Đồng bộ `rules`, `docs`, `sql` để loại bỏ mâu thuẫn giữa lịch sử P1–P4, working copy mới và schema ERD v3.
**Trạng thái:** Xong — chỉ sửa tài liệu, không sửa code/SQL schema và không chạy build.

### Đã làm
- [x] Chốt `sql/01_SportZoneDB_Schema.sql` + `docs/erd-file/erd.md` là nguồn chuẩn dữ liệu: `Bookings` không có `CourtID`, sân suy ra qua `TimeSlotID`, index chống trùng là `UQ_Bookings_Slot_Active`.
- [x] Chốt `rules/new-plan-0954.md` là nguồn chuẩn hiện trạng: working copy mới phải làm lại từ P1; kết quả P1–P4 ngày 17/08 chỉ là lịch sử.
- [x] Sửa link ERD cũ/hỏng, ghi rõ archive không có trong working copy và cập nhật mô tả Phase 5 theo schema v3.
- [x] Giữ nguyên các entry lịch sử, đồng thời thêm cảnh báo để không nhầm chúng với trạng thái code hiện hành.

### File đã tạo / sửa
| File | Loại | Tóm tắt thay đổi |
|------|------|------------------|
| `docs/implementation-plan.md` | Sửa | Cập nhật trạng thái reset P1, link ERD v3, rule booking và transaction P5 |
| `docs/tien-do.md` | Sửa | Cập nhật bảng tổng quan và thêm mốc đồng bộ 18/08 |
| `docs/phase-2-auth.md` | Sửa | Đánh dấu đặc tả còn nhưng code P2 phải dựng lại |
| `docs/admin-user-features.md` | Sửa | Sửa link ERD hiện hành |
| `docs/erd-file/erd.md` | Sửa | Bỏ link archive không tồn tại, giữ ghi chú lịch sử dạng văn bản |
| `rules/QUY-TAC-LAM-VIEC.md` | Sửa | Thay rule `Bookings.CourtID` cũ bằng quan hệ suy ra qua TimeSlot |
| `sql/README.md` | Sửa | Bỏ quan hệ trực tiếp Courts→Bookings, sửa link ERD |
| `rules/LOG-LAM-VIEC.md` | Sửa | Thêm entry này |

### Chưa làm / tồn đọng
- Chưa khởi tạo Git hoặc dựng lại P1; chờ user giao task tiếp theo.
- Chưa chạy build/test vì task chỉ đồng bộ tài liệu.

### Ghi chú cho model còn lại
- Khi dựng backend, không thêm `courtId` vào model/persistence của `Booking`; JOIN `Bookings → TimeSlots → Courts` khi cần thông tin sân.
- Không dùng trạng thái “P1–P4 đã xong” trong các entry 17/08 để suy ra hiện trạng working copy.

### Cần người dùng quyết định
- Không.

---

## [2026-08-17 23:55] — QWEN — Phase 1 (P3+P4): Xem sân & Hồ sơ — BE + FE theo demo kit

**Task:** Triển khai P3 (U7–U9 xem sân/slot) + P4 (U4–U6 hồ sơ/mật khẩu) theo kế hoạch 6 phase đã duyệt; FE restyle theo demo kit `design-review-kit.html` (đã duyệt trước đó).
**Trạng thái:** Xong — compile sạch, chờ user chạy Tomcat test.

### Đã làm
- [x] **BE:** `CourtDAO` (list JOIN CourtTypes + filter all/tennis/pickleball, countByType, findById IsActive=1, listBookable), `TimeSlotDAO` (listByCourtAndDate, findById, updateStatus nhận connection ngoài cho transaction P5, listByDate, insert, updateTimes, close mềm không đụng slot Booked).
- [x] `AccountDAO` thêm: updateProfile, updatePasswordHash, existsByEmailExcluding, existsByPhoneExcluding.
- [x] `ProfileService` (validate name/email/phone + chuẩn hóa + check trùng loại trừ chính mình; changePassword kiểm tra mật khẩu cũ, 8–64 ký tự, SHA-256).
- [x] 4 servlet mới: `/courts` (sanitized type filter), `/court?id=&date=` (kẹp ngày [hôm nay, +6], đánh dấu slot past, sinh `dateOptions` cho dãy pill), `/profile` + `/password` (CSRF trước, PRG `?saved=1`, catch SQLState 23000 lỗi thân thiện). `HomeServlet` lấy số sân thật từ DAO. `Views` + `web.xml` thêm 4 mapping.
- [x] **FE:** refresh toàn bộ `app.css` theo demo kit — nút pill 999px hover nhấc, filter pill tray, date pills, input nền kem focus ring xanh, card bo 1.25rem, alert mềm, hero card bo góc + đường kẻ sân, footer card bo góc, stat-card/KPI gradient (chuẩn bị cho P8). Giữ nguyên toàn bộ token màu + tên class cũ.
- [x] Restyle JSP giữ đúng contract EL/CSRF: `court/list.jsp` (C3: chip nổi trên ảnh, placeholder gradient theo loại), `court/detail.jsp` (C4: dãy pill chọn ngày thay date input, slot grid hiện giá), `home/index.jsp` (C1: hero + ưu điểm + quy trình 4 bước + bảng giá), `account/profile.jsp` (C10), `account/password.jsp`, `layout/footer.jsp`.
- [x] Cập nhật `MASTER.md` mục 5.1–5.15 ghi nhận component mới (pill, date pill, court card, hero/header/footer, stat/KPI).
- [x] Compile Ant (JDK 1.8.0_172): **BUILD SUCCESSFUL**, 0 lỗi.

### File đã tạo / sửa
| File | Loại | Tóm tắt thay đổi |
|------|------|------------------|
| `src/java/com/sportzone/dao/CourtDAO.java` | Mới | U7 — list/count/findById JOIN CourtTypes |
| `src/java/com/sportzone/dao/TimeSlotDAO.java` | Mới | U9 + nền A11–A14 — CRUD slot, updateStatus(conn) |
| `src/java/com/sportzone/model/DateOption.java` | Mới | View model dãy pill chọn ngày |
| `src/java/com/sportzone/model/Court.java` | Sửa | + typeName/typeSlug (view fields từ JOIN) |
| `src/java/com/sportzone/model/TimeSlot.java` | Sửa | + flag `past` (U9) |
| `src/java/com/sportzone/dao/AccountDAO.java` | Sửa | + updateProfile/updatePasswordHash/exists…Excluding |
| `src/java/com/sportzone/service/ProfileService.java` | Mới | U5+U6 — validate + đổi mật khẩu |
| `src/java/com/sportzone/controller/CourtsServlet.java` | Mới | U7 |
| `src/java/com/sportzone/controller/CourtDetailServlet.java` | Mới | U8+U9 + dateOptions |
| `src/java/com/sportzone/controller/ProfileServlet.java` | Mới | U4+U5 |
| `src/java/com/sportzone/controller/PasswordServlet.java` | Mới | U6 |
| `src/java/com/sportzone/controller/HomeServlet.java` | Sửa | Số sân thật từ DAO |
| `src/java/com/sportzone/util/Views.java` | Sửa | + COURT_LIST/COURT_DETAIL/PROFILE/PASSWORD |
| `web/WEB-INF/web.xml` | Sửa | + 4 servlet mapping |
| `web/assets/css/app.css` | Sửa | Refresh theo demo kit (21 section) |
| `web/WEB-INF/views/court/list.jsp`, `court/detail.jsp` | Sửa | Restyle C3/C4 |
| `web/WEB-INF/views/home/index.jsp` | Sửa | Restyle C1 |
| `web/WEB-INF/views/account/profile.jsp`, `password.jsp` | Sửa | Restyle C10 |
| `web/WEB-INF/views/layout/footer.jsp` | Sửa | Footer card bo góc 3 cột |
| `design-system/sportzone/MASTER.md` | Sửa | Ghi nhận component mới |

### Chưa làm / tồn đọng
- Chưa test runtime trên Tomcat (user tự chạy theo checklist: `/home`, `/courts`, `/courts?type=tennis`, `/court?id=1`, `/profile`, `/password`).
- `/booking/create` đang 404 — thuộc Phase 2 (P5), link từ court detail trỏ sẵn rồi.

### Ghi chú cho Deepseek
- `TimeSlotDAO.updateStatus(Connection, int, String)` đã nhận connection ngoài — P5 dùng trong transaction đặt/hủy booking.
- `CourtDAO.listBookable()` (Status='Available') chuẩn bị sẵn cho form đặt sân P5.
- `dateOptions` (List<DateOption>: value/weekday/dayMonth/selected) là attr mới của `/court` — JSP detail đã bind.
- CSS mới có sẵn `.btn-sz-danger`, `.btn-icon`, `.table-wrap`, `.stat-card`, `.kpi-icon` + 6 gradient — P5–P8 dùng luôn không cần chế thêm.

### Cần người dùng quyết định
- Không.

---

## [2026-08-17 23:15] — QWEN — Sửa lỗi NetBeans "không tìm được server"

**Task:** User báo không mở được project trên NetBeans, lỗi không tìm được server.
**Trạng thái:** Xong

### Đã làm
- [x] Chẩn đoán: `nbproject/private/private.properties` bị mất 3 dòng cấu hình server (`deploy.ant.properties.file`, `j2ee.server.home`, `j2ee.server.instance`) — chỉ còn dòng `user.properties.file`. Không có `j2ee.server.instance` nên NetBeans không nối project với Tomcat → báo lỗi server.
- [x] Xác minh nguyên nhân KHÔNG phải do file thiết kế mới: Qwen chỉ tạo file mới (JSP/CSS/docs), không đụng `nbproject`. Timestamp 3 file nbproject bị sửa là 20:59 cùng lúc NetBeans mở project; `genfiles.properties` đổi stylesheet sang `1.102.0.1` = đúng version NetBeans đang cài; diff `build-impl.xml` chỉ là 2 target copy jar (jstl, mssql-jdbc) do NetBeans tự regenerate. Tomcat 9.0.118 vẫn còn nguyên tại `E:\0_FPTU\SEMESTER_4_SUM26\PRJ\apache-tomcat-9.0.118`, registry server trong userdir NetBeans 26 vẫn trỏ đúng chỗ.
- [x] Ghi lại 3 dòng server vào `nbproject/private/private.properties` (trỏ Tomcat 9.0.118 + tomcat90.properties của NetBeans 26).
- [x] Verify: chạy `ant compile` (Ant bundled NetBeans + JDK 1.8.0_172) → **BUILD SUCCESSFUL**, 28 source files, 0 lỗi.

### File đã tạo / sửa
| File | Loại | Tóm tắt thay đổi |
|------|------|------------------|
| `SportZoneManagement/nbproject/private/private.properties` | Sửa | Khôi phục `deploy.ant.properties.file`, `j2ee.server.home`, `j2ee.server.instance` |

### Chưa làm / tồn đọng
- Chưa mở lại NetBeans GUI để xác nhận hết báo lỗi (cần user tự mở).
- Context `SportZoneManagement.xml` không còn trong `conf/Catalina/localhost` của Tomcat (chỉ còn CarWashSystem.xml, PE_Prj301.xml) — NetBeans sẽ tự deploy lại khi Run project.

### Ghi chú cho Deepseek
- Không đụng gì logic. Nếu NetBeans vẫn chưa nhận server sau khi mở lại: Services → Servers → Add Server → Tomcat, trỏ về `E:\0_FPTU\SEMESTER_4_SUM26\PRJ\apache-tomcat-9.0.118`, rồi Properties project → Run → chọn lại server.

### Cần người dùng quyết định
- Không.

---

## [2026-08-17 22:30] — QWEN — Thiết kế trước giao diện toàn bộ trang User (P3–P6)

**Task:** "Cần thiết kế trước giao diện các trang của người dùng, check file xem có bao nhiêu trang cần phải thiết kế."
**Trạng thái:** Xong

### Đã làm
- [x] Kiểm đếm trang từ `docs/admin-user-features.md` + `docs/implementation-plan.md` + JSP hiện có: **đã có 8 file** (home, login, register, header, footer, 403/404/500) — **cần thiết kế mới 10 trang user** thuộc P3–P6: court/list, court/detail, booking/create, booking/list, booking/detail, invoice/list, invoice/detail, account/profile, account/password (kèm layout admin sidebar cho P7 — chưa làm đợt này)
- [x] Chạy skill `ui-ux-pro-max` (3 query: design-system, ux bảng/badge, ux form/empty state). **Quyết định:** GIỮ palette xanh lá–vàng gold + system font hiện có (đã release P1/P2); palette đỏ `#DC2626` + Bebas Neue skill đề xuất chỉ ghi nhận làm tham khảo trong MASTER.md mục 1
- [x] Viết `design-system/sportzone/MASTER.md` — tokens màu (brand + semantic), typography, spacing, 11 component chuẩn (nút, badge 17 trạng thái, card, pagehead, bảng, form, alert, empty state, slot grid, tabs, icon SVG), quy ước class `sz-`/`is-*`
- [x] Viết 4 spec trang: `design-system/sportzone/pages/courts.md` (U7–U9), `booking.md` (U10–U13), `invoices.md` (U14–U18), `profile.md` (U4–U6) — mỗi spec có layout ASCII, trạng thái (có data/rỗng/lỗi), responsive, danh sách biến cần bind
- [x] Nâng cấp `web/assets/css/app.css`: chia 18 section block comment, giữ nguyên token/class P1-P2, thêm ~20 component mới (pagehead, tabs, court card, slot grid, empty, table-cards mobile, form đặt sân, invoice paper, profile, kv rows) + `prefers-reduced-motion`
- [x] Dựng markup 10 JSP mới (đủ CSRF hidden field mọi form POST, comment `<!-- DEEPSEEK: ... -->` đánh dấu chỗ bind, tag chức năng U7–U18)
- [x] Tạo bản preview HTML tĩnh 5 file tại `design-system/sportzone/preview/` — mở `index.html` bằng trình duyệt để xem thiết kế không cần Tomcat

### File đã tạo / sửa
| File | Loại | Tóm tắt thay đổi |
|------|------|------------------|
| `design-system/sportzone/MASTER.md` | Mới | Design system chuẩn toàn cục |
| `design-system/sportzone/pages/courts.md` | Mới | Spec U7, U8, U9 |
| `design-system/sportzone/pages/booking.md` | Mới | Spec U10–U13 |
| `design-system/sportzone/pages/invoices.md` | Mới | Spec U14–U18 |
| `design-system/sportzone/pages/profile.md` | Mới | Spec U4–U6 |
| `web/assets/css/app.css` | Sửa | Token semantic + 20 component mới, chia section |
| `web/WEB-INF/views/court/list.jsp` | Mới | U7 — tabs filter, court card, empty state |
| `web/WEB-INF/views/court/detail.jsp` | Mới | U8+U9 — info card, slot grid theo ngày |
| `web/WEB-INF/views/booking/create.jsp` | Mới | U10 — form 4 bước + tóm tắt đơn sticky + JS tổng tiền |
| `web/WEB-INF/views/booking/list.jsp` | Mới | U11 — tabs sắp tới/lịch sử, bảng card-stack mobile |
| `web/WEB-INF/views/booking/detail.jsp` | Mới | U12+U13+U16+U17 — detail, hủy, notice CK |
| `web/WEB-INF/views/invoice/list.jsp` | Mới | U14 — bảng hóa đơn |
| `web/WEB-INF/views/invoice/detail.jsp` | Mới | U15+U16+U18 — hóa đơn dạng giấy |
| `web/WEB-INF/views/account/profile.jsp` | Mới | U4+U5 — avatar initials + form |
| `web/WEB-INF/views/account/password.jsp` | Mới | U6 — form 3 ô password + JS validate |
| `design-system/sportzone/preview/*.html` (5 file) | Mới | Preview tĩnh xem bằng trình duyệt |

### Chưa làm / tồn đọng
- Chưa dựng trang Admin (P7/P8: dashboard, users, courts, slots, services, bookings, invoices, reports + sidebar) — chờ giao task
- Chưa verify JSP compile/deploy trên Tomcat (phần của Deepseek khi viết servlet P3–P6)
- Preview HTML chưa test trên trình duyệt thật (chỉ viết markup theo spec)

### Ghi chú cho Deepseek
- **Đọc spec trước khi code servlet P3–P6:** `design-system/sportzone/pages/*.md` — mỗi file có mục "Biến Deepseek cần bind" liệt kê tên biến + kiểu.
- **Model cần bổ sung getter (hoặc tạo view model):** `Court` cần `typeName`/`typeSlug` (JOIN CourtTypes); `Booking` cần `courtName`, `typeName`, `typeSlug`, `location`, `slotDate`, `startTime`, `endTime` (JOIN Courts+TimeSlots); `BookingService` cần `serviceName`; `Invoice` cần `accountName`, `courtName`, `slotDate`, `startTime`, `endTime`, `courtAmount` (= CourtPriceAtOrder của booking). Nếu không muốn sửa model thì tạo view model riêng — markup chỉ phụ thuộc tên getter.
- **Views.java:** cần thêm hằng số view cho 9 đường dẫn mới (`views/court/list.jsp` … `views/account/password.jsp`).
- **URL map giữ đúng plan mục 2.4:** `/courts`, `/court?id=&date=`, `/booking/create?courtId=&date=&slotId=`, `/bookings?tab=`, `/booking?id=`, `/booking/cancel` (POST), `/invoices`, `/invoice?id=`, `/invoice/notice` (POST), `/profile`, `/password`.
- **Biến điều khiển quan trọng:** `canCancel` + `cancelReason` (booking detail), `noticeSent` (U17), `paymentStatus` = NULL khi booking chưa có HĐ (U16 — markup tự render badge "Chưa có hóa đơn"), `fieldErrors` là Map<String,String> key theo tên input.
- **Slot grid:** `slotList` trả đủ 3 trạng thái Available/Booked/Closed; slot đã qua giờ trong ngày đánh dấu bằng cách thêm class `is-past` hoặc filter phía server — markup hiện tại coi slot không phải Available/Booked là Closed, Deepseek thêm nhánh `is-past` nếu cần (comment trong JSP).
- **fmt:** tiền dùng `<fmt:formatNumber type="number" groupingUsed="true">` (BigDecimal từ model OK); giờ dùng `<fmt:formatDate pattern="HH:mm">` trên `java.sql.Time`.
- Form đặt sân có attr `data-court-price` trên `<form>` — Deepseek bind `selectedCourtPrice` (giá sân đang chọn) để JS tính tổng đúng sau mỗi lần reload GET.

### Cần người dùng quyết định
- Không. Nếu muốn đổi palette sang đỏ thể thao (skill đề xuất) thì báo trước khi Deepseek code P3 — chỉ sửa token MASTER + app.css, không đụng markup.

---

## [2026-08-17 16:30] — QWEN — Setup bộ skill thiết kế + ban hành quy tắc làm việc

**Task:** Đọc docs dự án, cài bộ skill UI/UX từ repo `nextlevelbuilder/ui-ux-pro-max-skill`, tạo file quy tắc làm việc chung cho 2 model.
**Trạng thái:** Xong

### Đã làm
- [x] Đọc toàn bộ `docs/` (implementation-plan, admin-user-features, tien-do, logic-erd, phase-2-auth) — nắm ngữ cảnh: dự án đang xong P1+P2, sắp vào P3/P4
- [x] Tải repo `ui-ux-pro-max-skill` (GitHub) và cài 7 skill vào `.zcode/skills/`: `ui-ux-pro-max` (chính), `design`, `design-system`, `ui-styling`, `brand`, `banner-design`, `slides`
- [x] Verify skill chạy: test query `"sports booking dashboard" --design-system -p "SportZone"` trả về palette đỏ thể thao (#DC2626) + font Bebas Neue / Source Sans 3 — hợp với sản phẩm sân thể thao
- [x] Tạo `rules/QUY-TAC-LAM-VIEC.md` — phân công Qwen=UI / Deepseek=logic, quy trình 4 bước, template báo cáo, quy tắc comment code, luồng bàn giao thiết kế→code, 11 rule nghiệp vụ cấm vi phạm
- [x] Tạo file log này

### File đã tạo / sửa
| File | Loại | Tóm tắt thay đổi |
|------|------|------------------|
| `.zcode/skills/**` (7 thư mục skill, 256 file) | Mới | Bộ skill thiết kế ui-ux-pro-max, workspace scope |
| `rules/QUY-TAC-LAM-VIEC.md` | Mới | Quy tắc làm việc chung 2 model |
| `rules/LOG-LAM-VIEC.md` | Mới | File log này |

### Chưa làm / tồn đọng
- Chưa chạy `--persist` tạo `design-system/sportzone/MASTER.md` — chờ người dùng giao task thiết kế cụ thể rồi mới chốt design system chính thức (tránh chốt style khi chưa có yêu cầu)
- Chưa dựng mockup/spec cho bất kỳ trang nào — chờ giao việc

### Ghi chú cho Deepseek
- Đọc `rules/QUY-TAC-LAM-VIEC.md` trước khi nhận task kế tiếp. Điểm đáng chú ý nhất với bên logic: mọi form POST phải có CSRF hidden field (Qwen sẽ chừa chỗ trong markup), và khi bind dữ liệu vào JSP thì tìm comment `<!-- DEEPSEEK: ... -->` do Qwen để lại.
- Project hiện dùng Bootstrap 5 CDN + `app.css` — không thêm framework CSS/JS mới.

### Cần người dùng quyết định
- Không. Chờ giao task thiết kế tiếp theo.

---
