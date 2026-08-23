# 02. Xác thực, tài khoản và hồ sơ

## 1. Đăng ký tài khoản

### Giao diện

`auth/register.jsp` gửi POST tới:

```text
/MainController?action=register
```

Các input quan trọng: `fullName`, `email`, `phone`, `password`, `confirmPassword`, `csrfToken`.

### Luồng code

```text
register.jsp
→ MainController
→ RegisterServlet.doPost()
→ CsrfUtil.isValid()
→ AuthService.register()
→ AccountDAO.existsByEmail()/existsByPhone()
→ AccountDAO.insert()
→ AccountDAO.findById()
→ SessionUtil.storeLoggedInAccount()
→ redirect home
```

### Controller làm gì?

1. Chặn request nếu CSRF sai.
2. Đọc năm parameter từ form.
3. Gọi `AuthService.register()`.
4. Nếu validation lỗi, giữ lại tên/email/phone và `fieldErrors`, forward lại JSP.
5. Nếu SQLState `23000`, hiểu là unique constraint bị vi phạm do hai request đồng thời hoặc dữ liệu trùng.
6. Nếu thành công, lưu account vào session và redirect home.

### Service kiểm tra gì?

- Họ tên không rỗng, tối đa 100 ký tự.
- Email được trim, chuyển lowercase, đúng định dạng, chưa tồn tại.
- Điện thoại bỏ khoảng trắng/dấu gạch, đúng số Việt Nam, chưa tồn tại.
- Mật khẩu dài 8–64 ký tự.
- Confirm password phải khớp.
- Account mới luôn có `RoleID=2` (`User`) và `Status=Active`.
- Mật khẩu được băm SHA-256 trước khi lưu.

### DAO và database

`AccountDAO.insert()` insert vào `Accounts`, dùng `RETURN_GENERATED_KEYS`, transaction và trả `AccountID`. Sau đó service reload row cùng `Roles` để có `roleName` đầy đủ.

Database còn có unique constraint trên `Accounts.Email` và `Accounts.Phone`. Kiểm tra ở Service cho thông báo thân thiện; constraint là lớp bảo vệ cuối chống race condition.

## 2. Đăng nhập

### Giao diện

`auth/login.jsp` gửi `email`, `password`, `csrfToken` bằng POST.

### Luồng code

```text
login.jsp
→ LoginServlet.doPost()
→ CsrfUtil.isValid()
→ AuthService.login()
→ AccountDAO.findByEmail()
→ PasswordUtil.matches()
→ AccountDAO.updateLastLogin()
→ SessionUtil.storeLoggedInAccount()
→ redirect URL đã lưu hoặc home
```

### Chi tiết quan trọng

- Email được trim và lowercase.
- DAO JOIN `Accounts` với `Roles` để lấy cả `roleName`.
- `PasswordUtil.matches()` băm password nhập vào bằng SHA-256 rồi dùng `MessageDigest.isEqual()` để so sánh.
- Sai email và sai mật khẩu đều trả chung thông báo để tránh lộ account nào tồn tại.
- Login thành công cập nhật `LastLoginAt`.
- `SessionUtil.storeLoggedInAccount()` xóa `passwordHash` khỏi object trước khi lưu session.

### Redirect sau login

Nếu guest truy cập một GET được bảo vệ, `AuthFilter` lưu servlet path + query vào `redirectAfterLogin`. Sau login, `LoginServlet.consumeTarget()` xóa giá trị này khỏi session và redirect về đúng trang ban đầu. Chỉ GET được lưu vì phát lại GET an toàn hơn POST.

Nếu account Frozen, target bị xóa và user bị đưa thẳng tới `accountLocked`.

## 3. Đăng xuất

```text
Link action=logout
→ LogoutServlet.doGet()
→ request.getSession(false)
→ session.invalidate()
→ redirect home
```

Invalidate xóa account, role, frozen, CSRF token và mọi dữ liệu flash/redirect trong session.

## 4. Cách ly tài khoản Frozen

### Luồng

```text
Mọi request
→ FrozenAccountFilter
→ đọc Account trong session
→ nếu Frozen và request không nằm trong whitelist
→ redirect action=accountLocked
→ AccountLockedServlet
→ error/account-locked.jsp
```

Whitelist chỉ gồm:

- `action=accountLocked`;
- `action=logout`;
- static asset dưới `/assets/`.

Nếu account không Frozen mà cố mở `accountLocked`, servlet redirect về home.

## 5. Xem hồ sơ

### Luồng

```text
GET action=profile
→ ProfileServlet.doGet()
→ SessionUtil.getLoggedInAccount()
→ MembershipService.getSummary()
→ InvoiceDAO.sumPaidByAccountAndPeriod() × 2
→ request attributes
→ account/profile.jsp
```

Controller lấy account hiện tại từ session, không nhận `accountId` từ URL nên user không thể đổi ID để xem người khác. Nó tạo:

- `account`: thông tin tài khoản;
- `memberSince`: ngày tham gia đã format;
- `membershipSummary`: hạng và chi tiêu tháng hiện tại;
- `success`: khi redirect có `updated=1`.

## 6. Cập nhật hồ sơ

### Giao diện và input

`account/profile.jsp` POST `fullName`, `email`, `phone`, `csrfToken` tới `action=profile`.

### Luồng

```text
profile.jsp
→ ProfileServlet.doPost()
→ CSRF + account session
→ ProfileService.updateProfile(accountID, ...)
→ AccountDAO.existsByEmailExcludingId()
→ AccountDAO.existsByPhoneExcludingId()
→ AccountDAO.updateProfile()
→ AccountDAO.findById()
→ cập nhật lại account trong session
→ redirect profile&updated=1
```

Service chỉ cho sửa ba field được phép. `accountID`, `role`, `status`, `passwordHash` không lấy từ form nên client không thể tự nâng quyền hoặc tự mở khóa.

Các hàm `exists...ExcludingId` bỏ qua chính account hiện tại, vì user giữ nguyên email/phone của mình không được coi là trùng.

Nếu validation lỗi, controller tạo bản copy account để render lại dữ liệu vừa nhập nhưng vẫn dùng account session gốc cho các metadata như ngày tham gia và membership.

## 7. Đổi mật khẩu

### Luồng

```text
account/password.jsp
→ POST action=password
→ PasswordServlet.doPost()
→ CSRF + account session
→ ProfileService.changePassword()
→ AccountDAO.findById()
→ PasswordUtil.matches(currentPassword)
→ PasswordUtil.sha256Hex(newPassword)
→ AccountDAO.updatePasswordHash()
→ redirect password&changed=1
```

Validation:

- current password không rỗng;
- new password 8–64 ký tự;
- confirm phải khớp;
- current password phải khớp hash đang lưu.

DAO chỉ nhận hash mới, không nhận raw password. Sau redirect, GET hiển thị thông báo thành công.

## 8. Membership hiển thị trong profile

Membership không phải cột của `Accounts`; nó được tính động từ hóa đơn `Paid`.

`MembershipService.getSummary(accountID, referenceDate)` gọi DAO hai lần:

1. Tổng `FinalAmount` đã Paid của tháng trước.
2. Tổng `FinalAmount` đã Paid của tháng hiện tại.

Hạng được tính theo số lớn hơn giữa hai tháng:

| Tổng đủ điều kiện | Hạng |
|---:|---|
| Dưới 1.000.000 | MEMBER |
| Từ 1.000.000 | SILVER |
| Từ 2.000.000 | PLATINUM |
| Từ 5.000.000 | VIP |

Ý nghĩa: hạng có thể được duy trì nhờ chi tiêu tháng trước hoặc nâng ngay nhờ chi tiêu tháng hiện tại. `monthlySpent` hiển thị vẫn là chi tiêu tháng hiện tại.

## 9. Câu hỏi dễ bị hỏi

**Vì sao validate trùng email hai lần ở Service và database?**  
Service tạo UX tốt, unique constraint chống hai request đồng thời vượt qua bước kiểm tra.

**Vì sao không truyền accountID từ form cập nhật hồ sơ?**  
AccountID phải lấy từ session tin cậy để ngăn sửa tài khoản khác.

**Vì sao xóa passwordHash trước khi lưu session?**  
Giảm nguy cơ lộ hash qua session serialization, debug dump hoặc log.

**Nhược điểm password hiện tại?**  
SHA-256 không salt quá nhanh, chỉ phù hợp demo. Production nên dùng BCrypt/Argon2.

