# SportZone Management

SportZone Management là ứng dụng web quản lý và đặt sân thể thao dành cho tổ hợp **Tennis** và **Pickleball**. Hệ thống hỗ trợ khách hàng xem lịch sân, đặt sân, chọn dịch vụ đi kèm, thanh toán giả lập và theo dõi hóa đơn; đồng thời cung cấp khu vực quản trị để quản lý sân, khung giờ, booking, check-in và doanh thu.

## Công nghệ sử dụng

- Java 8
- JSP, JSTL và Expression Language (EL)
- Java Servlet 3.1
- JDBC
- Microsoft SQL Server
- Apache Tomcat 9
- Apache Ant / NetBeans
- HTML, CSS và JavaScript

Ứng dụng được tổ chức theo mô hình MVC:

```text
Browser
   ↓
Servlet / Filter
   ↓
Service (nghiệp vụ)
   ↓
DAO (JDBC)
   ↓
SQL Server
```

## Chức năng chính

### Khách chưa đăng nhập

- Xem trang chủ và thông tin tổng quan SportZone.
- Xem danh sách sân Tennis và Pickleball.
- Xem chi tiết sân, giá thuê và lịch khung giờ.
- Đăng ký và đăng nhập tài khoản.

### Khách hàng

- Cập nhật hồ sơ và thay đổi mật khẩu.
- Xem sân và các khung giờ còn trống.
- Đặt một khung giờ trong tương lai.
- Chọn thêm dịch vụ như thuê dụng cụ hoặc huấn luyện viên.
- Xem danh sách và chi tiết booking của chính mình.
- Thanh toán chuyển khoản QR giả lập.
- Xem danh sách và chi tiết hóa đơn đã thanh toán.
- Theo dõi hạng thành viên được tính từ tổng chi tiêu hợp lệ.
- Tài khoản bị khóa không thể tiếp tục sử dụng các chức năng được bảo vệ.

### Quản trị viên

- Xem dashboard tổng hợp số tài khoản, số sân, số booking và tổng doanh thu từ hóa đơn đã thanh toán.
- Tạo booking tại quầy; booking do admin tạo được hiển thị dưới tên **Khách vãng lai**.
- Xem, lọc và kiểm tra chi tiết booking theo trạng thái.
- Check-in booking từ đúng giờ bắt đầu đến trước 30 phút sau giờ bắt đầu.
- Booking đã xác nhận nhưng chưa check-in sau 30 phút được chuyển thành `NoShow` khi hệ thống thực hiện đối soát.
- Booking đã check-in được chuyển thành `Completed` sau khi hết giờ.
- Cập nhật giá và trạng thái sân với kiểm tra xung đột booking.
- Thêm slot cho một tuần mới mà không tạo trùng dữ liệu đã tồn tại.

## Quy tắc khung giờ

- Mỗi ngày có 15 slot, mỗi slot kéo dài 1 giờ.
- Giờ hoạt động: từ **06:00 đến 21:00**.
- Slot đầu tiên: `06:00–07:00`.
- Slot cuối cùng: `20:00–21:00`.
- Một tuần đầy đủ của 10 sân có tối đa 1.050 slot.
- Ngày bắt đầu tuần phải là thứ Hai và không được thuộc quá khứ.
- Có thể bấm thêm lại cùng một tuần: slot đã tồn tại được giữ nguyên, hệ thống chỉ bổ sung slot còn thiếu.

> Script seed hiện tạo dữ liệu mẫu cho tuần đầu. Nếu tuần đó chưa đủ 15 slot mỗi ngày, admin có thể dùng chức năng **Thêm Slot Tuần Mới** để bổ sung các slot còn thiếu.

## Cấu trúc thư mục

```text
PRJ301_AGAIN_SPORTZONE/
├── SportZoneManagement/        # Ứng dụng Java Web
│   ├── src/java/controller/    # Servlet điều phối request
│   ├── src/java/service/       # Quy tắc nghiệp vụ
│   ├── src/java/dao/           # Truy cập SQL Server bằng JDBC
│   ├── src/java/model/         # Model dữ liệu
│   ├── src/java/filter/        # Encoding, xác thực, khóa tài khoản, phân quyền
│   ├── web/WEB-INF/views/      # JSP
│   ├── web/assets/             # CSS, JavaScript, hình ảnh
│   ├── test/                   # Test và contract test
│   └── build.xml               # Cấu hình Ant
├── sql/                        # Schema, seed và migration SQL Server
├── design-system/sportzone/    # Giao diện thiết kế tham chiếu
├── security-test/              # Trang thử nghiệm bảo vệ CSRF
└── docs/                       # Tài liệu dự án
```

## Yêu cầu môi trường

Chuẩn bị các thành phần sau:

1. JDK 8.
2. Apache Tomcat 9.x.
3. Microsoft SQL Server chạy tại `localhost:1433`.
4. SQL Server Management Studio, Azure Data Studio hoặc `sqlcmd`.
5. NetBeans có hỗ trợ Java Web hoặc Apache Ant.

Driver Microsoft SQL Server JDBC và JSTL đã nằm trong `SportZoneManagement/web/WEB-INF/lib`.

## Khởi tạo database

### Cài mới hoàn toàn

Mở SSMS hoặc Azure Data Studio và chạy lần lượt:

1. `sql/01_SportZoneDB_Schema.sql`
2. `sql/02_SportZoneDB_Seed.sql`

Hoặc dùng `sqlcmd` tại thư mục `sql`:

```bat
sqlcmd -S localhost -U SA -P 12345 -C -I -i 01_SportZoneDB_Schema.sql
sqlcmd -S localhost -U SA -P 12345 -C -I -i 02_SportZoneDB_Seed.sql
```

> `01_SportZoneDB_Schema.sql` sẽ xóa database `SportZoneDB` hiện có trước khi tạo lại. Không chạy script này nếu cần giữ dữ liệu cũ.

### Nâng cấp database cũ

Nếu database đã được tạo từ phiên bản trước, sao lưu dữ liệu rồi chạy một lần:

```text
sql/03_SportZoneDB_AdminMvp_Migration.sql
```

Migration bổ sung vòng đời check-in và các trạng thái `CheckedIn`, `Completed`, `NoShow`.

## Cấu hình kết nối SQL Server

Mở file:

```text
SportZoneManagement/src/java/dao/DBUtils.java
```

Cập nhật URL, username và password phù hợp với SQL Server trên máy:

```java
private static final String URL = "jdbc:sqlserver://localhost:1433;"
        + "databaseName=SportZoneDB;encrypt=true;trustServerCertificate=true";
private static final String USERNAME = "sa";
private static final String PASSWORD = "12345";
```

SQL Server phải bật TCP/IP, lắng nghe cổng `1433` và cho phép SQL Server Authentication.

## Build và triển khai

### Cách 1: NetBeans

1. Chọn **File → Open Project** và mở thư mục `SportZoneManagement`.
2. Cấu hình JDK 8 và Apache Tomcat 9 cho project.
3. Kiểm tra lại thông tin kết nối trong `DBUtils.java`.
4. Chọn **Clean and Build**.
5. Chọn **Run** để NetBeans deploy ứng dụng lên Tomcat.

### Cách 2: Apache Ant và Tomcat

Từ thư mục gốc repository, chạy:

```bat
ant -f SportZoneManagement/build.xml clean dist
```

File WAR được tạo tại:

```text
SportZoneManagement/dist/SportZoneManagement.war
```

Sao chép WAR vào thư mục `webapps` của Tomcat, sau đó khởi động Tomcat. Truy cập:

```text
http://localhost:8080/SportZoneManagement/
```

Có thể kiểm tra kết nối ứng dụng và database tại:

```text
http://localhost:8080/SportZoneManagement/MainController?action=health
```

## Tài khoản demo

| Vai trò | Email | Mật khẩu |
|---|---|---|
| Admin | `admin@sportzone.local` | `Admin@123` |
| User | `user1@sportzone.local` | `User@123` |
| User | `user2@sportzone.local` | `User@123` |
| User bị khóa | `user3@sportzone.local` | `User@123` |

## Bảo mật và giới hạn hiện tại

- Các form thay đổi dữ liệu sử dụng CSRF token.
- Filter xử lý UTF-8, xác thực, tài khoản bị khóa và quyền admin.
- Truy vấn dữ liệu sử dụng `PreparedStatement`.
- Chi tiết booking và hóa đơn của khách hàng có kiểm tra ownership.
- Thanh toán QR hiện là **giả lập**, chưa kết nối cổng thanh toán thật.
- Mật khẩu demo đang dùng SHA-256 không salt; môi trường production nên chuyển sang BCrypt hoặc Argon2.
- Thông tin database hiện được cấu hình trực tiếp trong source; production nên chuyển sang biến môi trường hoặc JNDI DataSource.

## Tài liệu liên quan

- [Hướng dẫn database](sql/README.md)
- [ERD](docs/erd-file/erd.md)
- [Quy tắc làm việc với repository](rules/AGENTS.md)

