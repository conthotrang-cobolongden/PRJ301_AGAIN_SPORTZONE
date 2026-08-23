<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Booking Của Tôi — SportZone" scope="request"/>
<c:set var="pageCss" value="booking-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-booking" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- ==============================================
     USER BOOKING LIST (U11 + U16) — SPORTZONE SYNC
     ============================================== -->
<div class="container view-section active" id="viewList" style="padding-top:40px; padding-bottom:60px;">
    <div class="booking-list-head">
        <div>
            <div class="kicker"><span class="dot"></span> LỊCH SỬ THI ĐẤU</div>
            <h1>Booking <span class="accent-text">Của Tôi</span></h1>
        </div>
        <a class="btn green sm" href="${ctx}/MainController?action=courts">+ Đặt Thêm Sân Mới</a>
    </div>

    <!-- Filter Tabs (Sắp tới / Lịch sử) -->
    <ul class="booking-tabs">
        <li class="nav-item">
            <a class="filter-btn ${activeTab != 'history' ? 'active' : ''}" href="${ctx}/MainController?action=bookings">
                Sắp Tới <span class="sz-count">${upcomingList.size()}</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="filter-btn ${activeTab == 'history' ? 'active' : ''}" href="${ctx}/MainController?action=bookings&amp;tab=history">
                Lịch Sử <span class="sz-count">${historyList.size()}</span>
            </a>
        </li>
    </ul>

    <c:set var="rows" value="${activeTab == 'history' ? historyList : upcomingList}"/>

    <c:choose>
        <c:when test="${not empty rows}">
            <div class="sz-card" style="padding:0; overflow:hidden;">
                <div class="table-responsive">
                    <table class="sz-table sz-table-cards">
                        <thead>
                            <tr>
                                <th>Ngày Chơi</th>
                                <th>Khung Giờ</th>
                                <th>Sân Đấu</th>
                                <th class="mid">Trạng Thái</th>
                                <th class="mid">Thanh Toán</th>
                                <th class="num">Tổng Tiền</th>
                                <th class="sz-td-actions">Thao Tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="b" items="${rows}">
                                <tr class="${b.status == 'Cancelled' || b.status == 'NoShow' ? 'row-muted' : ''}">
                                    <td>
                                        <strong><fmt:formatDate value="${b.slotDateValue}" pattern="dd/MM/yyyy"/></strong>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${b.startTimeValue}" pattern="HH:mm"/> – <fmt:formatDate value="${b.endTimeValue}" pattern="HH:mm"/>
                                    </td>
                                    <td>
                                        <c:out value="${b.courtName}"/>
                                        <span class="sz-badge ${b.typeSlug == 'tennis' ? 'sz-badge-tennis' : 'sz-badge-pickle'} ms-1">
                                            <c:out value="${b.typeName}"/>
                                        </span>
                                    </td>
                                    <td class="mid">
                                        <c:choose>
                                            <c:when test="${b.status == 'Pending'}"><span class="sz-badge sz-badge-pending">Chờ xác nhận</span></c:when>
                                            <c:when test="${b.status == 'Confirmed'}"><span class="sz-badge sz-badge-confirmed">Đã xác nhận</span></c:when>
                                            <c:when test="${b.status == 'Completed'}"><span class="sz-badge sz-badge-completed">Hoàn thành</span></c:when>
                                            <c:when test="${b.status == 'Cancelled'}"><span class="sz-badge sz-badge-cancelled">Đã hủy</span></c:when>
                                            <c:otherwise><span class="sz-badge sz-badge-none">Không đến</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="mid">
                                        <c:choose>
                                            <c:when test="${b.paymentStatus == 'Paid'}"><span class="sz-badge sz-badge-paid">Đã thanh toán</span></c:when>
                                            <c:when test="${b.paymentStatus == 'Unpaid'}"><span class="sz-badge sz-badge-unpaid">Chưa thanh toán</span></c:when>
                                            <c:when test="${b.paymentStatus == 'Cancelled'}"><span class="sz-badge sz-badge-cancelled">Đã hủy</span></c:when>
                                            <c:otherwise><span class="sz-badge sz-badge-none">Chưa có hóa đơn</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="num">
                                        <fmt:formatNumber value="${b.totalAmount}" type="number" groupingUsed="true"/> đ
                                    </td>
                                    <td class="sz-td-actions">
                                        <a class="btn-action" href="${ctx}/MainController?action=bookingDetail&amp;id=${b.bookingId}">Chi tiết →</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-state" style="text-align:center; padding:60px 20px; background:#fff; border-radius:var(--radius); border:1px solid var(--line); box-shadow:var(--shadow-sm);">
                <div style="font-size:36px; margin-bottom:12px;">📅</div>
                <c:choose>
                    <c:when test="${activeTab == 'history'}">
                        <h2 style="font-size:22px; margin-bottom:8px;">Chưa có booking nào trong quá khứ</h2>
                        <p style="color:var(--muted); margin-bottom:20px;">Khi hoàn thành hoặc hủy booking, dữ liệu sẽ tự động lưu trữ tại đây.</p>
                    </c:when>
                    <c:otherwise>
                        <h2 style="font-size:22px; margin-bottom:8px;">Chưa có booking sắp tới</h2>
                        <p style="color:var(--muted); margin-bottom:20px;">Chọn sân và khung giờ trống để lên lịch rèn luyện thi đấu ngay hôm nay.</p>
                        <a class="btn green" href="${ctx}/MainController?action=courts">Khám phá &amp; Đặt sân ngay →</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
