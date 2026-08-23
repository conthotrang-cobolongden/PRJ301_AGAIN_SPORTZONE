# 01. Kiến trúc và vòng đời request

## 1. Các tầng của project

### View — JSP

Thư mục: `SportZoneManagement/web/WEB-INF/views/`

JSP chịu trách nhiệm hiển thị HTML. Dữ liệu động được lấy bằng EL/JSTL như `${court.courtName}`, `${sessionScope.account}` hoặc `<c:forEach>`. JSP không trực tiếp kết nối database và không nên chứa nghiệp vụ.

### Controller — Servlet

Thư mục: `SportZoneManagement/src/java/controller/`

Controller nhận parameter, kiểm tra sơ bộ HTTP/session/role/CSRF, gọi Service hoặc DAO, gắn request attribute rồi forward/redirect.

### Service

Thư mục: `SportZoneManagement/src/java/service/`

Service gom quy tắc nghiệp vụ có thể test độc lập: validate đăng ký, chống đặt slot không hợp lệ, tính membership, quy định cửa sổ check-in, chặn đóng sân đang có booking.

### DAO

Thư mục: `SportZoneManagement/src/java/dao/`

DAO mở connection qua `DBUtils`, dùng `PreparedStatement`, chạy SQL, điều khiển transaction và map `ResultSet` thành model.

### Model

Thư mục: `SportZoneManagement/src/java/model/`

Model là object truyền dữ liệu giữa DAO, Service, Controller và JSP: `Account`, `Court`, `TimeSlot`, `Booking`, `Invoice`, `BookingService`, `MembershipSummary`, `AdminDashboard`.

## 2. Front Controller hoạt động thế nào?

URL bên ngoài thống nhất dạng:

```text
/SportZoneManagement/MainController?action=<ten-action>
```

Ví dụ:

```text
GET /MainController?action=court&id=3&date=2026-08-24
```

Luồng chạy:

1. Tomcat đọc mapping `/MainController` trong `web.xml` và gọi `MainController`.
2. `MainController.processRequest()` đọc parameter `action`.
3. `resolveServletName()` dùng `switch` để đổi action thành servlet name, ví dụ `court → CourtDetailServlet`.
4. `ServletContext.getNamedDispatcher(servletName)` lấy named dispatcher đã khai báo trong `web.xml`.
5. `dispatcher.forward(request, response)` chuyển cùng request sang servlet đích.
6. Nếu action không hợp lệ, trả HTTP 404.

Lợi ích của cách này:

- URL có một cổng vào thống nhất.
- JSP không cần biết class servlet cụ thể.
- Mỗi nghiệp vụ vẫn tách thành servlet riêng, tránh `MainController` phình to.

## 3. Filter chạy trước controller

Theo thứ tự mapping trong `web.xml`:

1. `EncodingFilter`: đặt UTF-8 cho request và response.
2. `AuthFilter`: action bảo vệ phải có account trong session; guest được chuyển tới login.
3. `FrozenAccountFilter`: account Frozen chỉ được xem trang khóa, logout và tải `/assets/`.
4. `RoleFilter`: bảo vệ URL `/admin/*`.

Lưu ý khi vấn đáp: các admin action thực tế đi qua `/MainController`, không phải `/admin/*`. Vì vậy từng admin servlet vẫn gọi `requireAdmin()` để kiểm tra role. Đây là lớp kiểm tra thực tế cho các action `adminDashboard`, `adminCourts`, `adminBookings`.

## 4. Forward và redirect khác nhau thế nào?

### Forward

```java
request.setAttribute("courtList", courtList);
request.getRequestDispatcher(Views.COURT_LIST).forward(request, response);
```

- Chạy nội bộ server.
- URL trình duyệt không đổi.
- Giữ nguyên request attribute.
- Dùng khi render JSP.

### Redirect

```java
response.sendRedirect(contextPath
        + "/MainController?action=profile&updated=1");
```

- Trình duyệt gửi request GET mới.
- URL thay đổi.
- Request attribute cũ mất.
- Thường dùng sau POST theo mẫu PRG: Post → Redirect → Get, tránh submit trùng khi refresh.

Admin dùng session flash (`adminFlash`, `adminFlashError`) để giữ thông báo qua redirect; GET đọc flash vào request rồi xóa khỏi session.

## 5. Contract giữa Controller và JSP

Một JSP chỉ hoạt động đúng khi controller gắn đúng attribute. Ví dụ `CourtDetailServlet` gắn:

| Attribute | Kiểu | JSP sử dụng |
|---|---|---|
| `court` | `Court` | Tên, loại, giá, trạng thái sân |
| `slotList` | `List<TimeSlot>` | Các slot của ngày |
| `selectedDate` | `String` | Parameter/link ngày |
| `selectedDateObj` | `java.sql.Date` | Format ngày bằng JSTL |
| `dateOptions` | `List<DateOption>` | Thanh chọn 7 ngày |

Tên attribute, form action và input `name` là contract. Đổi HTML được, nhưng đổi các tên này mà không sửa controller sẽ làm dữ liệu mất hoặc POST không đọc được.

## 6. GET và POST trong project

### GET

Dùng cho đọc dữ liệu và hiển thị form:

- danh sách sân, chi tiết sân;
- form login/register/profile/password;
- form tạo booking;
- danh sách/chi tiết booking và hóa đơn;
- dashboard admin.

### POST

Dùng cho thay đổi trạng thái:

- login, register;
- cập nhật profile, đổi mật khẩu;
- tạo booking;
- xác nhận/hủy thanh toán;
- cập nhật sân, sinh slot tuần;
- check-in.

Các POST trên đều kiểm tra `CsrfUtil.isValid(request)` trước khi thay đổi dữ liệu.

## 7. Kết nối database

Mọi DAO gọi:

```java
DBUtils.getConnection()
```

`DBUtils` nạp driver `com.microsoft.sqlserver.jdbc.SQLServerDriver` và tạo connection tới `SportZoneDB`. Các truy vấn dùng try-with-resources để tự đóng `Connection`, `PreparedStatement`, `ResultSet`.

Các luồng nhiều bước như tạo booking, thanh toán và cập nhật sân dùng transaction:

```text
setAutoCommit(false)
  → chạy các câu SQL
  → commit nếu tất cả thành công
  → rollback nếu có lỗi
```

## 8. Luồng lỗi

- ID sai hoặc object không thuộc user: thường trả 404 để không tiết lộ tài nguyên có tồn tại.
- Chưa đăng nhập: `AuthFilter` redirect đến login; servlet còn có kiểm tra 401 phòng thủ.
- Sai role hoặc CSRF: trả 403.
- Operation POST không được hỗ trợ: trả 400.
- `SQLException`: controller bọc thành `ServletException`; container render trang 500 theo `web.xml`.
- Các trang 403, 404, 500 nằm dưới `WEB-INF/views/error/`.

## 9. Health check

Luồng `action=health` không render JSP:

```text
Browser
→ MainController
→ HealthServlet
→ HealthCheck.run()
→ DBUtils
→ INFORMATION_SCHEMA.TABLES
→ text/plain + HTTP 200 hoặc 503
```

`HealthCheck` xác nhận:

- kết nối database được;
- có đúng 9 base table;
- SHA-256 của mật khẩu demo `Admin@123` khớp hash cố định.

