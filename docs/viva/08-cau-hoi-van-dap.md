# 08. Câu hỏi vấn đáp và cheat sheet

## 1. Bài nói tổng quan 60 giây

> SportZone Management là Java Web MVC dùng JSP/Servlet, JDBC và SQL Server. Mọi URL đi qua MainController theo Front Controller pattern, sau đó named-dispatch tới servlet chuyên trách. Servlet xử lý HTTP/session/CSRF, Service chứa business rule, DAO dùng PreparedStatement và transaction để truy cập 9 bảng. Chức năng chính gồm xem sân và lịch 7 ngày, đăng ký/đăng nhập/hồ sơ, đặt sân kèm dịch vụ, thanh toán QR giả lập, hóa đơn và membership. Admin có dashboard doanh thu, booking vãng lai, quản lý sân, sinh slot tuần, check-in và lazy reconcile NoShow/Completed. Điểm mạnh dữ liệu là ownership query, snapshot giá, row locking và unique index chống double-booking.

## 2. Bài nói kiến trúc 30 giây

```text
JSP → MainController → Servlet → Service → DAO → SQL Server
                                      ← Model/Result ←
```

Không phải flow nào cũng cần Service. Read-only đơn giản như danh sách sân/dashboard có thể gọi DAO trực tiếp; flow phức tạp như booking/payment dùng Service và transaction.

## 3. Bài nói tạo booking 60 giây

> Form gửi slotId, serviceId, quantity và notes. Controller kiểm tra session, role, CSRF rồi parse input. BookingService mở transaction, lock account và slot/court bằng UPDLOCK HOLDLOCK, kiểm tra account Active, court/slot Available và slot chưa bắt đầu. Giá luôn đọc lại từ database, sau đó snapshot vào Booking và BookingServices. DAO insert Booking Pending, insert service lines, đổi TimeSlot sang Booked và commit. Nếu request khác lấy cùng slot, conditional update hoặc filtered unique index sẽ chặn; toàn bộ transaction rollback.

## 4. Bài nói payment 45 giây

> Booking ban đầu chưa có invoice. Khi user mở thanh toán, InvoiceDAO tạo tối đa một invoice Unpaid từ TotalAmount snapshot. Nút thanh toán mô phỏng đổi invoice thành Paid trong hạn 15 phút và đổi booking Pending thành Confirmed trong cùng transaction. Hủy hoặc hết hạn đổi cả invoice/booking thành Cancelled và nhả slot nếu không còn active booking. PaymentStatus thuộc Invoice, lifecycle sân thuộc Booking.

## 5. Bài nói check-in 30 giây

> Chỉ booking Confirmed được check-in từ đúng giờ bắt đầu đến trước 30 phút sau giờ bắt đầu. Service kiểm tra để trả message, DAO kiểm tra lại trong điều kiện UPDATE để chống race. Thành công chuyển status sang CheckedIn và lưu CheckInAt. Sau start 30 phút mà vẫn Confirmed thì reconcile chuyển NoShow; sau end time mà đã CheckedIn thì chuyển Completed.

## 6. 30 câu hỏi và câu trả lời ngắn

### 1. Vì sao dùng MainController?

Để có một entry point thống nhất và tách URL bên ngoài khỏi servlet class cụ thể.

### 2. MainController có chứa nghiệp vụ không?

Không. Nó chỉ resolve action và forward bằng named dispatcher.

### 3. Vì sao JSP đặt dưới WEB-INF?

Ngăn truy cập trực tiếp, bắt buộc request đi qua controller để có data và security check.

### 4. Khi nào forward, khi nào redirect?

Forward để render JSP trong cùng request; redirect sau POST để tránh submit trùng và tạo request GET mới.

### 5. Service khác DAO thế nào?

Service quyết định nghiệp vụ; DAO chỉ làm persistence/SQL/mapping/transaction primitive.

### 6. Vì sao PreparedStatement an toàn hơn nối chuỗi?

Value được bind riêng khỏi SQL syntax, giảm SQL injection và xử lý type đúng.

### 7. CSRF token được tạo ở đâu?

`CsrfUtil.ensureToken()` tạo 32 random bytes, Base64 URL-safe và lưu session.

### 8. Sai CSRF xảy ra gì?

Controller trả HTTP 403 trước khi gọi Service/DAO.

### 9. AuthFilter làm gì?

Cho public action đi qua; protected action cần account session, nếu thiếu thì lưu GET target và redirect login.

### 10. FrozenAccountFilter làm gì?

Cách ly account Frozen, chỉ cho xem trang locked, logout và asset.

### 11. Role admin được kiểm tra ở đâu?

Admin servlet gọi `requireAdmin()`; RoleFilter bổ sung cho `/admin/*`.

### 12. Vì sao account ID không lấy từ form profile?

Phải lấy từ session tin cậy để chống sửa account khác.

### 13. Đăng ký chống email trùng thế nào?

Service check trước cho UX, database unique constraint chống race condition.

### 14. Mật khẩu được lưu thế nào?

SHA-256 hex trong demo; session không giữ passwordHash.

### 15. Vì sao SHA-256 chưa đủ production?

Không salt và quá nhanh, dễ brute-force; nên dùng BCrypt/Argon2.

### 16. Vì sao trang sân chỉ thấy 7 ngày?

`SCHEDULE_DAY_COUNT=7`; controller clamp ngày từ hôm nay đến hôm nay + 6.

### 17. Admin sinh tuần sau rồi sao user vẫn chưa đặt được?

Slot có trong DB nhưng UI/controller booking chỉ cho cửa sổ 7 ngày.

### 18. Một ngày có bao nhiêu slot?

15 slot một giờ, từ 06:00–07:00 đến 20:00–21:00.

### 19. Bấm sinh slot cùng tuần có trùng không?

Không. SQL có NOT EXISTS và DB có unique `(CourtID, SlotDate, StartTime)`.

### 20. Vì sao Booking không lưu CourtID?

Court được suy ra từ TimeSlot; tránh dư thừa và sai lệch quan hệ.

### 21. Vì sao snapshot giá?

Giữ giá lịch sử không đổi khi admin cập nhật bảng giá.

### 22. Chống double-booking bằng gì?

Row lock, conditional update slot, transaction và filtered unique index active booking.

### 23. Ownership được kiểm tra thế nào?

SQL dùng đồng thời resource ID và AccountID lấy từ session.

### 24. Booking và Invoice status khác nhau sao?

Booking status mô tả vòng đời sử dụng sân; invoice status mô tả thu tiền.

### 25. Hủy payment có nhả slot không?

Có, trong transaction sau khi booking chuyển Cancelled và không còn active booking khác.

### 26. Membership được lưu ở Accounts không?

Không, tính động từ invoice Paid tháng trước và tháng hiện tại.

### 27. Doanh thu dashboard tính thế nào?

SUM `Invoices.FinalAmount` chỉ với PaymentStatus Paid.

### 28. Đổi sân Unavailable khi có booking thì sao?

DAO đếm booking tương lai Pending/Confirmed/CheckedIn; nếu có thì chặn update.

### 29. NoShow có chạy nền thật không?

Không. Reconcile chạy khi admin mở dashboard/booking hoặc check-in.

### 30. Payment có kết nối ngân hàng thật không?

Không. QR và nút pay chỉ mô phỏng state transition và transaction database.

## 7. Các trạng thái cần thuộc

### Account

```text
Active | Frozen | Pending
```

### Court

```text
Available | Maintenance | Unavailable
```

### TimeSlot

```text
Available | Booked | Closed
```

### Booking

```text
Pending | Confirmed | CheckedIn | Completed | Cancelled | NoShow
```

### Invoice

```text
Unpaid | Paid | Cancelled
```

## 8. Các con số cần thuộc

| Nội dung | Giá trị |
|---|---:|
| Session timeout | 30 phút |
| Booking UI window | 7 ngày |
| Slot mỗi ngày | 15 |
| Giờ hoạt động | 06:00–21:00 |
| Slot mỗi tuần với 10 sân | 1.050 |
| Payment timeout | 15 phút |
| Check-in window | `[start, start + 30 phút)` |
| Max notes | 255 ký tự |
| Max quantity mỗi service | 20 |
| Số bảng | 9 |

## 9. Cách trace một bug khi giáo viên hỏi

Ví dụ “bấm đặt sân nhưng báo slot không trống”:

1. Kiểm tra JSP gửi đúng `slotId` và CSRF không.
2. Kiểm tra `BookingCreateServlet` parse parameter.
3. Kiểm tra error code từ `BookingService.create()`.
4. Kiểm tra `BookingDAO.findSlotForUpdate()` đọc `TimeSlots.Status`, `Courts.Status`.
5. Kiểm tra transaction có rollback không.
6. Kiểm tra unique index active booking.

Trả lời theo chuỗi này chứng minh hiểu luồng thay vì chỉ đoán ở giao diện.

## 10. Những điều không nên nói sai

- Không nói NoShow có cron/scheduler; source không có.
- Không nói thanh toán thật; đây là simulated bank transfer.
- Không nói Booking lưu CourtID; nó lưu TimeSlotID.
- Không nói user tự chọn AccountID; account lấy từ session.
- Không nói sinh slot sẽ xóa/tạo lại; nó chỉ insert phần thiếu.
- Không nói invoice tạo cùng booking; invoice tạo khi bắt đầu payment.
- Không nói admin booking có customer entity riêng; hiện dùng account admin và hiển thị “Khách vãng lai”.

