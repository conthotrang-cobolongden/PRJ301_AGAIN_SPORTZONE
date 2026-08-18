# Phase 2 — Auth & phân quyền (A1 A2 U1 U2 U3)

**Trạng thái hiện hành (18/08):** đặc tả đã chốt; code P2 từng smoke test 6/6 ngày 17/08 nhưng đã mất khỏi working copy mới và phải dựng lại sau P1. Xem [`../rules/new-plan-0954.md`](../rules/new-plan-0954.md).
**Ngày soạn:** 2026-08-17  
**Phụ thuộc hiện hành:** P1 đã dựng lại và verify trên `feature/foundation`; merge P1 vào `dev` trước khi tạo `feature/auth`.
**Chưa làm trong phase này:** đổi mật khẩu, sửa profile (để P4); chặn đặt sân của Frozen (để P5); chặn báo chuyển khoản của Frozen (để P6).

---

## 1. Session & lưu trạng thái

### 1.1 Ba key session duy nhất

| Key | Kiểu | Nội dung |
|-----|------|----------|
| `account` | `com.sportzone.model.Account` | Tài khoản đăng nhập, **đã set `passwordHash = null` trước khi lưu** |
| `roleName` | `String` | `"Admin"` hoặc `"User"` |
| `frozen` | `Boolean` | `true` nếu `Accounts.Status = 'Frozen'` |

- `header.jsp` hiện tại đã đọc đúng `sessionScope.account` và `sessionScope.roleName` — giữ nguyên, **không chế thêm key khác**.
- **Không lưu mật khẩu hay hash dưới bất kỳ dạng nào trong session.** Lý do: session có thể bị serialize ra disk / dump log; hash lộ là tấn công pass-the-hash được ngay.
- `Account` cần thêm trường `roleName` (không phải cột DB, lấy từ JOIN `Roles`) để DAO trả về object đủ thông tin.

### 1.2 Vòng đời session

- Timeout 30 phút — đã khai báo trong `web.xml`, không đổi.
- Logout: `session.invalidate()` rồi redirect `/home`. Không logout bằng cách xóa từng attribute.
- CSRF: token nằm trong session qua `CsrfUtil.ensureToken(session)` (đã có từ P1). **Mọi POST của P2 phải check `CsrfUtil.isValid(request)` trước khi xử lý**; sai token → reject 403, không xử lý tiếp.

---

## 2. Redirect sau login

### 2.1 Quay lại trang định vào

- `AuthFilter` khi chặn request chưa đăng nhập: lưu URI + query string vào session key `redirectAfterLogin`, rồi redirect `/login`.
- Chỉ lưu với **GET**. Không lưu POST (không replay được form), không lưu URL ngoài context path.
- Login thành công: nếu `redirectAfterLogin` tồn tại thì redirect về đó và **xóa key ngay**; nếu không thì về theo role (mục 2.2).

### 2.2 Theo role

| Role | Sau login |
|------|-----------|
| User | `/home` |
| Admin | **tạm `/home`** — `/admin` chưa tồn tại đến P8. Khi làm P8 đổi đúng 1 dòng trong `LoginServlet` thành `/admin`. Quyết định này chốt ở đây, không tranh cãi lại. |

### 2.3 Đăng ký

- Đăng ký thành công: tự đăng nhập luôn (set 3 key session như login) rồi về `/home`. Không bắt user đăng nhập lại lần nữa.

---

## 3. Xử lý tài khoản Frozen

- Frozen đăng nhập **được** (đúng spec: xem hồ sơ / hóa đơn cũ).
- Ngay khi login, set `frozen = true` trên session và hiển thị thông báo: *"Tài khoản đang bị khóa — chỉ xem được hồ sơ và hóa đơn cũ, không thể đặt sân."*
- **P2 không chặn gì thêm.** Trách nhiệm chặn nằm ở phase sau:
  - P5: đọc cờ `frozen` → chặn tạo booking.
  - P6: đọc cờ `frozen` → chặn gửi "Tôi đã chuyển khoản".
- Không được chặn Frozen vào `/profile` hay `/invoices` — đó là quyền còn lại của họ.

---

## 4. Validate & bảo mật đăng ký

### 4.1 Bảng rule

| Field | Rule server | Giới hạn cột schema |
|-------|-------------|---------------------|
| FullName | Bắt buộc, không rỗng sau trim | `NVARCHAR(100)` |
| Email | Bắt buộc + regex `Validation.isEmail` + unique | `NVARCHAR(100)` |
| Phone | Bắt buộc + regex VN `(0\|+84)(3\|5\|7\|8\|9)` + 8 chữ số sau + unique | `NVARCHAR(20)` |
| Password | Bắt buộc, ≥ 8 ký tự, ≤ 64 ký tự | hash SHA-256 = 64 char |
| Confirm | Bắt buộc, khớp Password | — |

- Validate **server là bắt buộc và chạy trước**. JS client chỉ bổ trợ theo U1 (UX), không thay thế server.
- Đăng ký luôn tạo `RoleID = 2` (User), `Status = 'Active'`. Không cho chọn role từ form.

### 4.2 Chống lộ thông tin

- Login sai (sai email **hoặc** sai mật khẩu): **một** thông báo chung *"Email hoặc mật khẩu không đúng"*. Không lộ email có tồn tại hay không.
- Trùng email/phone khi đăng ký: được phép báo lỗi đúng field (trang đăng ký là public, không lộ dữ liệu nhạy cảm).
- Không echo lại password khi render lại form sau lỗi (chỉ giữ fullName, email, phone).

### 4.3 Chống trùng khi race condition

- Pre-check trong `AccountDAO` (findByEmail / findByPhone) để báo lỗi đúng field cho UX.
- **Kèm** catch `SQLException` có SQLState `23000` (unique violation) ở `RegisterServlet` làm chốt chặn cuối — pre-check là check-then-act, không an toàn khi 2 request chạy song song.

---

## 5. Quy tắc chất lượng code (quán triệt trước khi viết)

### 5.1 Cấu trúc

- Method nhỏ, một trách nhiệm. Servlet không viết SQL — SQL chỉ nằm trong DAO.
- Code lặp lại từ 2 lần trở lên phải tách helper:
  - DAO: `mapAccount(ResultSet)` dùng chung cho mọi query.
  - Servlet: helper đọc param + trim (dùng lại `Validation.trimToNull`).
  - Forward view: luôn qua hằng số trong `Views`, không hard-code đường dẫn JSP.
- Code tường minh: tên biến / method đọc hiểu được không cần comment; không dead code; không import thừa.

### 5.2 Comment

Comment **chỉ** ở chỗ xử lý rắc rối và phải nói rõ ràng buộc, không giải thích hiển nhiên. Bốn chỗ bắt buộc có comment trong P2:

1. Catch unique violation `23000` ở `RegisterServlet` — vì sao pre-check chưa đủ.
2. Set `passwordHash = null` trước khi lưu session — vì sao.
3. Thứ tự filter `Encoding → Auth → Role` trong `web.xml` — vì sao phải đúng thứ tự này.
4. Logic lưu / xóa `redirectAfterLogin` — vì sao chỉ lưu GET.

---

## 6. File mới / file sửa

### File mới

| File | Vai trò |
|------|---------|
| `dao/AccountDAO.java` | `findByEmail` (JOIN Roles lấy roleName), `findByPhone`, `insert`, `updateLastLogin` |
| `service/AuthService.java` | `login` (hash + so bằng `PasswordUtil.matches`), `register` (validate, check trùng, insert) |
| `controller/LoginServlet.java` | GET hiện form, POST xử lý login |
| `controller/RegisterServlet.java` | GET hiện form, POST xử lý đăng ký |
| `controller/LogoutServlet.java` | GET: invalidate session, redirect `/home` |
| `filter/AuthFilter.java` | Chặn URL cần đăng nhập → redirect `/login` |
| `filter/RoleFilter.java` | `/admin/*` chỉ cho Admin, sai role → 403 |
| `views/auth/login.jsp` | Form login + CSRF + thông báo lỗi |
| `views/auth/register.jsp` | Form register + CSRF + lỗi field + JS validate |

### File sửa

| File | Sửa gì |
|------|--------|
| `model/Account.java` | Thêm trường `roleName` + getter/setter |
| `util/Views.java` | Thêm hằng số `LOGIN`, `REGISTER` |
| `web/WEB-INF/web.xml` | Khai báo 3 servlet (`/login`, `/register`, `/logout`) + 2 filter. Thứ tự filter: Encoding → Auth → Role. AuthFilter khai báo sẵn pattern các phase sau: `/profile`, `/password`, `/booking/*`, `/bookings`, `/invoices`, `/invoice/*`, `/admin/*` |
| `views/layout/header.jsp` | Ẩn link Dashboard của Admin tới P8 (tránh link chết) |

---

## 7. Definition of Done

1. 4 account seed login đúng: `admin@sportzone.local` / `Admin@123`, `user1` + `user2` / `User@123`, `user3` Frozen login được kèm thông báo khóa.
2. Sai mật khẩu hoặc sai email đều ra **cùng một** thông báo chung.
3. Đăng ký mới thành công; trùng email / phone báo lỗi đúng field; sai định dạng bị chặn cả server lẫn client.
4. User gõ `/admin` nhận 403; chưa đăng nhập gõ trang cần login bị đẩy về `/login`, sau khi login được trả về đúng trang định vào.
5. Logout xóa session, vào lại trang cần login bị chặn.
6. POST không có hoặc sai CSRF token bị từ chối.

---

## 8. Việc cố ý không làm trong P2

- Đổi mật khẩu, sửa profile (P4).
- Chặn Frozen đặt sân / báo chuyển khoản (P5, P6).
- Trang `/admin` dashboard (P8).
- Remember-me, reset password qua mail, khóa brute-force (ngoài scope assignment).
