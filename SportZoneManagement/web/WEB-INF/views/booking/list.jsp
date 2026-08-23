<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- U11: Booking của tôi (tab Sắp tới / Lịch sử) + U16: cột thanh toán --%>
<c:set var="pageTitle" value="Booking của tôi — SportZone" scope="request"/>
<c:set var="pageCss" value="booking-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-booking" scope="request"/>
<c:set var="headerVariant" value="standard" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container view-section active" id="viewList">
    <div class="booking-list-head">
        <div>
            <span class="sz-badge sz-badge-completed">LỊCH SỬ THI ĐẤU</span>
            <h1>Booking của tôi</h1>
        </div>
        <a class="btn-action-green rounded-action" href="${ctx}/MainController?action=courts">+ Đặt Thêm Sân Mới</a>
    </div>

    <!-- Tabs: GET ?tab=upcoming|history -->
    <ul class="booking-tabs">
        <li class="nav-item">
            <a class="filter-btn ${activeTab != 'history' ? 'active' : ''}" href="${ctx}/MainController?action=bookings">
                Sắp tới <span class="sz-count">${upcomingList.size()}</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="filter-btn ${activeTab == 'history' ? 'active' : ''}" href="${ctx}/MainController?action=bookings&amp;tab=history">
                Lịch sử <span class="sz-count">${historyList.size()}</span>
            </a>
        </li>
    </ul>

    <!-- DEEPSEEK: bind upcomingList / historyList — mỗi item là view model kèm:
         bookingId, courtName, typeName, slotDate (Date), startTime/endTime (Time),
         status (Booking), paymentStatus (Invoice: Unpaid/Paid/Cancelled, NULL nếu chưa có HĐ),
         totalAmount. activeTab = 'upcoming'|'history'. -->
    <c:set var="rows" value="${activeTab == 'history' ? historyList : upcomingList}"/>

    <c:choose>
        <c:when test="${not empty rows}">
            <div class="sz-card"><div class="table-responsive">
                <table class="sz-table sz-table-cards">
                    <thead>
                        <tr>
                            <th>Ngày chơi</th>
                            <th>Giờ</th>
                            <th>Sân</th>
                            <th class="mid">Trạng thái</th>
                            <th class="mid">Thanh toán</th>
                            <th class="num">Tổng tiền</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="b" items="${rows}">
                            <tr class="${b.status == 'Cancelled' || b.status == 'NoShow' ? 'row-muted' : ''}">
                                <td data-label="Ngày chơi"><fmt:formatDate value="${b.slotDateValue}" pattern="dd/MM/yyyy"/></td>
                                <td data-label="Giờ"><fmt:formatDate value="${b.startTimeValue}" pattern="HH:mm"/>–<fmt:formatDate value="${b.endTimeValue}" pattern="HH:mm"/></td>
                                <td data-label="Sân">
                                    <c:out value="${b.courtName}"/>
                                    <span class="sz-badge ${b.typeSlug == 'tennis' ? 'sz-badge-tennis' : 'sz-badge-pickle'} ms-1"><c:out value="${b.typeName}"/></span>
                                </td>
                                <td class="mid" data-label="Trạng thái">
                                    <c:choose>
                                        <c:when test="${b.status == 'Pending'}"><span class="sz-badge sz-badge-pending">Chờ xác nhận</span></c:when>
                                        <c:when test="${b.status == 'Confirmed'}"><span class="sz-badge sz-badge-confirmed">Đã xác nhận</span></c:when>
                                        <c:when test="${b.status == 'Completed'}"><span class="sz-badge sz-badge-completed">Hoàn thành</span></c:when>
                                        <c:when test="${b.status == 'Cancelled'}"><span class="sz-badge sz-badge-cancelled">Đã hủy</span></c:when>
                                        <c:otherwise><span class="sz-badge sz-badge-noshow">Không đến</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="mid" data-label="Thanh toán">
                                    <%-- U16: chưa có HĐ → "Chưa có hóa đơn", KHÔNG hiển thị Unpaid (rule 3, 4) --%>
                                    <c:choose>
                                        <c:when test="${b.paymentStatus == 'Paid'}"><span class="sz-badge sz-badge-paid">Đã thanh toán</span></c:when>
                                        <c:when test="${b.paymentStatus == 'Unpaid'}"><span class="sz-badge sz-badge-unpaid">Chưa thanh toán</span></c:when>
                                        <c:when test="${b.paymentStatus == 'Cancelled'}"><span class="sz-badge sz-badge-cancelled">Đã hủy</span></c:when>
                                        <c:otherwise><span class="sz-badge sz-badge-none">Chưa có hóa đơn</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="num" data-label="Tổng tiền"><fmt:formatNumber value="${b.totalAmount}" type="number" groupingUsed="true"/> đ</td>
                                <td class="sz-td-actions">
                                    <a class="btn-action" href="${ctx}/MainController?action=bookingDetail&amp;id=${b.bookingId}">Xem đơn</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div></div>
        </c:when>
        <c:otherwise>
            <div class="sz-empty">
                <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                <c:choose>
                    <c:when test="${activeTab == 'history'}">
                        <h2>Chưa có booking nào trong quá khứ</h2>
                        <p>Khi hoàn thành hoặc hủy booking, chúng sẽ xuất hiện tại đây.</p>
                    </c:when>
                    <c:otherwise>
                        <h2>Chưa có booking sắp tới</h2>
                        <p>Chọn sân và khung giờ trống để đặt lịch chơi.</p>
                        <a class="btn btn-sz" href="${ctx}/MainController?action=courts">Xem sân &amp; đặt ngay</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
