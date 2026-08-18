# QUY CHUẨN TỔ CHỨC VÀ VIẾT CODE — JSP/Servlet MVC2

> Áp dụng cho các project Java Web theo pattern **MVC2** (JSP + Servlet + JDBC + JSTL/EL).
> Đây là bản quy chuẩn rút ra từ project mẫu `WebApplication12_CRUD_Search_EL_JSTL`, đã dọn dẹp các điểm chưa tốt.

---

## 1. Tổng quan

| Mục | Quy định |
|---|---|
| Ngôn ngữ | Java 8 |
| Nền tảng | Servlet API (`javax.servlet`), chạy trên Tomcat 9 |
| View | JSP + JSTL 1.2 + EL, **không dùng scriptlet cho logic** |
| Database | SQL Server, truy cập bằng JDBC thuần (PreparedStatement) |
| Pattern | MVC2 với Front Controller (`MainController`) |
| Build | NetBeans Ant project (không dùng Maven) |

**Nguyên tắc phân tầng:**

```
JSP (View)  →  Controller (Servlet)  →  DAO (Model)  →  Database
     ↑                                        |
     └──── request attribute / session ───────┘
```

- JSP **không bao giờ** gọi DAO, không viết logic Java.
- Controller **không bao giờ** viết SQL.
- DAO **không bao giờ** đụng tới request/response.

---

## 2. Cấu trúc project

```
ProjectName/
├── src/java/
│   ├── controller/        # Servlet xử lý request (MainController, SearchProductController...)
│   ├── dto/               # DTO của từng entity (ProductDTO, UserDTO)
│   ├── dao/               # DAO của từng entity (ProductDAO, UserDAO)
│   ├── utilsdao/          # Bộ khung JDBC dùng chung (BaseDAO, IDAO, SQLConsumer, SQLMapper)
│   ├── utils/             # Tiện ích (DbUtils — kết nối DB)
│   └── filter/            # Servlet Filter (AuthenticationFilter)
├── web/
│   ├── *.jsp              # JSP đặt phẳng ở root, tên chữ thường 1 từ
│   ├── lib/               # data.sql (script tạo DB), sqljdbc4.jar
│   ├── WEB-INF/web.xml    # Khai báo servlet/filter mapping
│   └── META-INF/context.xml
└── build.xml, nbproject/  # Cấu hình build của NetBeans
```

**Trách nhiệm từng package:**

| Package | Chứa gì | Không chứa gì |
|---|---|---|
| `controller` | Servlet đọc param, gọi DAO, set attribute, forward/redirect | SQL, HTML |
| `dto` | DTO — dữ liệu thuần (field + getter/setter) | Logic nghiệp vụ, request/response |
| `dao` | DAO — CRUD với DB | request/response |
| `utilsdao` | `BaseDAO`, `IDAO`, 2 functional interface | DAO cụ thể |
| `utils` | `DbUtils.getConnection()` | Logic nghiệp vụ |
| `filter` | Filter kiểm tra đăng nhập/phân quyền | Logic nghiệp vụ nặng |

---

## 3. Luồng request (MVC2)

### 3.1 Quy tắc vàng

```
Form POST về MainController (kèm hidden input "action")
   → MainController đọc action, forward sang Controller chức năng
      → Controller đọc param → gọi DAO → request.setAttribute(...)
         → forward sang JSP → JSP hiển thị bằng EL + JSTL
```

### 3.2 Forward vs Redirect

| Tình huống | Dùng | Lý do |
|---|---|---|
| Hiển thị kết quả sau khi xử lý (list, lỗi đăng nhập) | `forward` | Giữ được request attribute |
| Sau logout, sau thao tác thay đổi dữ liệu (POST-Redirect-GET) | `sendRedirect` | Tránh resubmit form khi F5 |

### 3.3 Ví dụ form và dispatch

```jsp
<!-- JSP: form luôn POST về MainController, kèm action ẩn -->
<form action="MainController" method="POST">
    <input type="hidden" name="action" value="login"/>
    ...
</form>
```

```java
// MainController: đọc action, forward sang servlet chức năng
String url = "login.jsp"; // trang mặc định
String action = request.getParameter("action");

if (action != null) {
    if (action.equals("login")) {
        url = "LoginController";
    } else if (action.equals("searchProduct")) {
        url = "SearchProductController";
    }
}
request.getRequestDispatcher(url).forward(request, response);
```

> Forward bằng **tên URL pattern** của servlet (`"LoginController"`), không phải đường dẫn file.

---

## 4. Quy ước đặt tên

| Đối tượng | Quy ước | Ví dụ |
|---|---|---|
| Class dữ liệu | PascalCase + hậu tố `DTO` | `ProductDTO`, `UserDTO` |
| Class truy cập DB | PascalCase + hậu tố `DAO` | `ProductDAO`, `UserDAO` |
| Servlet | PascalCase + hậu tố `Controller` | `LoginController`, `SearchProductController` |
| Package | chữ thường, theo tầng | `controller`, `dto`, `dao`, `utilsdao`, `utils`, `filter` |
| Input form | tiền tố `txt` + camelCase | `txtUsername`, `txtPassword`, `txtKeywords` |
| File JSP | chữ thường, 1 từ | `login.jsp`, `search.jsp`, `welcome.jsp` |
| URL pattern servlet | trùng tên class | `/LoginController` |
| Request attribute | camelCase, mô tả nội dung | `productList`, `errorMessage` |
| Session attribute | camelCase | `loggedUser` |
| Cột database | camelCase **trùng tên field Java** | `productID`, `productName`, `createdAt` |

**Quy tắc xuyên suốt:** tên cột DB = tên field Java = tên request attribute = tên trong EL.
Nhờ vậy `rs.getString("productName")` và `${p.productName}` dùng chung 1 tên từ DB tới giao diện.

---

## 5. Cách viết code từng tầng

### 5.1 DTO (Data Transfer Object)

POJO thuần: field private, constructor rỗng + constructor đầy đủ, getter/setter.
Không chứa logic nghiệp vụ, không cần `toString`/`equals`/`hashCode`.

```java
package dto;

public class UserDTO {

    private String userID;
    private String password;
    private String fullName;
    private String roleID;
    private boolean status;

    public UserDTO() {
    }

    public UserDTO(String userID, String password, String fullName, String roleID, boolean status) {
        this.userID = userID;
        this.password = password;
        this.fullName = fullName;
        this.roleID = roleID;
        this.status = status;
    }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    // ... getter/setter cho các field còn lại
    // boolean dùng isXxx():
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
}
```

### 5.2 DAO (Data Access Object)

Quy tắc:
- `extends BaseDAO implements IDAO<XxxDTO, KiểuCủaID>`
- Có field `mapRow` (lambda `SQLMapper`) **dùng chung cho mọi query** trả về DTO đó
- SQL viết dạng chuỗi nối, tên cột đặt trong `[...]` (chuẩn SQL Server)
- Chỉ dùng `PreparedStatement` với tham số `?` — **không nối giá trị vào SQL**
- `remove()` là **soft delete**: `UPDATE SET status = 0`, không DELETE thật
- Tiếng Việt trong DB dùng `setNString`/`getNString`

```java
package dao;

import dto.ProductDTO;
import utilsdao.BaseDAO;
import utilsdao.IDAO;
import utilsdao.SQLMapper;
import java.util.ArrayList;

public class ProductDAO extends BaseDAO implements IDAO<ProductDTO, String> {

    // Hàm map dùng chung cho mọi query trả về ProductDTO
    private final SQLMapper<ProductDTO> mapRow = rs -> {
        ProductDTO product = new ProductDTO();
        product.setProductID(rs.getString("productID"));
        product.setProductName(rs.getString("productName"));
        product.setPrice(rs.getDouble("price"));
        product.setQuantity(rs.getInt("quantity"));
        product.setStatus(rs.getBoolean("status"));
        return product;
    };

    @Override
    public boolean add(ProductDTO product) {
        String sql = "INSERT INTO [Product] ([productID], [productName], [price], [quantity], [status])"
                + " VALUES (?, ?, ?, ?, ?)";

        return executeUpdate(sql, ps -> {
            ps.setString(1, product.getProductID());
            ps.setString(2, product.getProductName());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getQuantity());
            ps.setBoolean(5, product.isStatus());
        });
    }

    @Override
    public boolean remove(ProductDTO product) {
        // Soft delete: chi tat trang thai, khong xoa that
        String sql = "UPDATE [Product] SET [status] = ? WHERE [productID] = ?";

        return executeUpdate(sql, ps -> {
            ps.setBoolean(1, false);
            ps.setString(2, product.getProductID());
        });
    }

    @Override
    public ArrayList<ProductDTO> listAll() {
        String sql = "SELECT [productID], [productName], [price], [quantity], [status]"
                + " FROM [Product] WHERE [status] = ?";

        return executeQuery(sql, ps -> ps.setBoolean(1, true), mapRow);
    }

    @Override
    public ProductDTO searchByID(String id) {
        String sql = "SELECT [productID], [productName], [price], [quantity], [status]"
                + " FROM [Product] WHERE [productID] = ?";

        ArrayList<ProductDTO> result = executeQuery(sql, ps -> ps.setString(1, id), mapRow);
        return result.isEmpty() ? null : result.get(0);
    }

    // Tim kiem theo ten: LIKE voi % nam trong tham so, khong noi vao SQL
    public ArrayList<ProductDTO> searchByName(String name) {
        String sql = "SELECT [productID], [productName], [price], [quantity], [status]"
                + " FROM [Product] WHERE [productName] LIKE ? AND [status] = ?";

        return executeQuery(sql, ps -> {
            ps.setString(1, "%" + name + "%");
            ps.setBoolean(2, true);
        }, mapRow);
    }
}
```

### 5.3 Bộ khung utilsdao (viết 1 lần, dùng lại mọi project)

```java
// IDAO.java — hop dong CRUD chung
public interface IDAO<T, K> {
    boolean add(T t);
    boolean remove(T t);
    boolean update(T t);
    ArrayList<T> listAll();
    T searchByID(K id);
}

// SQLConsumer.java — lambda set tham so cho PreparedStatement
@FunctionalInterface
public interface SQLConsumer {
    void accept(PreparedStatement ps) throws SQLException;
}

// SQLMapper.java — lambda map 1 dong ResultSet thanh DTO
@FunctionalInterface
public interface SQLMapper<T> {
    T map(ResultSet rs) throws SQLException;
}
```

```java
// BaseDAO.java — 2 method template, mo/dong connection moi lan goi
package utilsdao;

public class BaseDAO {

    protected boolean executeUpdate(String sql, SQLConsumer parameterSetter) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DbUtils.getConnection();
            ps = conn.prepareStatement(sql);
            parameterSetter.accept(ps);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, ps, null);
        }
    }

    protected <T> ArrayList<T> executeQuery(String sql, SQLConsumer parameterSetter, SQLMapper<T> mapper) {
        ArrayList<T> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DbUtils.getConnection();
            ps = conn.prepareStatement(sql);
            parameterSetter.accept(ps);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapper.map(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, ps, rs);
        }
        return list;
    }

    private void closeResources(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

### 5.4 Controller (Servlet)

Quy tắc:
- Theo template NetBeans: có `processRequest()`, cả `doGet` lẫn `doPost` gọi nó
- Dòng đầu tiên: `request.setCharacterEncoding("UTF-8")` (trước khi đọc param)
- Đọc param bằng `request.getParameter("txtXxx").trim()`
- DAO khởi tạo trong `init()` (nếu servlet dùng DAO cố định)
- Kết quả trả về JSP qua `request.setAttribute(...)` rồi **forward**
- Comment tiếng Việt không dấu

```java
package controller;

import dao.ProductDAO;
import dto.ProductDTO;

public class SearchProductController extends HttpServlet {

    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Doc tham so tu form
        String txtKeywords = request.getParameter("txtKeywords");

        // Goi DAO xu ly
        ArrayList<ProductDTO> productList;
        if (txtKeywords != null && txtKeywords.trim().length() > 0) {
            productList = productDAO.searchByName(txtKeywords);
        } else {
            productList = productDAO.listAll();
        }

        // Truyen du lieu sang JSP qua request attribute
        request.setAttribute("productList", productList);
        request.setAttribute("txtKeywords", txtKeywords);

        // Chuyen trang
        request.getRequestDispatcher("search.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
```

**Controller đăng nhập — quy tắc cải tiến:** không so sánh password plaintext.
Hash password trước khi so sánh (tối thiểu SHA-256, tốt hơn là BCrypt):

```java
// LoginController — ban cai tien
UserDTO user = userDAO.searchByID(username);
if (user != null && user.isStatus()
        && PasswordUtils.hash(password).equals(user.getPassword())) {
    HttpSession session = request.getSession();
    session.setAttribute("loggedUser", user);
    url = "welcome.jsp";
} else {
    request.setAttribute("errorMessage", "Invalid username or password!");
    url = "login.jsp";
}
request.getRequestDispatcher(url).forward(request, response);
```

### 5.5 Filter

Kiểm tra session trước khi vào trang cần đăng nhập; chưa đăng nhập thì redirect về `login.jsp`.

```java
package filter;

import dto.UserDTO;

public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Lay session hien tai, khong tao moi neu chua co
        HttpSession session = req.getSession(false);
        UserDTO user = (session != null) ? (UserDTO) session.getAttribute("loggedUser") : null;

        // Kiem tra co dang nhap chua
        if (user == null) {
            res.sendRedirect("login.jsp");
            return;
        }
        chain.doFilter(request, response);
    }
}
```

### 5.6 JSP (View)

Quy tắc:
- Đầu file luôn có 2 directive: `page` (UTF-8) + `taglib` JSTL — **thống nhất URI javax**:
  `<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>`
- Không scriptlet, không Java code — chỉ EL `${...}` và JSTL
- Form POST về `MainController` kèm hidden `action`
- Input giữ giá trị cũ sau khi submit: `value="${txtKeywords}"`
- Hiển thị lỗi: `<c:if test="${not empty errorMessage}">`
- Ghép trang (header chung): `<jsp:include page="welcome.jsp"/>`
- Dữ liệu session: `${sessionScope.loggedUser.fullName}`

```jsp
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Search Product</title>
    </head>
    <body>
        <jsp:include page="welcome.jsp"/>
        <hr/>

        <form action="MainController" method="post">
            <input type="hidden" name="action" value="searchProduct"/>
            <input type="text" name="txtKeywords" value="${txtKeywords}"/>
            <input type="submit" value="Search"/>
        </form>

        <table border="1">
            <c:if test="${not empty productList}">
                <thead>
                    <tr>
                        <th>productID</th><th>productName</th><th>price</th><th>quantity</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${productList}">
                        <tr>
                            <td>${p.productID}</td>
                            <td>${p.productName}</td>
                            <td>${p.price}</td>
                            <td>${p.quantity}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </c:if>
            <c:if test="${empty productList}">
                <tr><td colspan="4">No products found!</td></tr>
            </c:if>
        </table>
    </body>
</html>
```

---

## 6. Database

### 6.1 Kết nối — `utils/DbUtils.java`

Tập trung 1 nơi duy nhất, hard-code thông số kết nối:

```java
public class DbUtils {

    private static final String DB_NAME = "PRJ301";
    private static final String DB_USER_NAME = "sa";
    private static final String DB_PASSWORD = "12345";

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String url = "jdbc:sqlserver://localhost:1433;databaseName=" + DB_NAME;
        return DriverManager.getConnection(url, DB_USER_NAME, DB_PASSWORD);
    }
}
```

### 6.2 Script `data.sql`

Đặt tại `web/lib/data.sql`, tạo DB + bảng + dữ liệu mẫu. Quy ước kiểu:

| Loại dữ liệu | Kiểu SQL Server |
|---|---|
| ID, mã | `VARCHAR(50)` |
| Tiếng Việt | `NVARCHAR(...)` |
| Trạng thái (active/inactive) | `BIT` (1 = true, 0 = false), `DEFAULT 1` |
| Tiền | `DECIMAL(18, 2)` |
| Ngày tạo | `DATETIME DEFAULT GETDATE()` |

```sql
CREATE TABLE [Product] (
    [productID]   VARCHAR(50)     NOT NULL,
    [productName] NVARCHAR(255)   NOT NULL,
    [price]       DECIMAL(18, 2)  NOT NULL,
    [quantity]    INT             NOT NULL DEFAULT 0,
    [status]      BIT             NOT NULL DEFAULT 1,
    [createdAt]   DATETIME        NOT NULL DEFAULT GETDATE(),
    CONSTRAINT PK_Product PRIMARY KEY ([productID])
);
```

---

## 7. web.xml

Không dùng `@WebServlet` — khai báo thủ công toàn bộ trong `web/WEB-INF/web.xml`:

1. **Servlet + mapping**: tên servlet trùng tên class, url-pattern = `/TênClass`
2. **Filter + mapping**: map theo `<servlet-name>` (cho controller cần bảo vệ) và `<url-pattern>` (cho từng JSP cần bảo vệ)
3. **session-config**: timeout 30 phút
4. **welcome-file**: trang mặc định (thường là `login.jsp`)

```xml
<servlet>
    <servlet-name>SearchProductController</servlet-name>
    <servlet-class>controller.SearchProductController</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>SearchProductController</servlet-name>
    <url-pattern>/SearchProductController</url-pattern>
</servlet-mapping>

<filter>
    <filter-name>AuthenticationFilter</filter-name>
    <filter-class>filter.AuthenticationFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>AuthenticationFilter</filter-name>
    <url-pattern>/search.jsp</url-pattern>
</filter-mapping>

<session-config>
    <session-timeout>30</session-timeout>
</session-config>
<welcome-file-list>
    <welcome-file>login.jsp</welcome-file>
</welcome-file-list>
```

---

## 8. Quy ước chung

| Quy ước | Chi tiết |
|---|---|
| Encoding | `request.setCharacterEncoding("UTF-8")` đầu mỗi controller; `pageEncoding="UTF-8"` trong JSP |
| Thông báo lỗi | request attribute `errorMessage`, JSP hiển thị trong `<c:if test="${not empty errorMessage}">` |
| Session user | attribute `loggedUser` (lưu nguyên DTO) |
| Logout | `getSession(false)` → `invalidate()` → `sendRedirect("login.jsp")` |
| Trang đã đăng nhập | `login.jsp` tự redirect nếu `${not empty sessionScope.loggedUser}` |
| Comment code | tiếng Việt không dấu, chỉ ghi khi code không tự giải thích được |

---

## 9. Checklist thêm chức năng mới

Ví dụ cần thêm chức năng "Tìm kiếm sản phẩm theo khoảng giá":

1. ☐ **DTO**: thêm field vào `ProductDTO` nếu cần (getter/setter)
2. ☐ **DAO**: viết method mới trong `ProductDAO` — SQL + lambda set param, dùng lại `mapRow`
3. ☐ **Controller**: tạo `XxxController` mới theo template `processRequest`, đọc param `txtXxx`, gọi DAO, `setAttribute`, forward sang JSP
4. ☐ **web.xml**: khai báo `<servlet>` + `<servlet-mapping>` (url-pattern `/XxxController`); thêm `<filter-mapping>` nếu trang cần đăng nhập
5. ☐ **JSP**: tạo trang mới — form POST về `MainController`, hidden `action`, hiển thị bằng EL + `c:forEach`
6. ☐ **MainController**: thêm nhánh `else if (action.equals("tenActionMoi"))` trỏ tới controller mới
7. ☐ **Test**: chạy `data.sql` nếu có thay đổi schema, test cả 2 nhánh có dữ liệu / rỗng

---

## 10. Các lỗi cần tránh (rút từ project mẫu)

| ❌ Không làm | ✅ Làm thay |
|---|---|
| Khai báo taglib `jakarta.tags.core` (Tomcat 9 + JSTL 1.2 không nhận) | Thống nhất `http://java.sun.com/jsp/jstl/core` ở mọi JSP |
| Lưu/so sánh password plaintext | Hash trước khi lưu và so sánh |
| `listAll()` trả cả dòng đã inactive (quên lọc `status`) | Luôn `WHERE [status] = 1` trừ khi cần hiển thị cả bản ghi bị tắt |
| Import trùng (`import utilsdao.IDAO;` 2 lần), import không dùng | Xóa import thừa trước khi nộp |
| Nối giá trị người dùng vào chuỗi SQL | Chỉ dùng tham số `?` của PreparedStatement |
| JSP chứa scriptlet logic | Chuyển logic lên Controller, JSP chỉ EL + JSTL |
| Mỗi servlet tự mở connection theo 1 kiểu riêng | Mọi truy cập DB đi qua `BaseDAO` + `DbUtils` |
