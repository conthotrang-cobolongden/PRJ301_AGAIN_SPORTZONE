# Admin MVP Design

**Ngày:** 2026-08-24  
**Trạng thái:** Đã chốt trong hội thoại, chờ người dùng duyệt bản ghi  
**Phạm vi:** Gộp Phase 7 và Phase 8 thành một Admin MVP nhỏ, đủ demo và ưu tiên tái sử dụng luồng hiện có.

## 1. Mục tiêu

Admin MVP chỉ cung cấp các chức năng cần thiết để:

- xem nhanh dữ liệu hệ thống và tổng doanh thu;
- quản lý giá và trạng thái của 10 sân cố định;
- sinh lịch sân theo tuần;
- xem booking toàn hệ thống;
- tạo booking vãng lai;
- check-in khách và tự khép vòng booking bằng `Completed` hoặc `NoShow`.

Các chức năng quản trị không nằm trong danh sách trên được khóa và chuyển sang Future Features.

## 2. Dashboard Admin

Dashboard hiển thị đúng bốn card:

1. Tổng số tài khoản.
2. Tổng số sân active.
3. Tổng số booking.
4. Tổng doanh thu toàn thời gian.

Tổng doanh thu là tổng `Invoices.FinalAmount` của các invoice có `PaymentStatus = 'Paid'`. Invoice `Unpaid` và `Cancelled` không được tính. Không triển khai biểu đồ, so sánh kỳ hoặc trang báo cáo riêng.

## 3. Quản lý sân

Admin xem danh sách 10 sân cố định và chỉ được:

- sửa `PricePerHour`;
- đổi `Status` giữa `Available`, `Maintenance` và `Unavailable`.

Không cho sửa tên, loại sân, vị trí, mô tả, ảnh hoặc `IsActive`. Không tạo hay xóa sân.

### 3.1 Quy tắc an toàn khi đổi trạng thái

Chuyển từ `Maintenance` hoặc `Unavailable` về `Available` luôn được phép.

Chuyển từ `Available` sang `Maintenance` hoặc `Unavailable` bị từ chối nếu sân có booking tương lai ở trạng thái `Pending`, `Confirmed` hoặc `CheckedIn`. Booking tương lai được xác định bằng thời điểm kết hợp `TimeSlots.SlotDate` và `TimeSlots.StartTime` lớn hơn thời điểm hiện tại.

Thông báo từ chối phải nêu số booking đang cản thao tác. Không tự hủy booking để đổi trạng thái sân.

Đổi trạng thái sân chỉ cập nhật `Courts.Status`; không cập nhật dây chuyền sang `TimeSlots`, `Bookings` hoặc `Invoices`. Backend tạo booking tiếp tục kiểm tra `Courts.Status = 'Available'` trong transaction. Giá của booking cũ không đổi vì đã được snapshot trong `Bookings.CourtPriceAtOrder`.

## 4. Sinh TimeSlot theo tuần

Trang Admin có form chọn một tuần, mặc định là tuần kế tiếp. Tuần được hiểu là từ thứ Hai đến Chủ nhật.

Khi Admin xác nhận, hệ thống sinh slot cho toàn bộ 10 sân và tám khung giờ cố định mỗi ngày:

- 06:00–07:00
- 07:00–08:00
- 08:00–09:00
- 16:00–17:00
- 17:00–18:00
- 18:00–19:00
- 19:00–20:00
- 20:00–21:00

Một lần sinh có tối đa `10 × 7 × 8 = 560` slot.

Quy tắc:

- sân `Available` tạo slot `Available`;
- sân `Maintenance` hoặc `Unavailable` tạo slot `Closed`;
- chỉ insert slot chưa tồn tại theo khóa `(CourtID, SlotDate, StartTime)`;
- không ghi đè slot `Available`, `Booked` hoặc `Closed` đã tồn tại;
- chạy lại cùng một tuần là idempotent và không sinh trùng;
- kết quả hiển thị số slot đã tạo và số slot đã bỏ qua.

Không triển khai CRUD slot thủ công.

## 5. Booking Admin

### 5.1 Danh sách và chi tiết

Admin được:

- xem toàn bộ booking;
- lọc theo một trạng thái booking;
- xem chi tiết gồm người đặt, sân, ngày giờ, dịch vụ, tổng tiền, trạng thái booking và trạng thái thanh toán.

Không triển khai tìm kiếm nâng cao, phân trang phức tạp, sửa nội dung booking, hủy booking hoặc hoàn tiền.

### 5.2 Booking vãng lai

Role Admin được sử dụng form tạo booking hiện tại. Vì `Bookings.AccountID` là bắt buộc, booking vãng lai lưu `AccountID` của Admin đang thao tác.

Quy ước hiển thị:

- booking có account mang role `Admin` được xem là booking vãng lai;
- tên khách trên màn booking và invoice là `Khách vãng lai`, không phải tên tài khoản Admin;
- không lưu tên, email hoặc số điện thoại thật của khách vãng lai;
- mọi booking do User tạo vẫn hiển thị thông tin User bình thường.

Admin booking vẫn dùng các kiểm tra transaction hiện hành: account active, sân available, slot available, thời gian hợp lệ, dịch vụ hợp lệ, snapshot giá và chống double-book.

Sau khi tạo, Admin được dùng luồng QR giả lập hiện tại cho booking thuộc chính account Admin. Thanh toán thành công tạo hoặc cập nhật invoice thành `Paid`, đặt `PaymentMethod = 'BankTransfer'` và chuyển booking từ `Pending` sang `Confirmed`. Doanh thu booking vãng lai được tính như booking User.

## 6. Check-in và tự động khép vòng booking

### 6.1 Thay đổi dữ liệu

Bảng `Bookings` bổ sung:

```sql
CheckInAt DATETIME NULL
```

Constraint trạng thái bổ sung `CheckedIn`:

```text
Pending, Confirmed, CheckedIn, Completed, Cancelled, NoShow
```

Unique index chống double-book phải xem `CheckedIn` là trạng thái đang chiếm slot. Không thêm bảng mới và không lưu thời điểm check-out riêng.

### 6.2 Check-in

Admin chỉ được check-in booking khi:

- booking đang `Confirmed`;
- thời điểm hiện tại không sớm hơn 30 phút trước `StartTime` và phải nhỏ hơn `StartTime + 30 phút`.

Check-in dùng conditional update để chống thao tác đồng thời:

```text
Status = CheckedIn
CheckInAt = thời điểm hiện tại
UpdatedAt = thời điểm hiện tại
```

### 6.3 Auto NoShow và Auto Completed

Để tránh background thread trong ứng dụng Servlet, hệ thống dùng lazy reconciliation. Trước khi hiển thị Dashboard Admin, danh sách booking Admin hoặc chi tiết booking Admin, hệ thống thực hiện hai cập nhật có điều kiện:

- `Confirmed` và thời điểm hiện tại lớn hơn hoặc bằng `StartTime + 30 phút` → `NoShow`;
- `CheckedIn` và đã đến hoặc qua `EndTime` → `Completed`.

Vì đây là lazy reconciliation, trạng thái được đảm bảo đúng tại request liên quan tiếp theo; không cam kết cập nhật chính xác từng giây khi hệ thống không có request.

Không có nút check-out và không có nút NoShow thủ công trong MVP.

### 6.4 Slot và thanh toán

TimeSlot gắn với một ngày và khoảng giờ cụ thể nên không cần “nhả slot” khi hết giờ. Slot đã qua giữ `Booked` để bảo toàn lịch sử; slot kế tiếp là bản ghi độc lập và vẫn có thể được đặt.

Booking `NoShow` giữ nguyên invoice `Paid`, không hoàn tiền và vẫn được tính vào doanh thu. `Completed` và `NoShow` là trạng thái kết thúc.

## 7. Điều hướng và bảo mật

Menu Admin gồm:

```text
Dashboard
Quản lý sân
Sinh lịch theo tuần
Danh sách booking
Tạo booking vãng lai
```

Các action Admin tiếp tục được dispatch qua `MainController`. Mỗi controller/servlet Admin phải xác minh session đã đăng nhập, role là `Admin`, account không bị `Frozen`, và mọi POST phải kiểm tra CSRF.

Admin chỉ được thanh toán hoặc mở invoice của booking vãng lai thuộc chính `AccountID` Admin đang đăng nhập. Các kiểm tra ownership của User không bị nới rộng.

## 8. Future Features

Không triển khai trong Admin MVP:

- quản lý và khóa/mở tài khoản User;
- sửa đầy đủ thông tin sân;
- CRUD TimeSlot thủ công;
- quản lý dịch vụ;
- xác nhận hoặc hủy booking thủ công;
- check-out thủ công;
- NoShow thủ công;
- quản lý, hủy hoặc hoàn tiền invoice;
- thanh toán tiền mặt;
- in hoặc xuất hóa đơn;
- báo cáo theo ngày, tuần, tháng;
- biểu đồ, Excel hoặc PDF;
- background scheduler thực sự;
- lưu thông tin định danh khách vãng lai.

Các mục Future Features bị ẩn khỏi menu Admin trong MVP và không có route công khai.

## 9. Luồng demo chấp nhận

1. Admin đăng nhập và thấy bốn card Dashboard.
2. Admin sinh slot cho tuần kế tiếp; chạy lại cùng tuần không tạo trùng.
3. Admin sửa giá một sân.
4. Admin đổi trạng thái sân không có booking tương lai.
5. Admin bị từ chối khi ngừng sân đang có booking tương lai.
6. Admin tạo booking vãng lai và thanh toán QR giả lập.
7. Invoice hiển thị `Khách vãng lai`; Dashboard cộng doanh thu Paid.
8. Trong cửa sổ hợp lệ, Admin check-in booking thành `CheckedIn`.
9. Sau giờ kết thúc, request Admin tiếp theo tự chuyển booking thành `Completed`.
10. Booking Confirmed quá giờ bắt đầu 30 phút mà chưa check-in tự chuyển thành `NoShow`; invoice vẫn `Paid`.

## 10. Tiêu chí hoàn tất

- Các chức năng trong mục 2–7 hoạt động theo đúng rule và không mở thêm Future Features.
- Sinh tuần không trùng và không ghi đè slot hiện có.
- Đổi trạng thái sân không phá booking, slot hoặc invoice đã tồn tại.
- Admin tạo booking vãng lai không làm lỏng ownership của User.
- Check-in, auto NoShow và auto Completed dùng conditional update an toàn.
- Mọi POST Admin có CSRF; role User không gọi được action Admin.
- Schema, seed, Java model, DAO/service/controller, JSP và contract tests liên quan được đồng bộ với `CheckedIn` và `CheckInAt`.
