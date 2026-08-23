# Phase 6 Simulated Payment Design

**Ngày:** 2026-08-23  
**Trạng thái:** Đã được người dùng chốt trong hội thoại  
**Phạm vi:** Thanh toán chuyển khoản giả lập và hóa đơn phía User; không tích hợp cổng thanh toán.

## Contract

- User mở thanh toán từ booking thuộc chính tài khoản đang đăng nhập.
- Hệ thống tạo hoặc dùng lại tối đa một invoice của booking, ban đầu `Unpaid`.
- Trang thanh toán hiển thị QR giả lập, số tiền và đồng hồ 15 phút tính từ `InvoiceDate`.
- `Thanh toán` chạy cùng transaction: invoice sang `Paid`, đặt `PaymentMethod = BankTransfer`, lưu `PaidAt` và booking `Pending` sang `Confirmed`.
- `Hủy thanh toán` hoặc hết 15 phút chạy cùng transaction: invoice và booking sang `Cancelled`, slot trở lại `Available`.
- Backend kiểm tra thời hạn trong mọi thao tác; JavaScript chỉ đếm ngược và gửi yêu cầu hủy khi trang đang mở.
- Chỉ invoice `Paid` xuất hiện trong danh sách và được mở ở trang chi tiết. Invoice khác trả 404 ở route chi tiết.
- User không thể hủy sau khi invoice đã `Paid` và không được cập nhật booking/invoice của tài khoản khác.
- Booking đã `Confirmed` giữ nguyên; thanh toán không thay đổi trạng thái slot `Booked`.
- Khi tải lại phiên thanh toán `Paid` cũ, hệ thống reconcile booking còn `Pending` sang `Confirmed`.

## Kiến trúc

- `MainController` chỉ dispatch ba action: `invoicePayment`, `invoices`, `invoiceDetail`.
- `InvoicePaymentServlet` xử lý GET màn hình QR và POST `operation=pay|cancel`, có CSRF.
- `InvoicesServlet` và `InvoiceDetailServlet` chỉ đọc invoice `Paid` theo ownership.
- `InvoiceService` giữ rule 15 phút và orchestration; `InvoiceDAO` dùng SQL có điều kiện ownership/status để chống sửa ID.
- Không đổi schema: deadline được suy ra bằng `InvoiceDate + 15 phút`.

## Hoàn tất khi

- QR/countdown và hai nút hoạt động theo contract.
- Booking chưa có invoice có thể bắt đầu phiên thanh toán.
- Invoice Paid xem được; Unpaid/Cancelled không xem được trong danh sách/chi tiết.
- Không còn route Invoice trực tiếp; mọi request Phase 6 đi qua MainController.
