# 05. Thanh toán, hóa đơn và membership

## 1. Mô hình dữ liệu thanh toán

Một booking có thể chưa có invoice. `Invoices.BookingID` là UNIQUE nên một booking có tối đa một invoice.

Hai state machine tách biệt:

```text
Bookings.Status:
Pending → Confirmed → CheckedIn → Completed
          └──────────────→ NoShow
Pending/Confirmed → Cancelled

Invoices.PaymentStatus:
Unpaid → Paid
Unpaid → Cancelled
```

## 2. Bắt đầu thanh toán

### Giao diện

Từ `booking/detail.jsp` hoặc trang admin booking, người dùng mở:

```text
GET action=invoicePayment&bookingId=<id>
```

### Luồng

```text
booking/detail.jsp
→ InvoicePaymentServlet.doGet()
→ require User hoặc Admin
→ InvoiceService.startPayment(bookingID, sessionAccountID)
→ InvoiceDAO.startPayment()
→ transaction
→ xử lý invoice Unpaid hết hạn của account
→ tìm invoice hiện có
→ nếu chưa có: kiểm tra booking ownership/status và INSERT Invoice Unpaid
→ invoice/payment.jsp
```

DAO chỉ tạo invoice khi booking thuộc đúng account và status là `Pending` hoặc `Confirmed`. `InvoiceCode` có dạng `INV-%06d` dựa trên BookingID.

Các số tiền invoice lấy từ `Bookings.TotalAmount`, không tính lại từ giá hiện tại.

Nếu invoice đã Paid, servlet redirect thẳng tới chi tiết hóa đơn. Nếu invoice đã Cancelled, DAO đồng bộ booking thành Cancelled và nhả slot nếu an toàn.

## 3. Xác nhận thanh toán giả lập

### Request

```text
POST action=invoicePayment
operation=pay
bookingId=...
csrfToken=...
```

### Luồng

```text
invoice/payment.jsp
→ InvoicePaymentServlet.doPost()
→ CSRF + account + bookingId
→ InvoiceService.pay()
→ InvoiceDAO.markPaid()
→ transaction
→ expire invoice quá hạn
→ UPDATE Invoice Unpaid thành Paid nếu còn trong 15 phút
→ UPDATE Booking Pending thành Confirmed
→ load lại Invoice
→ commit
→ redirect invoiceDetail
```

`markPaid()` chỉ update khi:

- booking thuộc account hiện tại;
- invoice đang `Unpaid`;
- `InvoiceDate + 15 phút > GETDATE()`.

Khi Paid:

- `PaymentStatus=Paid`;
- `PaymentMethod=BankTransfer`;
- `PaidAt=GETDATE()`;
- booking `Pending → Confirmed`.

PaymentStatus không được ghi vào `Bookings`.

## 4. Hủy thanh toán

### Request

```text
POST action=invoicePayment
operation=cancel
```

### Luồng

```text
payment.jsp
→ InvoicePaymentServlet
→ InvoiceService.cancel()
→ InvoiceDAO.cancelPayment()
→ Invoice Unpaid → Cancelled
→ Booking Pending/Confirmed → Cancelled
→ TimeSlot Booked → Available nếu không còn active booking khác
→ commit
→ redirect lại trang payment
```

DAO dùng transaction để invoice, booking và slot không rơi vào trạng thái nửa vời.

## 5. Payment timeout 15 phút

`InvoiceDAO.expireOwnedUnpaid()` được gọi khi bắt đầu/đánh dấu payment. Nó:

1. Tìm invoice `Unpaid` của account có `InvoiceDate + 15 phút <= hiện tại`.
2. Chuyển booking liên quan sang `Cancelled` với reason `Payment expired`.
3. Chuyển invoice sang `Cancelled`.
4. Nhả các slot cancelled nếu không còn booking active khác.

Đây là lazy expiration: không có scheduler chạy mỗi giây. Trạng thái được reconcile khi user/admin đi vào luồng payment tương ứng.

## 6. Danh sách hóa đơn

### Luồng

```text
GET action=invoices
→ InvoicesServlet
→ require role User
→ InvoiceService.listPaid(accountID)
→ InvoiceDAO.findPaidByAccount(accountID)
→ request invoiceList
→ invoice/list.jsp
```

Chỉ invoice `Paid` thuộc account hiện tại được trả về, sort theo `PaidAt` và `InvoiceID` giảm dần.

## 7. Chi tiết hóa đơn

### Luồng

```text
GET action=invoiceDetail&bookingId=<id>
→ InvoiceDetailServlet
→ require User hoặc Admin
→ InvoiceService.getPaidDetail(bookingID, accountID session)
→ InvoiceDAO.findPaidByBookingAndAccount()
→ BookingDAO.findServicesByBookingIDAndAccountID()
→ invoice/detail.jsp
```

Chỉ invoice Paid và đúng ownership mới xem được. Admin cũng chỉ xem được invoice gắn với chính account admin đó; điều này phù hợp với booking vãng lai hiện tại vì booking tại quầy dùng account admin đang đăng nhập.

Invoice detail JOIN các bảng:

```text
Invoices
→ Bookings
→ Accounts + Roles
→ TimeSlots
→ Courts
```

Service lines lấy từ `BookingServices JOIN Services`.

## 8. Tính membership

Membership được trình bày ở profile nhưng nguồn dữ liệu là invoice Paid.

```text
ProfileServlet
→ MembershipService.getSummary(accountID, today)
→ InvoiceDAO.sumPaidByAccountAndPeriod(previousMonth)
→ InvoiceDAO.sumPaidByAccountAndPeriod(currentMonth)
→ MembershipSummary(tier, currentMonthSpent)
→ profile.jsp
```

SQL chỉ SUM `Invoices.FinalAmount` với:

- đúng `Bookings.AccountID`;
- `PaymentStatus=Paid`;
- `PaidAt >= fromInclusive`;
- `PaidAt < toExclusive`.

Dùng khoảng `[from, to)` tránh trùng dữ liệu ở ranh giới hai tháng.

## 9. Vì sao payment cần transaction?

Một hành động payment có thể sửa ba bảng:

- `Invoices`;
- `Bookings`;
- `TimeSlots`.

Nếu invoice đã Cancelled nhưng booking vẫn Confirmed, hoặc booking Cancelled nhưng slot vẫn Booked, dữ liệu sẽ mâu thuẫn. Transaction đảm bảo hoặc toàn bộ thay đổi commit, hoặc rollback toàn bộ.

## 10. Giới hạn hiện tại

- QR là ảnh/demo; nút `pay` tự đánh dấu Paid, không có callback ngân hàng.
- Không có chữ ký webhook, transaction ID từ provider hay đối soát ngoài hệ thống.
- Password và DB credential đang ở mức demo.
- Timeout và trạng thái tự động là lazy reconciliation, không phải background job.

Khi giáo viên hỏi “có thanh toán thật không?”, câu trả lời đúng là: **không; project mô phỏng lifecycle thanh toán và tính nhất quán database, chưa tích hợp payment gateway**.

