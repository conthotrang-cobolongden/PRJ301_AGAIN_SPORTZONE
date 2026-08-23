# 06. Chức năng quản trị

## 1. Bảo vệ khu vực admin

Mỗi admin servlet có `requireAdmin()` gọi `SessionUtil.isAdmin(session)`. Nếu role trong session không phải `Admin`, trả 403.

Điều này cần thiết vì URL thực tế là `/MainController?action=admin...`; `RoleFilter` chỉ map `/admin/*` nên không phải lớp duy nhất bảo vệ các named-dispatch admin hiện tại.

Các POST admin còn kiểm tra CSRF.

## 2. Dashboard admin

### Luồng

```text
GET action=adminDashboard
→ AdminDashboardServlet
→ requireAdmin()
→ AdminBookingService.reconcile()
→ AdminBookingDAO.reconcile()
→ AdminDashboardDAO.load()
→ AdminDashboard model
→ request attribute dashboard
→ admin/dashboard.jsp
```

### Số liệu dashboard

`AdminDashboardDAO.load()` dùng một query với bốn subquery:

| Chỉ số | Cách tính |
|---|---|
| Tổng tài khoản | `COUNT(*) FROM Accounts` |
| Tổng sân | `COUNT(*) FROM Courts WHERE IsActive=1` |
| Tổng booking | `COUNT(*) FROM Bookings` |
| Tổng doanh thu | `SUM(Invoices.FinalAmount) WHERE PaymentStatus='Paid'` |

Dashboard gọi DAO trực tiếp vì đây là read model tổng hợp đơn giản, không có validation đầu vào. Trước khi đọc số liệu, servlet reconcile lifecycle booking.

## 3. Danh sách sân admin

### Luồng

```text
GET action=adminCourts
→ AdminCourtsServlet.doGet()
→ requireAdmin + ensure CSRF
→ consume session flash
→ AdminCourtService.list()
→ AdminCourtDAO.findAll()
→ request courtList + defaultWeek
→ admin/courts.jsp
```

`defaultWeek` là thứ Hai kế tiếp tính từ ngày hiện tại. DAO JOIN `Courts` với `CourtTypes`, lấy các sân active.

## 4. Cập nhật giá và trạng thái sân

### Form

Mỗi dòng sân trong `admin/courts.jsp` POST:

```text
action=adminCourts
operation=updateCourt
courtId=...
price=...
status=Available|Maintenance|Unavailable
csrfToken=...
```

### Luồng

```text
admin/courts.jsp
→ AdminCourtsServlet.doPost()
→ requireAdmin + CSRF
→ AdminCourtService.update()
→ validate ID/price/status
→ AdminCourtDAO.updatePriceAndStatus()
→ transaction + lock court
→ kiểm tra booking tương lai nếu muốn ngừng sân
→ UPDATE Courts
→ flash + redirect adminCourts
```

### Service validation

- CourtID phải dương.
- Price parse được `BigDecimal` và không âm.
- Status phải thuộc whitelist `Available`, `Maintenance`, `Unavailable`.

### Vì sao đổi trạng thái không làm hỏng booking?

DAO lock row court bằng `UPDLOCK,HOLDLOCK`. Nếu status mới khác `Available`, DAO đếm booking tương lai của sân qua `TimeSlots JOIN Bookings` với status:

- `Pending`;
- `Confirmed`;
- `CheckedIn`.

Chỉ booking có start time sau `now` mới chặn. Nếu `blockingCount > 0`, transaction không update court và Service trả thông báo số booking đang chặn.

Nếu không có booking tương lai, court được update. Booking cũ không bị xóa hay đổi status.

### Một chi tiết quan trọng

Update court hiện chỉ sửa `Courts.PricePerHour` và `Courts.Status`, không bulk-update các `TimeSlots` cũ. Tuy nhiên `BookingService` luôn kiểm tra Court.Status lúc đặt, nên court không Available vẫn không thể nhận booking dù một slot cũ còn status Available.

## 5. Thêm slot tuần mới

### Form và route

```text
POST action=adminCourts
operation=generateWeek
weekStart=<thứ Hai>
csrfToken=...
```

### Luồng

```text
AdminCourtsServlet
→ WeeklySlotService.generate()
→ validate đúng thứ Hai và không quá khứ
→ TimeSlotDAO.generateWeek()
→ SERIALIZABLE transaction
→ sinh 7 × 15 × số sân active
→ bỏ qua slot trùng
→ GenerateResult(created, skipped)
→ flash message + redirect
```

Chi tiết SQL và giới hạn 1.050 slot được giải thích trong tài liệu sân/khung giờ.

## 6. Danh sách và filter booking admin

### Request

```text
GET action=adminBookings&status=all|Pending|Confirmed|CheckedIn|Completed|Cancelled|NoShow
```

### Luồng

```text
admin/bookings.jsp filter form
→ AdminBookingsServlet.doGet()
→ requireAdmin + ensure CSRF + consume flash
→ AdminBookingService.list(status)
→ reconcile()
→ normalize status whitelist
→ AdminBookingDAO.findAll(status hoặc null)
→ request bookingList + selectedStatus
→ admin/bookings.jsp
```

Giá trị filter lạ được chuẩn hóa thành null, nghĩa là xem tất cả. DAO dùng parameter bind nếu có status.

Query JOIN `Bookings`, `Accounts`, `Roles`, `TimeSlots`, `Courts`, `CourtTypes`, LEFT JOIN `Invoices` để trả đủ customer, sân, giờ, booking status và payment status.

## 7. Chi tiết booking admin

### Request

```text
GET action=adminBookings&id=<bookingId>
```

### Luồng

```text
AdminBookingsServlet.doGet()
→ AdminBookingService.getDetail(id)
→ reconcile()
→ AdminBookingDAO.findDetail(id)
→ AdminBookingDAO.findServices(id)
→ service.canCheckIn(booking, now)
→ request booking/serviceLines/canCheckIn
→ admin/bookings.jsp nhánh detail
```

Admin được phép xem mọi booking nên DAO không lọc theo accountID như user flow.

## 8. Check-in

### Điều kiện mở nút

`AdminBookingService.canCheckIn()` trả true khi:

- booking status là `Confirmed`;
- có slot date/start time hợp lệ;
- `now >= start`;
- `now < start + 30 phút`.

Không check-in trước giờ bắt đầu. Mốc đúng start được phép; đúng start + 30 phút không được phép.

### Request

```text
POST action=adminBookings
operation=checkIn
bookingId=...
csrfToken=...
```

### Luồng

```text
admin/bookings.jsp
→ AdminBookingsServlet.doPost()
→ requireAdmin + CSRF + operation whitelist
→ AdminBookingService.checkIn()
→ reconcile trước
→ load lại booking
→ kiểm tra canCheckIn
→ AdminBookingDAO.checkIn()
→ UPDATE chỉ khi status vẫn Confirmed và thời gian vẫn trong cửa sổ
→ flash + redirect detail
```

Service check trước để trả message rõ ràng; DAO check lại trong điều kiện UPDATE để chống trạng thái thay đổi giữa lúc đọc và ghi.

Khi thành công:

- `Bookings.Status=CheckedIn`;
- `CheckInAt=now`;
- `UpdatedAt=now`.

## 9. Auto NoShow và Completed thực sự chạy thế nào?

`AdminBookingDAO.reconcile(now)` chạy hai UPDATE trong một transaction:

### Confirmed → NoShow

Nếu booking vẫn `Confirmed` và thời gian hiện tại đã đạt `slot start + 30 phút`, chuyển `NoShow`.

### CheckedIn → Completed

Nếu booking `CheckedIn` và hiện tại đã đạt `slot end`, chuyển `Completed`.

### Khi nào reconcile được gọi?

- mở dashboard admin;
- mở danh sách booking admin;
- mở chi tiết booking admin;
- thực hiện check-in.

Vì vậy đây là **lazy auto-update**. Không có `Timer`, `ScheduledExecutorService`, Quartz hay cron. Nếu không ai truy cập các luồng admin, database chưa tự đổi trạng thái đúng từng giây; lần truy cập kế tiếp mới reconcile.

## 10. Slot sau khi hết giờ

Slot được tạo theo ngày và giờ cụ thể. Khi thời gian đã qua, UI/controller đánh dấu past nên không thể đặt lại. Hệ thống không cần đổi slot cũ về `Available` để tái sử dụng vì slot đó gắn với ngày đã qua. Tuần/ngày mới dùng row TimeSlot mới.

## 11. Booking vãng lai và hóa đơn

Admin bấm “Đặt sân vãng lai” nhưng dùng chung form/user flow:

```text
admin page
→ action=bookingCreate
→ BookingCreateServlet cho phép role Admin
→ BookingService.create(accountID admin, ...)
→ redirect adminBookings detail
```

DAO hiển thị account có role Admin thành “Khách vãng lai”. Payment/Invoice vẫn dùng ownership account admin đó.

## 12. Câu hỏi dễ bị hỏi

**Tại sao không cho đóng sân đang có booking tương lai?**  
Để không phá hợp đồng đã xác nhận/đang giữ slot; admin phải xử lý booking trước.

**NoShow có scheduler không?**  
Không. Hiện tại là lazy reconciliation khi admin truy cập.

**Vì sao vừa kiểm tra cửa sổ ở Service vừa kiểm tra trong SQL UPDATE?**  
Service cho message nghiệp vụ; SQL bảo vệ khỏi race condition.

**Tổng doanh thu tính thế nào?**  
Chỉ SUM `FinalAmount` của invoice Paid, không cộng booking Pending/Cancelled hay invoice Unpaid.

