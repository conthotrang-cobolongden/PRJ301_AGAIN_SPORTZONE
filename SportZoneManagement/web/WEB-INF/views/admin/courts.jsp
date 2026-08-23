<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Quản Lý Sân &amp; TimeSlots Admin — SportZone" scope="request"/>
<c:set var="pageCss" value="admin-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-admin" scope="request"/>
<c:set var="headerVariant" value="none" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<c:set var="adminSection" value="courts" scope="request"/>
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
                <strong>Quản Trị Cơ Sở Vật Chất &amp; Khung Giờ</strong>
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
            <!-- Header -->
            <div class="admin-pagehead" style="margin-bottom:28px;">
                <span class="page-eyebrow" style="font-size:11px; font-weight:800; letter-spacing:.12em; text-transform:uppercase; color:var(--green); margin-bottom:8px; display:block;">
                    QUẢN LÝ CƠ SỞ VẬT CHẤT
                </span>
                <h1 style="font-size:30px; color:var(--ink); margin:0;">
                    Danh Sách 10 Sân <span class="accent-text" style="background:linear-gradient(135deg,#1b5e3b,#caa54b); -webkit-background-clip:text; -webkit-text-fill-color:transparent;">Tiêu Chuẩn Quốc Tế</span>
                </h1>
                <p style="color:var(--muted); font-size:14px; margin:6px 0 0;">
                    Cập nhật đơn giá giờ chơi, điều chỉnh trạng thái hoạt động / bảo trì và thêm slot cho tuần mới.
                </p>
            </div>

            <!-- Alert Notifications -->
            <c:if test="${not empty success}">
                <div class="alert alert-success" role="alert" style="border-radius:14px; margin-bottom:24px;"><c:out value="${success}"/></div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert" style="border-radius:14px; margin-bottom:24px;"><c:out value="${error}"/></div>
            </c:if>

            <!-- Generator Slot Panel -->
            <section class="admin-panel" style="background:#fff; border-radius:var(--radius); border:1px solid var(--line); padding:24px 28px; box-shadow:var(--shadow-sm); margin-bottom:28px;">
                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:18px; border-bottom:1px solid var(--line); padding-bottom:12px;">
                    <div>
                        <h2 style="font-size:17px; font-weight:800; margin:0; color:var(--ink);">Thêm Slot Tuần Mới</h2>
                        <p style="font-size:13px; color:var(--muted); margin:4px 0 0;">Thêm các khung giờ cho toàn bộ sân trong tuần được chọn; slot đã tồn tại sẽ được giữ nguyên.</p>
                    </div>
                </div>

                <form method="post" action="${ctx}/MainController?action=adminCourts" style="display:flex; align-items:flex-end; gap:16px; flex-wrap:wrap;">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="operation" value="generateWeek">
                    
                    <div>
                        <label for="weekStart" style="font-weight:700; font-size:13px; color:var(--ink); display:block; margin-bottom:6px;">Chọn Thứ Hai bắt đầu tuần:</label>
                        <input id="weekStart" name="weekStart" type="date" value="${defaultWeek}" required class="form-control" style="border-radius:10px; font-weight:600; padding:8px 14px; width:220px;">
                    </div>

                    <button class="btn btn-success" type="submit" style="padding:9px 24px; font-weight:800; border-radius:10px; background:linear-gradient(135deg,#1b5e3b,#13472d); border:0; box-shadow:0 4px 14px rgba(27,94,59,.25);">
                        Thêm Slot Tuần Mới →
                    </button>
                </form>
            </section>

            <!-- Court Status Table Panel -->
            <section class="admin-panel" style="background:#fff; border-radius:var(--radius); border:1px solid var(--line); box-shadow:var(--shadow-sm); overflow:hidden;">
                <div style="padding:20px 24px; border-bottom:1px solid var(--line); display:flex; justify-content:space-between; align-items:center;">
                    <div>
                        <h2 style="font-size:17px; font-weight:800; margin:0; color:var(--ink);">Bảng Quản Lý 10 Sân Đấu</h2>
                        <p style="font-size:13px; color:var(--muted); margin:4px 0 0;">Cập nhật giá và trạng thái từng sân (Đảm bảo an toàn không xung đột lịch đặt)</p>
                    </div>
                    <span class="sz-badge sz-badge-gold">${courtList.size()} Sân</span>
                </div>

                <div class="table-responsive">
                    <table class="sz-table" style="width:100%; border-collapse:collapse; margin:0;">
                        <thead>
                            <tr>
                                <th style="padding:14px 20px; font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); background:var(--cream); border-bottom:1px solid var(--line);">Sân Đấu</th>
                                <th style="padding:14px 20px; font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); background:var(--cream); border-bottom:1px solid var(--line);">Phân Môn</th>
                                <th style="padding:14px 20px; font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); background:var(--cream); border-bottom:1px solid var(--line); width:200px;">Giá Thuê / Giờ (VNĐ)</th>
                                <th style="padding:14px 20px; font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); background:var(--cream); border-bottom:1px solid var(--line); width:200px;">Trạng Thái</th>
                                <th style="padding:14px 20px; font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); background:var(--cream); border-bottom:1px solid var(--line); text-align:right;">Thao Tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="court" items="${courtList}">
                                <tr style="border-bottom:1px solid rgba(11,25,17,.05);">
                                    <td style="padding:16px 20px;">
                                        <strong><c:out value="${court.courtName}"/></strong>
                                        <small style="display:block; color:var(--muted); font-size:11px; font-weight:700;">ID #${court.courtId}</small>
                                    </td>
                                    <td style="padding:16px 20px;">
                                        <span class="sz-badge ${court.typeName == 'Tennis' ? 'sz-badge-tennis' : 'sz-badge-pickle'}">
                                            <c:out value="${court.typeName}"/>
                                        </span>
                                    </td>
                                    <td style="padding:16px 20px;">
                                        <input form="courtForm${court.courtId}" class="form-control" name="price" type="number" min="0" step="1000" value="${court.pricePerHour}" required style="font-weight:700; border-radius:8px; padding:6px 12px; font-size:14px;">
                                    </td>
                                    <td style="padding:16px 20px;">
                                        <select form="courtForm${court.courtId}" class="form-select" name="status" style="font-weight:600; border-radius:8px; padding:6px 12px; font-size:14px;">
                                            <option value="Available" ${court.status == 'Available' ? 'selected' : ''}>Available (Sẵn sàng)</option>
                                            <option value="Maintenance" ${court.status == 'Maintenance' ? 'selected' : ''}>Maintenance (Bảo trì)</option>
                                            <option value="Unavailable" ${court.status == 'Unavailable' ? 'selected' : ''}>Unavailable (Tạm đóng)</option>
                                        </select>
                                    </td>
                                    <td style="padding:16px 20px; text-align:right;">
                                        <form id="courtForm${court.courtId}" method="post" action="${ctx}/MainController?action=adminCourts" style="display:inline-block; margin:0;">
                                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="operation" value="updateCourt">
                                            <input type="hidden" name="courtId" value="${court.courtId}">
                                            <button class="btn btn-outline-success" type="submit" style="padding:6px 16px; font-size:12.5px; font-weight:800; border-radius:999px;">
                                                Lưu Thay Đổi
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </section>
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
