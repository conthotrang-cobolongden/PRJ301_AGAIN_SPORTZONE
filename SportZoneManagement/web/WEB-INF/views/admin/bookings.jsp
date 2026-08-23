<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Quản Lý Booking Admin — SportZone" scope="request"/>
<c:set var="pageCss" value="admin-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-admin" scope="request"/>
<c:set var="headerVariant" value="none" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<c:set var="adminSection" value="bookings" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="/WEB-INF/views/layout/admin-sidebar.jsp"/>

<div class="admin-viewport">
    <!-- Topbar Header -->
    <header class="admin-topbar">
        <div class="topbar-left">
            <button class="btn-topbar-toggle" id="topbarToggle" type="button" title="Mở rộng / Thu gọn Sidebar">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="3" y1="12" x2="21" y2="12"/>
                    <line x1="3" y1="6" x2="21" y2="6"/>
                    <line x1="3" y1="18" x2="21" y2="18"/>
                </svg>
            </button>
            <div class="court-live-ticker">
                <strong>Quản Trị Booking Vận Hành</strong>
            </div>
        </div>

        <div class="topbar-right">
            <a href="${ctx}/MainController?action=bookingCreate" class="btn-admin-primary" style="padding:9px 20px; font-size:13px; font-weight:800; border-radius:999px; background:linear-gradient(135deg,#1b5e3b,#13472d); color:#fff; text-decoration:none; box-shadow:0 4px 14px rgba(27,94,59,.25);">
                + Đặt Sân Vãng Lai
            </a>
        </div>
    </header>

    <!-- Main Content -->
    <main class="admin-main-scroll" style="flex:1; overflow-y:auto; padding:32px 36px;">
        <section class="admin-view active">
            <!-- Alert Notifications -->
            <c:if test="${not empty success}">
                <div class="alert alert-success" role="alert" style="border-radius:14px; margin-bottom:24px;"><c:out value="${success}"/></div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert" style="border-radius:14px; margin-bottom:24px;"><c:out value="${error}"/></div>
            </c:if>

            <c:choose>
                <%-- ==================== VIEW 1: BOOKING LIST REGISTRY ==================== --%>
                <c:when test="${empty booking}">
                    <div class="admin-pagehead" style="display:flex; justify-content:space-between; align-items:flex-end; margin-bottom:28px; flex-wrap:wrap; gap:16px;">
                        <div>
                            <span class="page-eyebrow" style="font-size:11px; font-weight:800; letter-spacing:.12em; text-transform:uppercase; color:var(--green); margin-bottom:8px; display:block;">
                                BOOKING OPERATIONS
                            </span>
                            <h1 style="font-size:30px; color:var(--ink); margin:0;">
                                Danh Sách Booking <span class="accent-text" style="background:linear-gradient(135deg,#1b5e3b,#caa54b); -webkit-background-clip:text; -webkit-text-fill-color:transparent;">Hệ Thống</span>
                            </h1>
                            <p style="color:var(--muted); font-size:14px; margin:6px 0 0;">
                                Theo dõi, lọc trạng thái và thực hiện check-in cho khách đến sân thi đấu.
                            </p>
                        </div>
                    </div>

                    <!-- Filter Bar -->
                    <form method="get" action="${ctx}/MainController" style="display:flex; align-items:center; gap:12px; margin-bottom:24px; background:#fff; padding:16px 20px; border-radius:16px; border:1px solid var(--line); box-shadow:var(--shadow-sm); flex-wrap:wrap;">
                        <input type="hidden" name="action" value="adminBookings">
                        <label for="bookingStatus" style="font-weight:700; font-size:13.5px; color:var(--ink);">Lọc theo trạng thái:</label>
                        <select id="bookingStatus" name="status" class="form-select" style="width:auto; min-width:200px; border-radius:10px; font-weight:600; padding:8px 14px;">
                            <option value="all">Tất cả trạng thái</option>
                            <option value="Pending" ${selectedStatus == 'Pending' ? 'selected' : ''}>Pending (Chờ xác nhận)</option>
                            <option value="Confirmed" ${selectedStatus == 'Confirmed' ? 'selected' : ''}>Confirmed (Đã xác nhận)</option>
                            <option value="CheckedIn" ${selectedStatus == 'CheckedIn' ? 'selected' : ''}>CheckedIn (Đã check-in)</option>
                            <option value="Completed" ${selectedStatus == 'Completed' ? 'selected' : ''}>Completed (Hoàn thành)</option>
                            <option value="Cancelled" ${selectedStatus == 'Cancelled' ? 'selected' : ''}>Cancelled (Đã hủy)</option>
                            <option value="NoShow" ${selectedStatus == 'NoShow' ? 'selected' : ''}>NoShow (Không đến)</option>
                        </select>
                        <button class="btn btn-outline-success" type="submit" style="font-weight:800; border-radius:10px; padding:8px 20px;">
                            Áp Dụng Bộ Lọc
                        </button>
                    </form>

                    <!-- Booking Registry Table -->
                    <section class="admin-panel" style="background:#fff; border-radius:var(--radius); border:1px solid var(--line); box-shadow:var(--shadow-sm); overflow:hidden;">
                        <div style="padding:20px 24px; border-bottom:1px solid var(--line); display:flex; justify-content:space-between; align-items:center;">
                            <div>
                                <h2 style="font-size:17px; font-weight:800; margin:0; color:var(--ink);">Danh Sách Đơn Đặt Sân</h2>
                                <p style="font-size:13px; color:var(--muted); margin:4px 0 0;">Cập nhật theo thời gian thực từ cơ sở dữ liệu</p>
                            </div>
                            <span class="sz-badge sz-badge-gold">${bookingList.size()} Đơn</span>
                        </div>

                        <div class="table-responsive">
                            <table class="sz-table" style="width:100%; border-collapse:collapse; margin:0;">
                                <thead>
                                    <tr>
                                        <th style="padding:14px 20px; font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); background:var(--cream); border-bottom:1px solid var(--line);">Mã Đơn</th>
                                        <th style="padding:14px 20px; font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); background:var(--cream); border-bottom:1px solid var(--line);">Khách Hàng</th>
                                        <th style="padding:14px 20px; font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); background:var(--cream); border-bottom:1px solid var(--line);">Sân Đấu</th>
                                        <th style="padding:14px 20px; font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); background:var(--cream); border-bottom:1px solid var(--line);">Ngày &amp; Giờ</th>
                                        <th style="padding:14px 20px; font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); background:var(--cream); border-bottom:1px solid var(--line); text-align:right;">Tổng Tiền</th>
                                        <th style="padding:14px 20px; font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); background:var(--cream); border-bottom:1px solid var(--line); text-align:center;">Trạng Thái</th>
                                        <th style="padding:14px 20px; font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); background:var(--cream); border-bottom:1px solid var(--line); text-align:right;">Thao Tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${bookingList}">
                                        <tr style="border-bottom:1px solid rgba(11,25,17,.05);">
                                            <td style="padding:16px 20px; font-family:'Montserrat',sans-serif; font-weight:800; color:var(--gold);">
                                                #BK-${item.bookingId}
                                            </td>
                                            <td style="padding:16px 20px;">
                                                <strong><c:out value="${item.accountName}"/></strong>
                                            </td>
                                            <td style="padding:16px 20px; font-weight:600;">
                                                <c:out value="${item.courtName}"/>
                                            </td>
                                            <td style="padding:16px 20px; font-size:13.5px;">
                                                <strong><fmt:formatDate value="${item.slotDateValue}" pattern="dd/MM/yyyy"/></strong><br>
                                                <small style="color:var(--muted); font-weight:700;">${item.startTime} – ${item.endTime}</small>
                                            </td>
                                            <td style="padding:16px 20px; text-align:right; font-family:'Montserrat',sans-serif; font-weight:800; color:var(--green);">
                                                <fmt:formatNumber value="${item.totalAmount}" groupingUsed="true"/> đ
                                            </td>
                                            <td style="padding:16px 20px; text-align:center;">
                                                <c:choose>
                                                    <c:when test="${item.status == 'Pending'}"><span class="sz-badge sz-badge-pending">Pending</span></c:when>
                                                    <c:when test="${item.status == 'Confirmed'}"><span class="sz-badge sz-badge-confirmed">Confirmed</span></c:when>
                                                    <c:when test="${item.status == 'CheckedIn'}"><span class="sz-badge sz-badge-active">Checked-in</span></c:when>
                                                    <c:when test="${item.status == 'Completed'}"><span class="sz-badge sz-badge-active">Completed</span></c:when>
                                                    <c:when test="${item.status == 'Cancelled'}"><span class="sz-badge sz-badge-frozen">Cancelled</span></c:when>
                                                    <c:otherwise><span class="sz-badge sz-badge-noshow">NoShow</span></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td style="padding:16px 20px; text-align:right;">
                                                <a class="btn-action" href="${ctx}/MainController?action=adminBookings&amp;id=${item.bookingId}" style="padding:6px 14px; font-size:12px; font-weight:800; border-radius:999px; border:1px solid var(--line); background:#fff; text-decoration:none; color:var(--ink);">
                                                    Xem Chi Tiết →
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty bookingList}">
                                        <tr>
                                            <td colspan="7" style="text-align:center; padding:40px; color:var(--muted);">
                                                Không có booking nào phù hợp với bộ lọc hiện tại.
                                            </td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </section>
                </c:when>

                <%-- ==================== VIEW 2: BOOKING OPERATIONAL DETAIL ==================== --%>
                <c:otherwise>
                    <div style="margin-bottom:24px;">
                        <a href="${ctx}/MainController?action=adminBookings" style="display:inline-flex; align-items:center; gap:6px; font-size:13.5px; font-weight:800; color:var(--green); text-decoration:none; margin-bottom:16px;">
                            ← Quay lại danh sách bookings
                        </a>
                        <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:16px;">
                            <div>
                                <span class="page-eyebrow" style="font-size:11px; font-weight:800; letter-spacing:.12em; text-transform:uppercase; color:var(--gold); display:block; margin-bottom:4px;">
                                    CHI TIẾT BOOKING #${booking.bookingId}
                                </span>
                                <h1 style="font-size:28px; margin:0; color:var(--ink);"><c:out value="${booking.accountName}"/></h1>
                            </div>
                            <span class="sz-badge ${booking.status == 'Completed' || booking.status == 'CheckedIn' ? 'sz-badge-active' : (booking.status == 'Cancelled' ? 'sz-badge-frozen' : 'sz-badge-pending')}" style="font-size:13px; padding:6px 16px;">
                                Trạng thái: ${booking.status}
                            </span>
                        </div>
                    </div>

                    <div style="display:grid; grid-template-columns:1.6fr 1.1fr; gap:28px; align-items:start;">
                        <!-- Left Info Panel -->
                        <section class="admin-panel" style="background:#fff; border-radius:var(--radius); border:1px solid var(--line); padding:28px; box-shadow:var(--shadow-sm);">
                            <h2 style="font-size:18px; font-weight:800; margin-bottom:20px; color:var(--ink); border-bottom:1px solid var(--line); padding-bottom:12px;">
                                Thông Tin Đơn Đặt Sân
                            </h2>

                            <dl style="margin:0; font-size:14px;">
                                <div style="display:flex; justify-content:space-between; padding:12px 0; border-bottom:1px solid rgba(11,25,17,.06);">
                                    <dt style="color:var(--muted); font-weight:600;">Khách hàng</dt>
                                    <dd style="margin:0; font-weight:800; color:var(--ink);">
                                        <c:out value="${booking.accountName}"/>
                                        <c:if test="${booking.accountRoleName == 'Admin'}">
                                            <span class="sz-badge sz-badge-gold ms-1">Khách Vãng Lai</span>
                                        </c:if>
                                    </dd>
                                </div>

                                <div style="display:flex; justify-content:space-between; padding:12px 0; border-bottom:1px solid rgba(11,25,17,.06);">
                                    <dt style="color:var(--muted); font-weight:600;">Sân thi đấu</dt>
                                    <dd style="margin:0; font-weight:800;"><c:out value="${booking.courtName}"/></dd>
                                </div>

                                <div style="display:flex; justify-content:space-between; padding:12px 0; border-bottom:1px solid rgba(11,25,17,.06);">
                                    <dt style="color:var(--muted); font-weight:600;">Buổi chơi</dt>
                                    <dd style="margin:0; font-weight:800;">
                                        <fmt:formatDate value="${booking.slotDateValue}" pattern="dd/MM/yyyy"/> · ${booking.startTime} – ${booking.endTime}
                                    </dd>
                                </div>

                                <div style="display:flex; justify-content:space-between; padding:12px 0; border-bottom:1px solid rgba(11,25,17,.06);">
                                    <dt style="color:var(--muted); font-weight:600;">Tổng tiền</dt>
                                    <dd style="margin:0; font-family:'Montserrat',sans-serif; font-weight:900; font-size:17px; color:var(--green);">
                                        <fmt:formatNumber value="${booking.totalAmount}" groupingUsed="true"/> đ
                                    </dd>
                                </div>

                                <div style="display:flex; justify-content:space-between; padding:12px 0; border-bottom:1px solid rgba(11,25,17,.06);">
                                    <dt style="color:var(--muted); font-weight:600;">Thanh toán</dt>
                                    <dd style="margin:0; font-weight:800;">
                                        <c:choose>
                                            <c:when test="${booking.paymentStatus == 'Paid'}"><span class="sz-badge sz-badge-active">Đã thanh toán</span></c:when>
                                            <c:when test="${booking.paymentStatus == 'Unpaid'}"><span class="sz-badge sz-badge-pending">Chưa thanh toán</span></c:when>
                                            <c:when test="${booking.paymentStatus == 'Cancelled'}"><span class="sz-badge sz-badge-frozen">Đã hủy</span></c:when>
                                            <c:otherwise><span class="sz-badge sz-badge-noshow">Chưa có hóa đơn</span></c:otherwise>
                                        </c:choose>
                                    </dd>
                                </div>

                                <c:if test="${not empty booking.checkInAt}">
                                    <div style="display:flex; justify-content:space-between; padding:12px 0;">
                                        <dt style="color:var(--muted); font-weight:600;">Check-in lúc</dt>
                                        <dd style="margin:0; font-weight:800; color:var(--green);">${booking.checkInAt}</dd>
                                    </div>
                                </c:if>
                            </dl>
                        </section>

                        <!-- Right Action Panel -->
                        <aside class="admin-panel" style="background:#fff; border-radius:var(--radius); border:1px solid var(--line); padding:28px; box-shadow:var(--shadow-sm);">
                            <h2 style="font-size:18px; font-weight:800; margin-bottom:20px; color:var(--ink); border-bottom:1px solid var(--line); padding-bottom:12px;">
                                Thao Tác Nghiệp Vụ
                            </h2>

                            <!-- Check In Action Button -->
                            <c:if test="${canCheckIn}">
                                <form method="post" action="${ctx}/MainController?action=adminBookings" style="margin-bottom:16px;">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="operation" value="checkIn">
                                    <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                    <button class="btn btn-success w-100" type="submit" style="padding:14px; font-weight:800; border-radius:12px; font-size:15px; background:linear-gradient(135deg,#1b5e3b,#13472d); border:0; box-shadow:0 4px 14px rgba(27,94,59,.25);">
                                        Xác Nhận Check-in Khách Đến Sân
                                    </button>
                                </form>
                            </c:if>

                            <c:if test="${!canCheckIn}">
                                <div class="alert alert-warning" style="border-radius:12px; font-size:13px; line-height:1.6; margin-bottom:16px;">
                                    ⚠️ Có thể check-in từ đúng giờ bắt đầu đến trước 30 phút sau giờ bắt đầu.
                                </div>
                            </c:if>

                            <!-- Walk-in Customer Invoicing -->
                            <c:if test="${booking.accountRoleName == 'Admin' && booking.accountId == sessionScope.account.accountID}">
                                <c:choose>
                                    <c:when test="${booking.paymentStatus == 'Paid'}">
                                        <a class="btn btn-outline-success w-100 mb-2" style="border-radius:10px; font-weight:700; padding:10px;" href="${ctx}/MainController?action=invoiceDetail&amp;bookingId=${booking.bookingId}">
                                            Xem Hóa Đơn Điện Tử
                                        </a>
                                    </c:when>
                                    <c:when test="${booking.paymentStatus != 'Cancelled'}">
                                        <a class="btn btn-success w-100 mb-2" style="border-radius:10px; font-weight:700; padding:10px; background:var(--green);" href="${ctx}/MainController?action=invoicePayment&amp;bookingId=${booking.bookingId}">
                                            Thu Tiền / Thanh Toán QR
                                        </a>
                                    </c:when>
                                </c:choose>
                            </c:if>
                        </aside>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </main>
</div>

<script>
    (function () {
        var sidebar = document.getElementById('adminSidebar');
        if (!sidebar) return;
        function toggle() {
            document.body.classList.toggle('sidebar-collapsed');
        }
        var sToggle = document.getElementById('sidebarToggle');
        var tToggle = document.getElementById('topbarToggle');
        if (sToggle) sToggle.addEventListener('click', toggle);
        if (tToggle) tToggle.addEventListener('click', toggle);
    })();
</script>
