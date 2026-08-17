# Admin & User Main Features  
# Chức năng chính của Admin và User

**System / Hệ thống:** Sports Complex Management (Tennis + Pickleball)  
**Roles / Vai trò:** Admin, User  
**Courts / Sân:** 5 Tennis + 5 Pickleball (fixed)

---

## 1. Admin Features / Chức năng Admin

### 1.1 Authentication & Session / Xác thực & phiên

| ID | English | Tiếng Việt |
|----|---------|------------|
| A1 | Login | Đăng nhập |
| A2 | Logout | Đăng xuất |
| A3 | View admin dashboard (overview: bookings, court status, quick revenue) | Xem dashboard admin (tổng quan: booking, trạng thái sân, doanh thu nhanh) |

### 1.2 User Management / Quản lý người dùng

| ID | English | Tiếng Việt |
|----|---------|------------|
| A4 | View user list (search / filter) | Xem danh sách user (tìm kiếm / lọc) |
| A5 | View user detail (profile + booking history) | Xem chi tiết user (hồ sơ + lịch sử đặt sân) |
| A6 | Update user status (Active / Frozen) | Cập nhật trạng thái user (Active / Frozen) |
| A7 | Update user information (name, phone, email) | Cập nhật thông tin user (họ tên, SĐT, email) |

### 1.3 Court Management / Quản lý sân

| ID | English | Tiếng Việt |
|----|---------|------------|
| A8 | View court list (5 Tennis + 5 Pickleball) | Xem danh sách sân (5 Tennis + 5 Pickleball) |
| A9 | View / update court details (name, location, price per hour, description, image) | Xem / cập nhật chi tiết sân (tên, vị trí, giá/giờ, mô tả, ảnh) |
| A10 | Change court status (Available / Maintenance / Unavailable) | Đổi trạng thái sân (Available / Maintenance / Unavailable) |

> Court types and court count are fixed (hardcoded seed). No create/delete court type.  
> Loại sân và số lượng sân cố định (seed). Không tạo/xóa loại sân.

### 1.4 Time Slot Management / Quản lý khung giờ

| ID | English | Tiếng Việt |
|----|---------|------------|
| A11 | View time slots by date / court | Xem khung giờ theo ngày / sân |
| A12 | Create time slot | Tạo khung giờ |
| A13 | Update time slot | Cập nhật khung giờ |
| A14 | Delete / close time slot | Xóa / đóng khung giờ |

### 1.5 Service Management / Quản lý dịch vụ

| ID | English | Tiếng Việt |
|----|---------|------------|
| A15 | View service list | Xem danh sách dịch vụ |
| A16 | Create service | Thêm dịch vụ |
| A17 | Update service (name, price, duration, active) | Cập nhật dịch vụ (tên, giá, thời lượng, trạng thái) |
| A18 | Delete / deactivate service | Xóa / ẩn dịch vụ |

### 1.6 Booking Management / Quản lý đặt sân

| ID | English | Tiếng Việt |
|----|---------|------------|
| A19 | View all bookings (filter by date, court, status, user) | Xem tất cả booking (lọc theo ngày, sân, trạng thái, user) |
| A20 | View booking detail | Xem chi tiết booking |
| A21 | Confirm booking (Pending → Confirmed) | Xác nhận booking (Pending → Confirmed) |
| A22 | Complete booking (→ Completed) | Hoàn thành booking (→ Completed) |
| A23 | Cancel booking (→ Cancelled) | Hủy booking (→ Cancelled) |
| A24 | Mark no-show (→ NoShow) | Đánh dấu không đến (→ NoShow) |

### 1.7 Invoice & Payment Management / Quản lý hóa đơn & thanh toán

| ID | English | Tiếng Việt |
|----|---------|------------|
| A25 | View invoice list | Xem danh sách hóa đơn |
| A26 | View invoice detail (linked booking, amounts, payment status) | Xem chi tiết hóa đơn (booking liên kết, số tiền, trạng thái thanh toán) |
| A27 | Create / generate invoice for a booking that has none (at most one invoice per booking) | Tạo / sinh hóa đơn cho booking **chưa có HĐ** (mỗi booking tối đa 1 hóa đơn) |
| A28 | Update payment status (Unpaid → Paid / Cancelled) | Cập nhật trạng thái thanh toán (Unpaid → Paid / Cancelled) |
| A29 | Record payment method (Cash / BankTransfer) | Ghi nhận phương thức thanh toán (Tiền mặt / Chuyển khoản) |
| A30 | Cancel invoice (when booking is cancelled) | Hủy hóa đơn (khi hủy booking — chỉ khi booking đã có HĐ) |
| A31 | Print / export invoice view | In / xem bản in hóa đơn |

> **Bookings : Invoices = 1 : 0..1.** Tạo booking (U10) **không** sinh hóa đơn. Hóa đơn tạo sau bằng A27 khi bắt đầu thu.  
> `Unpaid` nằm trên `Invoices.PaymentStatus`, không nằm trên `Bookings.Status`.  
> Hết hạn thanh toán: đã có HĐ → giữ **Unpaid**; chưa có HĐ → không có bản ghi Unpaid, xử lý trên Booking (`Cancelled`).  
> A27 không tạo hóa đơn thứ hai cho cùng booking (`Invoices.BookingID` UNIQUE).

### 1.8 Reports / Báo cáo

| ID | English | Tiếng Việt |
|----|---------|------------|
| A32 | View revenue report (by day / week / month) | Xem báo cáo doanh thu (theo ngày / tuần / tháng) |
| A33 | View booking statistics (by court type / status) | Xem thống kê booking (theo loại sân / trạng thái) |

---

## 2. User Features / Chức năng User

### 2.1 Authentication & Account / Xác thực & tài khoản

| ID | English | Tiếng Việt |
|----|---------|------------|
| U1 | Register account (server-side + client-side validation) | Đăng ký tài khoản (validate phía server + client) |
| U2 | Login | Đăng nhập |
| U3 | Logout | Đăng xuất |
| U4 | View own profile | Xem hồ sơ cá nhân |
| U5 | Update own profile (name, phone, email) | Cập nhật hồ sơ cá nhân (họ tên, SĐT, email) |
| U6 | Change password (hashed; CSRF-protected form) | Đổi mật khẩu (đã mã hoá; form có CSRF) |

### 2.2 Browse Courts & Schedule / Xem sân & lịch

| ID | English | Tiếng Việt |
|----|---------|------------|
| U7 | View court list (filter Tennis / Pickleball) | Xem danh sách sân (lọc Tennis / Pickleball) |
| U8 | View court detail (price, location, status, image) | Xem chi tiết sân (giá, vị trí, trạng thái, ảnh) |
| U9 | View available time slots by date | Xem khung giờ trống theo ngày |

### 2.3 Booking / Đặt sân

| ID | English | Tiếng Việt |
|----|---------|------------|
| U10 | Create booking (court + date + time slot + service if any). Does **not** create an invoice | Tạo booking (sân + ngày + khung giờ + dịch vụ nếu có). **Không** tạo hóa đơn |
| U11 | View my bookings (upcoming / history) | Xem booking của tôi (sắp tới / lịch sử) |
| U12 | View my booking detail | Xem chi tiết booking của tôi |
| U13 | Cancel my booking (within allowed policy) | Hủy booking của tôi (trong thời hạn cho phép) |

> User cannot confirm / complete / no-show bookings — Admin only.  
> User không xác nhận / hoàn thành / no-show — chỉ Admin.

### 2.4 Invoice & Payment / Hóa đơn & thanh toán

| ID | English | Tiếng Việt |
|----|---------|------------|
| U14 | View my invoices | Xem hóa đơn của tôi (booking chưa có HĐ thì không có dòng) |
| U15 | View invoice detail (amount, payment status, booking info) | Xem chi tiết hóa đơn (số tiền, trạng thái thanh toán, thông tin booking) |
| U16 | View payment status of a booking (from its invoice if any: Unpaid / Paid / Cancelled; else “no invoice”) | Xem trạng thái thanh toán của booking (lấy từ hóa đơn nếu có: Unpaid / Paid / Cancelled; không có HĐ → “chưa có hóa đơn”) |
| U17 | Confirm offline payment notice (optional: mark “I have transferred”) — may cause Admin to generate invoice (A27) if none exists; Admin still finalizes Paid | Gửi xác nhận đã thanh toán offline (tuỳ chọn: “Tôi đã chuyển khoản”) — nếu chưa có HĐ thì Admin lập HĐ (A27); Admin mới chốt Paid |
| U18 | View payment method recorded on invoice (Cash / BankTransfer) | Xem phương thức thanh toán ghi trên hóa đơn (Tiền mặt / Chuyển khoản) |

> No online payment gateway (PayOS, etc.). Payment is Cash / Bank Transfer, finalized by Admin.  
> Không cổng thanh toán online. Thanh toán tiền mặt / chuyển khoản, Admin chốt trạng thái Paid.  
> Booking chưa có hóa đơn thì chưa có `PaymentStatus`. Hết hạn mà đã có HĐ thì HĐ giữ **Unpaid**.

### 2.5 General / Chung

| ID | English | Tiếng Việt |
|----|---------|------------|
| U19 | View home / intro page (branding, court types) | Xem trang chủ / giới thiệu (thương hiệu, loại sân) |

---

## 3. Context Diagram Flows (summary)  
## Tóm tắt luồng Context Diagram

### Admin ↔ System

| # | English | Tiếng Việt |
|---|---------|------------|
| 1 | Login / Logout | Đăng nhập / Đăng xuất |
| 2 | Manage users | Quản lý người dùng |
| 3 | Manage courts | Quản lý sân |
| 4 | Manage time slots | Quản lý khung giờ |
| 5 | Manage services | Quản lý dịch vụ |
| 6 | Manage bookings | Quản lý đặt sân |
| 7 | Manage invoices & payments | Quản lý hóa đơn & thanh toán |
| 8 | View revenue & statistics | Xem doanh thu & thống kê |

### User ↔ System

| # | English | Tiếng Việt |
|---|---------|------------|
| 1 | Register / Login / Logout | Đăng ký / Đăng nhập / Đăng xuất |
| 2 | Manage own profile | Quản lý hồ sơ cá nhân |
| 3 | Browse courts & available slots | Xem sân & khung giờ trống |
| 4 | Create booking | Tạo đặt sân |
| 5 | View / cancel own bookings | Xem / hủy booking của mình |
| 6 | View own invoices & payment status | Xem hóa đơn & trạng thái thanh toán của mình |

---

## 4. Invoice & Payment Status (shared)  
## Trạng thái hóa đơn & thanh toán (dùng chung)

**Cardinality:** một booking có **0 hoặc 1** hóa đơn. Chi tiết ERD: [`logic-erd.md`](logic-erd.md).

| Field | Values (EN) | Giá trị (VI) | Nằm ở |
|-------|-------------|--------------|--------|
| Payment status | Unpaid, Paid, Cancelled | Chưa thanh toán, Đã thanh toán, Đã hủy | `Invoices.PaymentStatus` |
| Payment method | Cash, BankTransfer | Tiền mặt, Chuyển khoản | `Invoices.PaymentMethod` (`NULL` nếu Unpaid) |
| Booking status | Pending, Confirmed, Completed, Cancelled, NoShow | Chờ xử lý, Đã xác nhận, Hoàn thành, Đã hủy, Không đến | `Bookings.Status` |

Luồng hóa đơn:

1. U10 tạo Booking (`Pending`) — **chưa** có Invoice.  
2. A27 tạo Invoice khi bắt đầu thu — mặc định **Unpaid**.  
3. Hết hạn chưa trả + đã có HĐ → giữ **Unpaid**. Chưa có HĐ → hủy Booking, không có dòng Unpaid.  
4. A28 + A29: Admin chốt **Paid** + method.  
5. A23/U13 hủy booking: nếu có HĐ thì A30 set invoice **Cancelled**.

---

*Document for PRJ301 individual assignment — Sports Complex (Tennis & Pickleball).*  
*Tài liệu phục vụ assignment cá nhân PRJ301 — Tổ hợp sân Tennis & Pickleball.*
