# 03. Sân và khung giờ

## 1. Trang chủ

### Luồng

```text
GET action=home hoặc không có action
→ MainController
→ HomeServlet.processRequest()
→ CourtDAO.countByType("tennis")
→ CourtDAO.countByType("pickleball")
→ request attributes
→ home/index.jsp
```

`HomeServlet` gọi DAO trực tiếp vì chỉ cần hai count đơn giản. DAO JOIN `Courts` và `CourtTypes`, chỉ đếm row có `c.IsActive=1` và `ct.IsActive=1`.

Attributes:

- `tennisCourtCount`;
- `pickleballCourtCount`.

Home cũng gọi `CsrfUtil.ensureToken()` để layout/form dùng chung có token trong session.

## 2. Danh sách sân và filter

### Giao diện

`court/list.jsp` tạo các link dạng:

```text
action=courts&type=all
action=courts&type=tennis
action=courts&type=pickleball
```

### Luồng

```text
court/list.jsp hoặc navbar
→ GET action=courts&type=...
→ CourtsServlet.doGet()
→ normalizeTypeFilter()
→ CourtDAO.findAll(typeFilter)
→ CourtDAO.countByType() × 2
→ courtList/typeFilter/tennisCount/pickleballCount
→ court/list.jsp
```

Controller whitelist filter: chỉ `tennis` và `pickleball` được giữ; null/rỗng/giá trị lạ thành `all`. DAO vẫn bind filter bằng `PreparedStatement`, không nối raw input vào SQL.

DAO chỉ trả sân và loại sân có `IsActive=1`, sắp theo `CourtID`. Sân `Maintenance` hoặc `Unavailable` vẫn xuất hiện để khách xem thông tin, nhưng UI không cho đặt.

## 3. Chi tiết sân và lịch theo ngày

### Request

```text
GET action=court&id=<courtId>&date=<yyyy-MM-dd>
```

### Luồng

```text
court/list.jsp
→ CourtDetailServlet.doGet()
→ parsePositiveInteger(id)
→ resolveSelectedDate(date)
→ CourtDAO.findById(id)
→ TimeSlotDAO.listByCourtAndDate(trustedCourtId, date)
→ markPastSlots(now)
→ tạo 7 DateOption
→ court/detail.jsp
```

### Quy tắc ID

ID null, không phải số, số âm/0 hoặc không tồn tại đều trả 404. `CourtDAO.findById()` còn trả null nếu court hoặc court type inactive.

Sau khi load được `Court`, servlet dùng `court.getCourtID()` để query slot thay vì tiếp tục tin raw parameter.

### Quy tắc ngày

`SCHEDULE_DAY_COUNT=7`, nên phạm vi là hôm nay đến hôm nay + 6 ngày:

- ngày sai format/null → hôm nay;
- ngày trong quá khứ → hôm nay;
- ngày sau phạm vi → ngày thứ 7 gần nhất.

Đây là lý do admin có thể sinh slot cho tuần xa hơn nhưng user vẫn chưa chọn được nếu ngày đó nằm ngoài cửa sổ 7 ngày. Slot đã tồn tại trong database không đồng nghĩa UI cho phép duyệt tới ngày đó.

### DAO lấy slot

`TimeSlotDAO.listByCourtAndDate(courtID, slotDate)` query `TimeSlots` theo sân + ngày và sắp theo `StartTime`. DAO trả đủ:

- `Available`;
- `Booked`;
- `Closed`.

Không lọc chỉ `Available` vì giao diện cần hiển thị cả slot đã đặt/đóng.

Thuộc tính `past` không nằm trong database. Servlet tính bằng thời gian hiện tại: slot bắt đầu đúng thời điểm hiện tại cũng bị xem là đã qua và không thể đặt.

## 4. Liên kết từ slot sang đặt sân

Nếu slot thỏa các điều kiện:

- sân `Available`;
- slot `Available`;
- slot chưa bắt đầu;

JSP tạo link:

```text
action=bookingCreate&courtId=...&date=...&slotId=...
```

Các parameter này giúp preselect form. Khi lưu thật, `BookingService` chỉ tin `slotId` và đọc lại court/date/price từ database.

## 5. Trạng thái sân và trạng thái slot khác nhau

### Court status

- `Available`: sân nhận booking.
- `Maintenance`: sân bảo trì.
- `Unavailable`: sân tạm đóng.

### TimeSlot status

- `Available`: slot chưa đặt.
- `Booked`: đã được giữ bởi booking.
- `Closed`: slot không phục vụ.

Khi tạo booking, Service kiểm tra đồng thời `Court.Status`, `Court.IsActive`, `CourtType.IsActive` và `TimeSlot.Status`. Vì vậy dù một slot còn chữ `Available`, court `Maintenance` vẫn không thể nhận booking.

## 6. Admin thêm slot theo tuần

Phần UI nằm trong `admin/courts.jsp`:

```text
POST action=adminCourts
operation=generateWeek
weekStart=<yyyy-MM-dd>
csrfToken=...
```

Luồng đầy đủ:

```text
admin/courts.jsp
→ AdminCourtsServlet.doPost()
→ requireAdmin + CSRF
→ parse LocalDate
→ WeeklySlotService.generate(monday)
→ validate thứ Hai và không quá khứ
→ TimeSlotDAO.generateWeek()
→ transaction SERIALIZABLE
→ CTE Days × Hours × Courts
→ INSERT những slot chưa tồn tại
→ flash message
→ redirect adminCourts
```

### SQL sinh slot

- `Days`: 7 ngày từ thứ Hai đến Chủ nhật.
- `Hours`: đệ quy từ `06:00–07:00` đến `20:00–21:00`, tổng 15 slot/ngày.
- `Courts`: tất cả sân active.
- CROSS JOIN ba tập để tạo tổ hợp.
- `NOT EXISTS` theo `(CourtID, SlotDate, StartTime)` để không insert trùng.
- Court đang `Available` tạo slot `Available`; court khác trạng thái tạo slot `Closed`.
- Isolation `SERIALIZABLE` giảm race condition nếu hai admin bấm cùng lúc.

Với 10 sân active:

```text
10 sân × 7 ngày × 15 slot = 1.050 slot/tuần
```

Nếu bấm lại tuần đã đủ, `created=0`, `skipped=1050`; dữ liệu cũ được giữ nguyên.

### Giới hạn cần nhớ

`GenerationResult` hiện tính `skipped = 1050 - created`, tức đang giả định luôn có 10 sân active. Nếu số sân active thay đổi, con số thông báo có thể không phản ánh chính xác tổng tổ hợp. Unique constraint trong database vẫn bảo vệ dữ liệu không trùng.

## 7. Unique constraint của slot

Database có:

```text
UNIQUE (CourtID, SlotDate, StartTime)
```

Nó đảm bảo một sân không có hai slot cùng ngày và cùng giờ bắt đầu. Đây là lớp bảo vệ cuối bên cạnh `NOT EXISTS` trong SQL generator.

