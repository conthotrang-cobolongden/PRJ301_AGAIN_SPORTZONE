# UI Rebuild Guardrails — Bảo vệ contract Backend

> **Mục đích:** Cho phép rebuild mạnh phần giao diện mà không làm hỏng chức năng, dữ liệu, bảo mật hoặc luồng backend hiện hành.
>
> **Áp dụng bắt buộc:** Mọi task redesign, restyle, rebuild, responsive, đổi layout, đổi component hoặc chỉnh markup trong JSP/CSS/JavaScript.
>
> **Nguyên tắc trung tâm:** Có thể thay đổi cách giao diện trông như thế nào, nhưng không được tự ý thay đổi cách giao diện giao tiếp với backend.

---

## 1. Chế độ mặc định — Contract-Preserving UI Rebuild

Mọi task UI mặc định là **presentation-only**.

Được phép:

- Đổi layout, grid, wrapper và thứ tự các khối trình bày.
- Đổi Bootstrap class và CSS class `sz-*`.
- Đổi màu, font, spacing, border, shadow, icon, ảnh và animation.
- Chuyển cách hiển thị table/card/list khi vẫn giữ đủ dữ liệu và action.
- Thêm responsive behavior cho mobile, tablet và desktop.
- Cải thiện accessibility, focus state, label và semantic HTML.
- Viết lại copy mang tính trình bày nếu không đổi ý nghĩa nghiệp vụ.

Không được phép mặc định:

- Sửa Java backend, DAO, Service, Servlet, Filter, model hoặc utility.
- Sửa SQL, database schema, seed hoặc transaction rule.
- Sửa `web.xml`, dependency, build config hoặc environment config.
- Đổi contract giữa JSP/JavaScript và backend.
- Đưa dữ liệu demo hard-code vào JSP production thay cho dữ liệu backend.
- Chuyển authorization, validation hoặc nghiệp vụ từ server sang client.

Nếu UI mới bắt buộc cần một thay đổi bị cấm, phải dừng theo mục 8.

---

## 2. Functional Contract Freeze — BẮT BUỘC GIỮ

### 2.1 URL và HTTP

Phải giữ nguyên:

- URL path và context-relative URL.
- Query parameter name và ý nghĩa của parameter.
- HTTP method `GET`/`POST`.
- `form action` và `form method`.
- Redirect/forward target mà giao diện đang phụ thuộc.

Ví dụ không được tự đổi:

```text
GET /courts?type=tennis
GET /court?id=1&date=2026-08-23
POST /booking/cancel
```

### 2.2 Form contract

Phải giữ nguyên:

- `name` của `input`, `select`, `textarea` và button submit có giá trị.
- Kiểu control khi backend phụ thuộc cách submit dữ liệu.
- Hidden field và giá trị nghiệp vụ.
- `csrfToken` trên mọi form POST.
- Record ID, action, redirect target và các field phục vụ ownership check.
- Checkbox/radio `value`, trạng thái checked/selected và multi-value behavior.
- Field error key tương ứng với input name.

Không được disable một field cần submit. Nếu chỉ muốn khóa chỉnh sửa, phải kiểm tra backend contract và dùng giải pháp vẫn gửi đúng giá trị.

### 2.3 Request/session attribute và JSP EL

Phải giữ nguyên:

- Tên request attribute do Servlet bind.
- Tên session attribute và scope.
- Kiểu dữ liệu mà JSP đang nhận.
- Bean property trong EL.
- Tên biến vòng lặp nếu JavaScript hoặc JSP fragment phụ thuộc.

Ví dụ:

```jsp
${courtList}
${court.courtId}
${slotList}
${s.timeSlotId}
${sessionScope.account}
${fieldErrors.email}
```

Không được đổi `${court.courtId}` thành `${court.id}` chỉ vì tên mới trông ngắn hơn.

### 2.4 JSTL và nghiệp vụ hiển thị

Có thể tổ chức lại markup quanh JSTL, nhưng phải giữ nguyên ý nghĩa của:

- `c:if`, `c:choose`, `c:when`, `c:forEach`.
- Kiểm tra role, frozen state, ownership và login state.
- Điều kiện hiện/ẩn action.
- Điều kiện status và mapping nhãn trạng thái.
- Empty/error/success state.
- Điều kiện `canCancel`, `noticeSent`, `past` và các cờ nghiệp vụ khác.

Không được:

- Hiện nút mà user không có quyền dùng.
- Ẩn cảnh báo hoặc lỗi backend cần người dùng thấy.
- Đổi status code tiếng Anh trong điều kiện.
- Thay server-side permission bằng cách chỉ ẩn nút bằng CSS/JavaScript.

### 2.5 JavaScript và DOM hook

Phải giữ nguyên nếu đang được JavaScript hoặc test sử dụng:

- Element `id`.
- Input `name`.
- `data-*` attribute.
- CSS selector có chức năng.
- Event target và submit behavior.
- Giá trị dùng để tính tổng tiền, chọn slot hoặc confirm action.

Trước khi đổi/xóa một hook, phải tìm toàn bộ nơi sử dụng hook đó trong đúng scope trang.

### 2.6 Security contract

UI rebuild không được làm yếu:

- CSRF hidden field và server validation.
- Authentication/authorization visibility.
- Ownership-protected action.
- Output escaping bằng `c:out`.
- Password handling và sensitive field behavior.
- Confirm step cho action phá hủy hoặc không thể hoàn tác.

Không đưa password, token, internal ID không cần thiết hoặc SQL/backend error vào HTML/JavaScript.

---

## 3. File Boundary mặc định

Task UI được phép sửa khi nằm trong scope đã duyệt:

```text
SportZoneManagement/web/WEB-INF/views/**/*.jsp
SportZoneManagement/web/assets/css/**
SportZoneManagement/web/assets/img/**
SportZoneManagement/web/assets/js/**
design-system/**
```

Task UI không được tự mở rộng sang:

```text
SportZoneManagement/src/java/**
SportZoneManagement/web/WEB-INF/web.xml
SportZoneManagement/nbproject/**
sql/**
```

Đọc backend file chỉ được thực hiện khi cần xác nhận contract cụ thể. Đọc không đồng nghĩa được phép sửa.

---

## 4. Contract Snapshot trước khi sửa

Trước mỗi trang, phải ghi lại contract tối thiểu:

```markdown
## UI Contract Snapshot — <tên trang>

- JSP:
- URL GET:
- URL POST:
- Query parameters:
- Form action/method:
- Input names:
- Hidden fields:
- Request attributes:
- Session attributes:
- EL bean properties:
- JSTL business conditions:
- JavaScript/DOM hooks:
- Trạng thái cần giữ: data / empty / error / success / unauthorized
- Backend files tham chiếu read-only:
```

Snapshot có thể nằm trong plan, design spec hoặc báo cáo task. Không cần tạo file riêng nếu task chỉ có một trang nhỏ.

Không bắt đầu thay markup khi chưa xác định được form và dynamic contract của trang.

---

## 5. Quy trình rebuild một trang

### Bước 1 — Khóa scope

- Nêu chính xác JSP, CSS, JS và asset được sửa.
- Nêu rõ backend đang bị khóa.
- Nếu có shared layout, liệt kê các trang có thể bị ảnh hưởng.

### Bước 2 — Lập Contract Snapshot

- Đọc JSP hiện tại.
- Theo direct evidence, đọc đúng Servlet/JavaScript liên quan nếu cần.
- Ghi đầy đủ contract theo mục 4.

### Bước 3 — Chốt UI design

- Layout desktop/mobile.
- Component và class sẽ dùng.
- Data, empty, error và permission state.
- Không thiết kế action mà backend chưa hỗ trợ.

### Bước 4 — Thực hiện presentation-only

- Giữ nguyên contract đã snapshot.
- Ưu tiên CSS và wrapper mới hơn là thay logic JSTL.
- Dùng Bootstrap 5 + CSS hiện hữu; không thêm framework khi chưa được duyệt.
- Class mới dùng prefix `sz-`.

### Bước 5 — Static contract comparison

So sánh trước/sau:

- URL, method và form action.
- Input name và hidden field.
- Request/session attribute.
- EL bean property.
- JSTL business condition.
- `id`, `data-*` và selector JavaScript.
- CSRF và action permission.

Nếu có khác biệt chưa được duyệt, task chưa hoàn tất.

### Bước 6 — Runtime verification

Test đúng trang ở tối thiểu:

- Desktop khoảng 1200px.
- Tablet khoảng 768px.
- Mobile khoảng 375px.
- Có dữ liệu.
- Empty state nếu có.
- Validation/error state nếu có form.
- User chưa login, User, Frozen User và Admin nếu trang có phân quyền.
- GET/POST action chính của trang.

### Bước 7 — Báo cáo

- Ghi file đã sửa.
- Ghi contract được giữ.
- Ghi test/smoke đã chạy.
- Ghi rõ phần chưa verify.
- Cập nhật `rules/LOG-LAM-VIEC.md` theo quy trình chung.

---

## 6. Shared UI Safety

Các file sau có blast radius lớn:

```text
views/layout/header.jsp
views/layout/footer.jsp
assets/css/app.css
assets/js/* dùng chung
```

Khi sửa shared UI:

- Liệt kê các trang tiêu thụ component/class bị sửa.
- Không xóa class cũ nếu chưa kiểm tra toàn bộ nơi sử dụng.
- Ưu tiên thêm modifier/class mới thay vì đổi global selector rộng.
- Không đổi nav visibility theo role nếu chưa đối chiếu session contract.
- Smoke ít nhất một trang public, một trang User và một trang Admin có dùng shared UI.

---

## 7. Những cách làm bị cấm

- Thay JSP động bằng HTML demo tĩnh trong production.
- Comment hoặc xóa JSTL để giao diện dễ căn hơn.
- Hard-code tên, giá, status, số lượng hoặc URL từ seed.
- Đổi `POST` thành link `GET` cho action thay đổi dữ liệu.
- Bỏ `csrfToken` vì form “chỉ là giao diện”.
- Đổi input name mà không đổi backend theo task riêng đã duyệt.
- Dùng CSS để hiện action bị JSTL ẩn theo role/status.
- Chèn scriptlet Java vào JSP.
- Thêm React/Vue/Tailwind build hoặc dependency UI mới khi chưa được duyệt.
- Sửa Java/SQL/web.xml để làm cho mockup mới chạy mà chưa qua contract-change gate.
- Dọn/refactor backend ngoài task UI.

---

## 8. Contract Change Gate — BẮT BUỘC DỪNG

Nếu UI mới cần đổi contract, phải dừng trước khi sửa và báo:

```text
STATUS: BLOCKED

UI CONTRACT CHANGE REQUEST

Current contract:
- ...

Requested contract:
- ...

Reason UI needs the change:
- ...

Backend files/symbols affected:
- ...

Security/data behavior affected:
- ...

Tests and pages affected:
- ...

Decision needed from user:
- Approve coordinated UI + backend change, or keep current contract.
```

Không được xem yêu cầu “làm giao diện đẹp hơn” là quyền ngầm để đổi backend contract.

---

## 9. Checklist trước khi báo hoàn tất

### Contract

- [ ] URL và HTTP method giữ nguyên.
- [ ] Form action, input name và hidden field giữ nguyên.
- [ ] CSRF còn đủ trên mọi POST form.
- [ ] Request/session attribute và EL property giữ nguyên.
- [ ] JSTL condition giữ nguyên ý nghĩa nghiệp vụ.
- [ ] JavaScript hook và `data-*` còn hoạt động.
- [ ] Không có backend/SQL/web.xml change ngoài phê duyệt.

### Behavior

- [ ] Action chính hoạt động trên Tomcat.
- [ ] Data/empty/error state render đúng.
- [ ] Role/status visibility không regression.
- [ ] Không có JSP compile error hoặc HTTP 500.
- [ ] Output động được escape phù hợp.

### UI quality

- [ ] Responsive ở 375px, 768px và 1200px.
- [ ] Keyboard focus nhìn thấy được.
- [ ] Label và interactive element có thể nhận biết.
- [ ] Contrast chữ chính đạt tối thiểu 4.5:1.
- [ ] Không có horizontal overflow ngoài chủ ý.

### Reporting

- [ ] Ghi rõ file đã sửa.
- [ ] Ghi verification thực tế, không nói “pass” nếu chưa chạy.
- [ ] Cập nhật `rules/LOG-LAM-VIEC.md`.

---

## 10. Ví dụ nhanh

### Hợp lệ

```jsp
<form action="${ctx}/login" method="POST">
    <input type="hidden" name="csrfToken" value="${csrfToken}">
    <input class="form-control sz-input" type="email" name="email">
</form>
```

Có thể đổi wrapper, class và layout nhưng giữ `action`, `POST`, `csrfToken`, `email`.

### Không hợp lệ

```jsp
<form action="${ctx}/sign-in" method="GET">
    <input type="email" name="username">
</form>
```

Đoạn này đã đổi URL, method, field name và bỏ CSRF; đây là backend contract change, không còn là UI-only task.

---

*File này là contract bắt buộc cho mọi UI rebuild. Ngoại lệ chỉ có hiệu lực khi người dùng phê duyệt rõ cho task cụ thể.*
