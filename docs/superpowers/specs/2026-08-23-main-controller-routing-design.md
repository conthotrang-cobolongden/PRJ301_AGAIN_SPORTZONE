# MainController Routing Migration Design

**Ngày:** 2026-08-23  
**Trạng thái:** Chờ người dùng duyệt đặc tả  
**Phạm vi:** Chuyển routing hiện có của Phase 1–5 sang Front Controller; Phase 6 tạm dừng.

## 1. Mục tiêu

Mọi request nghiệp vụ từ trình duyệt đi vào một URL duy nhất:

```text
${contextPath}/MainController?action=<action>
```

`MainController` chỉ làm ba việc thuộc trách nhiệm điều hướng:

1. Đọc parameter `action`.
2. Ánh xạ action sang `servlet-name` đã khai báo trong `web.xml`.
3. Forward nguyên request/response sang Servlet chức năng bằng named dispatcher.

`MainController` không gọi DAO/Service, không validate dữ liệu nghiệp vụ, không xử lý CSRF, không kiểm tra ownership và không render JSP.

Static resources `/assets/*`, JSP lỗi do container dispatch và JSP nằm dưới `WEB-INF` không phải request nghiệp vụ nên không đi qua MainController.

## 2. Kiến trúc đích

```text
Browser
  -> Encoding/Auth/Frozen/Role Filter
  -> MainController?action=...
  -> named dispatcher
  -> Servlet chức năng
  -> Service/DAO
  -> JSP
```

Các Servlet chức năng vẫn được khai báo bằng `<servlet>` trong `web.xml` để named dispatcher tìm thấy, nhưng sau khi action tương ứng được migrate thì `<servlet-mapping>` public trực tiếp của Servlet đó bị xóa.

Quy tắc lỗi routing:

- Thiếu `action`: điều hướng mặc định tới `home`.
- `action` rỗng hoặc không được hỗ trợ: trả HTTP 404.
- Named dispatcher không tồn tại: ném `ServletException`; container xử lý bằng trang lỗi 500 hiện hành.
- HTTP method và toàn bộ query/form parameter được giữ nguyên khi forward.

## 3. Action contract

| Phase | Action | Servlet đích | Method hiện hành |
|---|---|---|---|
| P1 | `home` | `HomeServlet` | GET/POST |
| P1 | `health` | `HealthServlet` | GET/POST |
| P2 | `login` | `LoginServlet` | GET/POST |
| P2 | `register` | `RegisterServlet` | GET/POST |
| P2 | `logout` | `LogoutServlet` | GET |
| P3 | `courts` | `CourtsServlet` | GET |
| P3 | `court` | `CourtDetailServlet` | GET |
| P4 | `profile` | `ProfileServlet` | GET/POST |
| P4 | `password` | `PasswordServlet` | GET/POST |
| P5 | `bookingCreate` | `BookingCreateServlet` | GET/POST |
| P5 | `bookings` | `BookingsServlet` | GET |
| P5 | `bookingDetail` | `BookingDetailServlet` | GET |
| P5 | `accountLocked` | `AccountLockedServlet` | GET |

Phase 6 sẽ bổ sung sau khi migration P1–P5 hoàn tất: `invoices`, `invoiceDetail`, `invoiceNotice`.

## 4. Migration theo phase

### Phase 1 — Foundation routing

- Tạo `controller.MainController` với action `home`, `health`.
- Khai báo và map duy nhất `/MainController` trong `web.xml`.
- Giữ khai báo `HomeServlet`, `HealthServlet`, xóa mapping `/home`, `/health`.
- Đổi `index.jsp` thành forward tới `/MainController?action=home`.
- Đổi mọi link và redirect toàn project đang quay về `/home` thành MainController action `home`; đây là dependency toàn cục bắt buộc để Phase 1 không làm gãy P2–P5.
- Đổi các link Home/Health thuộc giao diện Phase 1.
- Thêm test routing RED trước khi tạo MainController; cập nhật foundation contract test.

Kết quả lát cắt: Home và Health không còn đường public trực tiếp; các route P2–P5 vẫn tạm chạy trực tiếp cho tới phase migration tương ứng.

### Phase 2 — Authentication routing

- Bổ sung action `login`, `register`, `logout`.
- Xóa mapping trực tiếp `/login`, `/register`, `/logout`.
- Đổi form/link/redirect auth sang MainController.
- `AuthFilter` nhận diện action được bảo vệ và lưu nguyên target `/MainController?action=...` cho redirect sau login.
- Login page là action public; logout tiếp tục hủy session trong `LogoutServlet`.
- Cập nhật Auth servlet/filter/web contract tests.

### Phase 3 — Court routing

- Bổ sung action `courts`, `court`.
- Xóa mapping trực tiếp `/courts`, `/court`.
- Đổi filter pill, court card, date pill và CTA sang MainController; giữ nguyên `type`, `id`, `date`.
- Cập nhật Phase 3 JSP/web contract tests.

### Phase 4 — Profile routing

- Bổ sung action `profile`, `password`.
- Xóa mapping trực tiếp `/profile`, `/password`.
- Đổi GET link, POST form và PRG redirect; giữ nguyên CSRF và field names.
- `AuthFilter` bảo vệ hai action này.
- Cập nhật Phase 4 servlet/JSP/web contract tests.

### Phase 5 — Booking và Frozen routing

- Bổ sung action `bookingCreate`, `bookings`, `bookingDetail`, `accountLocked`.
- Xóa mapping trực tiếp `/booking/create`, `/bookings`, `/booking`, `/account-locked`.
- Đổi link/form/PRG redirect; giữ nguyên `courtId`, `date`, `slotId`, `serviceId`, `tab`, `id`, `created` và CSRF.
- `AuthFilter` bảo vệ các booking action.
- `FrozenAccountFilter` đọc `action`; chỉ cho Frozen dùng `accountLocked`, `logout` và `/assets/*`.
- Route không được xác thực vẫn lưu đúng MainController target trước khi chuyển tới login.
- Cập nhật Phase 5 servlet/filter/JSP/web contract và regression batch tests.

Kết quả cuối P5: không còn Servlet nghiệp vụ P1–P5 nào có URL mapping public trực tiếp; mọi request động từ browser đi qua `/MainController?action=...`.

## 5. Filter contract

- `EncodingFilter` tiếp tục map `/*`.
- `AuthFilter` map `/MainController` và quyết định protected/public theo `action`, không theo servlet path cũ.
- `FrozenAccountFilter` tiếp tục map `/*` để cho `/assets/*` đi qua; khi servlet path là `/MainController`, filter dùng `action` để áp dụng whitelist.
- `RoleFilter` sẽ map `/MainController` và kiểm tra nhóm action admin khi Phase 7 được triển khai; P1–P5 chưa có admin action.
- MainController không chứa security rule; security vẫn thuộc Filter.

## 6. Tính tương thích và giới hạn

- Java 8, Tomcat 9, `javax.servlet`, JSP/JSTL và NetBeans Ant được giữ nguyên.
- Không sửa DAO, Service, model, SQL hoặc schema.
- Không đổi tên input, request/session attribute, EL property hay rule nghiệp vụ.
- URL cũ như `/home`, `/login`, `/booking` sẽ ngừng là contract public sau khi phase tương ứng được migrate.
- Không tạo alias/redirect compatibility cho URL cũ vì yêu cầu là buộc request nghiệp vụ đi qua MainController.
- MainController không dùng reflection và không nhận servlet-name từ client; action phải nằm trong whitelist cố định để tránh arbitrary dispatch.

## 7. Kiểm thử và tiêu chí hoàn tất

Mỗi phase dùng TDD:

1. Viết test routing/contract mới và xác nhận fail vì chưa có action/mapping.
2. Thêm routing tối thiểu cho phase.
3. Chạy targeted tests của phase khi được người dùng cho phép.
4. Sau P5, chạy regression P1–P5 và Tomcat smoke nếu được cho phép.

Các assertion cốt lõi:

- Action hợp lệ forward đúng named servlet.
- Missing action về Home; action lạ trả 404.
- Method và parameters không bị thay đổi.
- `web.xml` chỉ public-map MainController cho route đã migrate.
- JSP không còn form/link nghiệp vụ trỏ trực tiếp tới route đã migrate.
- Filter vẫn bảo vệ đúng guest/User/Frozen/Admin.
- PRG redirect luôn quay lại MainController.

## 8. Thứ tự commit đề xuất

1. `docs: design main controller routing migration`
2. `refactor(routing): migrate phase 1 through main controller`
3. `refactor(routing): migrate phase 2 auth routes`
4. `refactor(routing): migrate phase 3 court routes`
5. `refactor(routing): migrate phase 4 profile routes`
6. `refactor(routing): migrate phase 5 booking routes`

Mỗi phase là một checkpoint độc lập; không bắt đầu Phase 6 invoice cho tới khi migration P1–P5 được verify.
