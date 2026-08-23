# 07. Bảo mật và database

## 1. Chín bảng database

| Bảng | Vai trò | Quan hệ chính |
|---|---|---|
| `Roles` | Danh mục Admin/User | 1–N Accounts |
| `Accounts` | Tài khoản đăng nhập | N–1 Roles, 1–N Bookings |
| `CourtTypes` | Tennis/Pickleball | 1–N Courts |
| `Courts` | Thông tin và trạng thái sân | 1–N TimeSlots |
| `TimeSlots` | Slot theo sân/ngày/giờ | N–1 Courts, 1–N lịch sử Bookings |
| `Services` | Dịch vụ đi kèm | 1–N BookingServices |
| `Bookings` | Lần đặt sân | N–1 Account, N–1 TimeSlot |
| `BookingServices` | Chi tiết dịch vụ và giá snapshot | khóa ghép BookingID + ServiceID |
| `Invoices` | Hóa đơn/thanh toán | Booking 1–0..1 Invoice |

## 2. Quan hệ quan trọng

```text
Roles 1 ─── N Accounts
Accounts 1 ─── N Bookings
CourtTypes 1 ─── N Courts
Courts 1 ─── N TimeSlots
TimeSlots 1 ─── N Bookings (lịch sử; active bị giới hạn)
Bookings 1 ─── N BookingServices
Services 1 ─── N BookingServices
Bookings 1 ─── 0..1 Invoices
```

Không có FK trực tiếp `Bookings.CourtID`. Court được suy ra qua TimeSlot để tránh lưu dữ liệu dư thừa và mâu thuẫn.

## 3. Constraint bảo vệ dữ liệu

### Unique

- RoleName duy nhất.
- Account email/phone duy nhất.
- CourtType name và Court name duy nhất.
- Slot duy nhất theo `(CourtID, SlotDate, StartTime)`.
- Invoice duy nhất theo BookingID và InvoiceCode.
- BookingServices dùng khóa chính ghép `(BookingID, ServiceID)`.

### Check

- Account status: `Active`, `Frozen`, `Pending`.
- Court price không âm; status: `Available`, `Maintenance`, `Unavailable`.
- TimeSlot end > start; status: `Available`, `Booked`, `Closed`.
- Service price không âm, duration dương.
- Booking amount phải nhất quán; status đúng whitelist.
- Invoice amount phải nhất quán; payment status/method đúng whitelist.

### Filtered unique index booking active

Database giới hạn một booking active trên một TimeSlot bằng filtered unique index. Đây là lớp bảo vệ cuối chống double-booking ngay cả khi application có race condition.

## 4. PreparedStatement và SQL injection

DAO không ghép raw input vào vị trí value. Ví dụ:

```java
String sql = "SELECT ... WHERE AccountID = ?";
PreparedStatement statement = connection.prepareStatement(sql);
statement.setInt(1, accountID);
```

Filter/status động được whitelist trước; chỉ đoạn SQL cố định do code quyết định mới được nối.

## 5. CSRF

### Tạo token

`CsrfUtil.ensureToken(session)`:

1. Dùng token cũ nếu đã có.
2. Nếu chưa có, tạo 32 random bytes bằng `SecureRandom`.
3. Encode Base64 URL-safe.
4. Lưu vào session với key `csrfToken`.

### Gửi token

Mỗi form POST chứa:

```jsp
<input type="hidden" name="csrfToken"
       value="${sessionScope.csrfToken}">
```

### Kiểm tra

Controller gọi `CsrfUtil.isValid(request)`. Utility lấy expected token từ session, actual token từ request và dùng `MessageDigest.isEqual()` để so sánh. Sai/mất token → HTTP 403 trước khi gọi Service/DAO.

CSRF bảo vệ tình huống nạn nhân đang đăng nhập nhưng bị trang khác dụ gửi POST tới SportZone.

## 6. Authentication và authorization

### Authentication

`AuthFilter` kiểm tra session có `account`. Public actions được đi qua; action profile/booking/invoice/admin bị bảo vệ.

Nếu guest truy cập GET protected, filter lưu URL rồi redirect login.

### Authorization

- User-only servlet kiểm tra role `User`.
- BookingCreate/Payment/InvoiceDetail cho phép User hoặc Admin theo nhu cầu.
- Admin servlet gọi `requireAdmin()`.
- `RoleFilter` bảo vệ thêm `/admin/*` nếu sau này có URL admin trực tiếp.

## 7. Ownership chống IDOR

Chỉ kiểm tra đã đăng nhập là chưa đủ. User có thể đổi `id=10` thành `id=11`. Vì vậy DAO query đồng thời ID tài nguyên và AccountID session:

```text
WHERE BookingID = ? AND AccountID = ?
```

Áp dụng cho:

- booking detail;
- booking service lines;
- payment;
- invoice list/detail;
- membership sum.

Không tìm thấy hoặc không thuộc owner đều trả 404/null, không tiết lộ tài nguyên của người khác.

## 8. Session contract

`SessionUtil.storeLoggedInAccount()` lưu:

| Key | Value |
|---|---|
| `account` | `Account` đã xóa passwordHash |
| `roleName` | `Admin` hoặc `User` |
| `frozen` | boolean |
| `csrfToken` | token của session |

`redirectAfterLogin` chỉ tồn tại tạm khi AuthFilter lưu target.

Session timeout trong `web.xml` là 30 phút không hoạt động.

## 9. Transaction và lock

### Tạo account

Insert + lấy generated key, rollback nếu lỗi.

### Tạo booking

Lock account/slot/court, insert booking/services, mark slot booked, commit đồng thời.

### Payment

Update invoice, booking và slot cùng transaction.

### Update court

Lock court, đếm booking tương lai, chỉ update nếu an toàn.

### Generate slot

Isolation SERIALIZABLE + NOT EXISTS + unique constraint.

## 10. Mapping ResultSet

DAO có hàm `mapAccount`, `mapCourt`, `mapBooking`, `mapInvoice` để đổi từng row thành model. Timestamp SQL được chuyển sang `LocalDateTime`, Date sang `LocalDate`, Time sang `LocalTime`.

Các field như `typeName`, `courtName`, `accountName`, `paymentStatus` là view field lấy từ JOIN, không nhất thiết là cột trực tiếp của bảng gốc model.

## 11. Database setup và migration

- `01_SportZoneDB_Schema.sql`: drop/create toàn bộ database và 9 bảng.
- `02_SportZoneDB_Seed.sql`: roles, demo accounts, 10 sân, services, slot và booking mẫu.
- `03_SportZoneDB_AdminMvp_Migration.sql`: nâng cấp database cũ với CheckInAt/status/index mới.
- `00_SportZoneDB_Init_All.sql`: gọi schema + seed trong SQLCMD mode.

Không chạy script schema trên dữ liệu cần giữ vì nó drop database.

## 12. Các giới hạn bảo mật cần trả lời thẳng

- Password demo dùng SHA-256 không salt; production nên BCrypt/Argon2.
- DB username/password hard-code trong `DBUtils`; production nên biến môi trường/JNDI DataSource/secret manager.
- Logout dùng GET; production lý tưởng nên POST + CSRF.
- Payment chỉ mô phỏng.
- Không thấy rate limit/brute-force lockout động cho login.
- CSRF được kiểm tra tại từng POST controller, chưa triển khai thành filter dùng chung.

Nêu được giới hạn không có nghĩa project sai hoàn toàn; nó cho thấy hiểu phạm vi demo và biết hướng production hóa.

