# Phase 6 Simulated Payment Implementation Plan

> **For agentic workers:** Execute inline in one batch; the user requires exactly one consolidated verification run at the end.

**Goal:** Thêm thanh toán QR giả lập 15 phút và chỉ cho User xem hóa đơn Paid của chính mình.

**Architecture:** MainController chỉ định tuyến. Servlet xử lý HTTP/CSRF, InvoiceService giữ rule nghiệp vụ, InvoiceDAO thực thi ownership và chuyển trạng thái có điều kiện trong SQL Server. Deadline dùng `InvoiceDate + 15 phút`, không thêm scheduler hoặc cột database.

**Tech Stack:** Java 8, Servlet 3.1, JSP/JSTL, SQL Server JDBC, NetBeans Ant.

---

### Task 1: Persistence và nghiệp vụ invoice

**Files:**
- Modify: `SportZoneManagement/src/java/dao/InvoiceDAO.java`
- Create: `SportZoneManagement/src/java/service/InvoiceService.java`
- Modify: `SportZoneManagement/src/java/model/Invoice.java`

- [ ] Thêm query tạo/dùng lại invoice theo booking + account, danh sách/chi tiết Paid theo owner.
- [ ] Thêm update nguyên tử: `Unpaid -> Paid` đồng thời `Pending -> Confirmed`; cancel/timeout đồng thời cancel invoice + booking và trả slot `Available`.
- [ ] Expose deadline và kiểu ngày JSTL-safe trên model.

### Task 2: HTTP routing và security

**Files:**
- Create: `SportZoneManagement/src/java/controller/InvoicePaymentServlet.java`
- Create: `SportZoneManagement/src/java/controller/InvoicesServlet.java`
- Create: `SportZoneManagement/src/java/controller/InvoiceDetailServlet.java`
- Modify: `SportZoneManagement/src/java/controller/MainController.java`
- Modify: `SportZoneManagement/src/java/filter/AuthFilter.java`
- Modify: `SportZoneManagement/web/WEB-INF/web.xml`

- [ ] Khai báo named servlets và dispatch action `invoicePayment`, `invoices`, `invoiceDetail`.
- [ ] Bảo vệ ba action bằng AuthFilter; giữ MainController không chứa security/business logic.
- [ ] GET payment bắt đầu/tải phiên; POST pay/cancel kiểm tra CSRF và ownership.

### Task 3: Giao diện tối giản

**Files:**
- Create: `SportZoneManagement/web/WEB-INF/views/invoice/payment.jsp`
- Create: `SportZoneManagement/web/assets/img/payment-qr.svg`
- Modify: `SportZoneManagement/web/WEB-INF/views/invoice/list.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/invoice/detail.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/booking/detail.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/layout/header.jsp`
- Modify: `SportZoneManagement/src/java/utils/Views.java`

- [ ] Hiển thị QR, số tiền, countdown và form POST `pay|cancel`.
- [ ] Khi countdown về 0, submit cancel; backend vẫn là nguồn chân lý cho deadline.
- [ ] Chỉ render danh sách/chi tiết Paid và thêm CTA thanh toán từ booking.

### Task 4: Contract, docs và một lần verify

**Files:**
- Modify: các regression contract hiện có liên quan routing/JSP/filter.
- Modify: `rules/LOG-LAM-VIEC.md`

- [ ] Quét direct Invoice route và `git diff --check`.
- [ ] Chạy đúng một batch: `ant clean compile-test`, Auth/Frozen contracts và regression P1–P6.
- [ ] Ghi kết quả thực tế vào work log; không commit nếu người dùng chưa yêu cầu.
