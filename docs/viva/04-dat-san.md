# 04. Đặt sân

## 1. Hiển thị form đặt sân

### Request

```text
GET action=bookingCreate&courtId=...&date=...&slotId=...
```

### Luồng

```text
Court detail hoặc nút đặt sân
→ AuthFilter yêu cầu đăng nhập
→ BookingCreateServlet.doGet()
→ requireBookingActor(User hoặc Admin)
→ CsrfUtil.ensureToken()
→ CourtDAO.findAll()
→ lọc court Active + Available
→ TimeSlotDAO.listByCourtAndDate()
→ ServiceDAO.findAllActive()
→ request attributes
→ booking/create.jsp
```

Controller chỉ cho role `User` hoặc `Admin`. Ngày đặt bị giới hạn từ hôm nay đến hôm nay + 6, giống trang chi tiết sân.

Các attribute chính:

| Attribute | Ý nghĩa |
|---|---|
| `courtList` | Các sân có thể nhận booking |
| `selectedCourtId` | Sân được chọn |
| `selectedCourtPrice` | Giá hiện tại để UI dự tính |
| `selectedDate` | Ngày được chọn |
| `minDate`, `maxDate` | Giới hạn date input |
| `selectedSlotId` | Slot preselect |
| `slotList` | Slot của sân/ngày |
| `serviceList` | Dịch vụ active |
| `selectedServiceQuantities` | Dữ liệu giữ lại khi form lỗi |

## 2. Form gửi dữ liệu gì?

`booking/create.jsp` POST tới `action=bookingCreate` với:

- `csrfToken`;
- `courtId` và `date` để render lại form nếu lỗi;
- `slotId` là slot thực sự cần đặt;
- nhiều `serviceId`;
- quantity tương ứng có tên `qty_<serviceId>`;
- `notes`.

Controller parse service thành `Map<ServiceID, Quantity>`, chặn ID trùng, ID sai, hơn 20 loại hoặc quantity ngoài 1–20.

## 3. Tạo booking — luồng tổng thể

```text
booking/create.jsp
→ POST action=bookingCreate
→ BookingCreateServlet.doPost()
→ CSRF + role + frozen check
→ parse slot/service/notes
→ BookingService.create(accountID, timeSlotID, map, notes)
→ BookingDAO + ServiceDAO trong transaction
→ insert Bookings
→ insert BookingServices
→ update TimeSlots.Status=Booked
→ commit
→ redirect bookingDetail hoặc adminBookings
```

User thành công được redirect tới `bookingDetail&id=...&created=1`. Admin thành công được redirect tới `adminBookings&id=...&created=1`.

## 4. Transaction tạo booking từng bước

`BookingService.create()` mở một connection và `setAutoCommit(false)`.

### Bước 1 — Lock account

`BookingDAO.findAccountStatusForUpdate()` dùng:

```sql
SELECT Status
FROM Accounts WITH (UPDLOCK, HOLDLOCK)
WHERE AccountID = ?
```

Chỉ account `Active` được tiếp tục.

### Bước 2 — Lock slot và court

`BookingDAO.findSlotForUpdate()` JOIN `TimeSlots`, `Courts`, `CourtTypes` và lock slot/court bằng `UPDLOCK, HOLDLOCK`.

Service kiểm tra:

- slot tồn tại;
- slot status là `Available`;
- court và court type active;
- court status là `Available`;
- giá sân hợp lệ;
- thời điểm bắt đầu phải sau hiện tại.

### Bước 3 — Xác minh dịch vụ

Với từng service ID, `ServiceDAO.findActiveByID(connection, id)` đọc lại từ database. Client không được tự gửi giá.

Service tính:

```text
lineTotal = service.Price × quantity
serviceTotal = tổng lineTotal
totalAmount = court.PricePerHour + serviceTotal
```

### Bước 4 — Snapshot giá

Booking lưu:

- `CourtPriceAtOrder`;
- `ServiceTotalAtOrder`;
- `TotalAmount`.

Mỗi `BookingServices` lưu `PriceAtOrder`. Nhờ vậy admin đổi giá sân/dịch vụ sau này không sửa hóa đơn lịch sử.

### Bước 5 — Insert booking

`BookingDAO.insertBooking()` insert status `Pending` và lấy generated `BookingID`.

Bookings không có `CourtID`; chỉ lưu `TimeSlotID`. Quan hệ tới sân là:

```text
Bookings.TimeSlotID
→ TimeSlots.TimeSlotID
→ TimeSlots.CourtID
→ Courts.CourtID
```

### Bước 6 — Insert dịch vụ

Mỗi dịch vụ được insert vào `BookingServices` với khóa chính ghép `(BookingID, ServiceID)`.

### Bước 7 — Giữ slot

DAO chạy:

```sql
UPDATE TimeSlots
SET Status = N'Booked'
WHERE TimeSlotID = ? AND Status = N'Available'
```

Nếu không update đúng một row, có request khác vừa lấy slot; Service rollback toàn bộ.

### Bước 8 — Commit hoặc rollback

- Mọi bước thành công → commit.
- Business rule lỗi hoặc SQL lỗi → rollback.
- SQL Server error 2601/2627 liên quan unique active booking được đổi thành thông báo slot vừa được đặt.

## 5. Vì sao chống được double-booking?

Có nhiều lớp:

1. Lock slot bằng `UPDLOCK, HOLDLOCK` trong transaction.
2. Update slot chỉ khi status còn `Available`.
3. Filtered unique index `UQ_Bookings_Slot_Active` trên database ngăn nhiều booking active cùng `TimeSlotID`.
4. Nếu unique index ném lỗi, Service nhận diện error code và trả lỗi nghiệp vụ thay vì làm vỡ trang.

## 6. Booking của admin là khách vãng lai thế nào?

Admin dùng cùng `BookingCreateServlet` và cùng `BookingService`; không có bảng khách vãng lai riêng. Booking được gắn với account admin đang đăng nhập.

Khi DAO map booking/invoice:

```text
nếu RoleName của account = Admin
→ AccountName hiển thị "Khách vãng lai"
```

Vì vậy đây là quy ước hiển thị đơn giản. Muốn lưu tên/số điện thoại khách tại quầy riêng sẽ cần thay đổi schema và form trong tương lai.

## 7. Danh sách booking của user

### Luồng

```text
GET action=bookings&tab=upcoming|history
→ BookingsServlet.doGet()
→ require role User
→ BookingService.listMine(accountID)
→ BookingDAO.findByAccountID(accountID)
→ chia upcoming/history tại Controller
→ booking/list.jsp
```

Ownership nằm ngay trong SQL: `WHERE b.AccountID = ?`.

Booking được coi là upcoming nếu:

- status là `Pending` hoặc `Confirmed`;
- slot start nằm sau thời điểm hiện tại.

Các trường hợp còn lại vào history. Controller sort upcoming tăng dần theo ngày và giờ.

## 8. Chi tiết booking của user

### Luồng

```text
GET action=bookingDetail&id=<bookingId>
→ BookingDetailServlet
→ require role User
→ BookingService.getMine(bookingID, accountID session)
→ BookingDAO.findDetailByIDAndAccountID()
→ BookingDAO.findServicesByBookingIDAndAccountID()
→ booking/detail.jsp
```

Cả booking và service lines đều lọc bằng `BookingID + AccountID`. Nếu ID có tồn tại nhưng thuộc user khác, DAO trả null và controller trả 404. Đây là chống IDOR/đọc trộm dữ liệu.

## 9. Vòng đời booking cơ bản

```text
Tạo booking
    ↓
 Pending
    ├─ thanh toán Paid → Confirmed
    └─ hủy/hết hạn payment → Cancelled

Confirmed
    ├─ check-in đúng cửa sổ → CheckedIn
    └─ quá 30 phút chưa check-in → NoShow

CheckedIn
    └─ hết giờ slot → Completed
```

`PaymentStatus` không được trộn với `Booking.Status`. `Paid/Unpaid/Cancelled` thuộc `Invoices`; `Pending/Confirmed/CheckedIn/...` thuộc `Bookings`.

## 10. Câu hỏi dễ bị hỏi

**Vì sao client gửi courtId nhưng Service không dùng để insert?**  
`courtId` chỉ phục vụ UI. Server lấy court thật qua slot ID để ngăn client ghép slot của sân A với giá/tên sân B.

**Vì sao chưa tạo Invoice ngay khi tạo Booking?**  
Booking giữ slot trước; Invoice chỉ được tạo khi người dùng bắt đầu thu tiền. Quan hệ là Booking 1 → 0..1 Invoice.

**Vì sao cần snapshot giá?**  
Để dữ liệu lịch sử ổn định và đối soát được dù bảng giá thay đổi.

