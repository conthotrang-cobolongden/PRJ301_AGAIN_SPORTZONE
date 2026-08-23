# Bộ tài liệu vấn đáp SportZone Management

Bộ tài liệu này giải thích project theo đúng luồng chạy thực tế của source code. Mục tiêu là giúp người đọc không chỉ nhớ tên class mà còn trả lời được: dữ liệu bắt đầu từ đâu, đi qua lớp nào, được kiểm tra ở đâu, câu SQL nào chạy và kết quả quay lại giao diện như thế nào.

## Thứ tự đọc đề xuất

1. [Kiến trúc và vòng đời request](01-kien-truc-va-request-lifecycle.md)
2. [Xác thực, tài khoản và hồ sơ](02-xac-thuc-va-tai-khoan.md)
3. [Sân và khung giờ](03-san-va-khung-gio.md)
4. [Đặt sân](04-dat-san.md)
5. [Thanh toán, hóa đơn và membership](05-thanh-toan-hoa-don-membership.md)
6. [Chức năng quản trị](06-chuc-nang-admin.md)
7. [Bảo mật và database](07-bao-mat-va-database.md)
8. [Câu hỏi vấn đáp và cheat sheet](08-cau-hoi-van-dap.md)

## Cách đọc mỗi chức năng

Mỗi chức năng được trình bày theo một khuôn cố định:

```text
Giao diện JSP
  → HTTP request tới MainController?action=...
  → MainController named-dispatch
  → Servlet chuyên trách
  → Service kiểm tra nghiệp vụ (nếu có)
  → DAO dùng JDBC/PreparedStatement
  → SQL Server
  → Model / Result object
  → request attribute hoặc redirect
  → JSP kết xuất bằng EL/JSTL
```

Không phải chức năng nào cũng đi đủ các tầng. Ví dụ:

- `AdminDashboardServlet` gọi `AdminDashboardDAO` trực tiếp vì chỉ đọc số liệu tổng hợp đơn giản.
- `CourtsServlet` gọi `CourtDAO` trực tiếp vì chỉ đọc danh sách và filter đã được whitelist tại controller.
- Tạo booking đi đủ Controller → Service → nhiều DAO vì có transaction và nhiều quy tắc chống double-booking.

## Bản đồ nhanh 19 action

| `action` | Servlet đích | Mục đích |
|---|---|---|
| `home` | `HomeServlet` | Trang chủ và số lượng sân |
| `health` | `HealthServlet` | Kiểm tra database/schema/hash demo |
| `login` | `LoginServlet` | Đăng nhập |
| `register` | `RegisterServlet` | Đăng ký |
| `logout` | `LogoutServlet` | Hủy session |
| `courts` | `CourtsServlet` | Danh sách và lọc sân |
| `court` | `CourtDetailServlet` | Chi tiết sân và lịch 7 ngày |
| `profile` | `ProfileServlet` | Xem/cập nhật hồ sơ và membership |
| `password` | `PasswordServlet` | Đổi mật khẩu |
| `bookingCreate` | `BookingCreateServlet` | Hiển thị form và tạo booking |
| `bookings` | `BookingsServlet` | Booking sắp tới/lịch sử của user |
| `bookingDetail` | `BookingDetailServlet` | Chi tiết booking thuộc user |
| `accountLocked` | `AccountLockedServlet` | Trang cách ly tài khoản Frozen |
| `invoicePayment` | `InvoicePaymentServlet` | Bắt đầu, xác nhận hoặc hủy thanh toán |
| `invoices` | `InvoicesServlet` | Danh sách hóa đơn Paid của user |
| `invoiceDetail` | `InvoiceDetailServlet` | Chi tiết hóa đơn Paid có ownership |
| `adminDashboard` | `AdminDashboardServlet` | Dashboard tổng hợp |
| `adminCourts` | `AdminCourtsServlet` | Giá/trạng thái sân và sinh slot tuần |
| `adminBookings` | `AdminBookingsServlet` | Lọc, xem booking và check-in |

## Những điểm phải nhớ trước khi vấn đáp

- JSP nằm dưới `WEB-INF`, người dùng không truy cập trực tiếp; servlet phải forward tới JSP.
- `MainController` chỉ điều hướng, không chứa nghiệp vụ.
- Request đọc dữ liệu thường dùng `GET`; request thay đổi dữ liệu dùng `POST` và kiểm tra CSRF.
- `Service` chịu trách nhiệm validation và quy tắc nghiệp vụ.
- `DAO` chịu trách nhiệm JDBC, SQL, transaction và mapping `ResultSet` sang model.
- Session lưu `account`, `roleName`, `frozen`, `csrfToken` và có thể lưu URL cần quay lại sau login.
- Booking không lưu `CourtID`; sân được suy ra qua `Bookings.TimeSlotID → TimeSlots.CourtID`.
- Giá sân và giá dịch vụ được snapshot lúc đặt để việc đổi giá sau này không làm thay đổi booking cũ.
- Thanh toán là mô phỏng, chưa tích hợp cổng thanh toán thật.
- `NoShow` và `Completed` hiện được đối soát kiểu lazy khi admin mở dashboard/danh sách/chi tiết booking, không có background scheduler chạy độc lập.

