<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- U14: Hóa đơn của tôi — chỉ booking ĐÃ có hóa đơn mới xuất hiện (rule 3, 4) --%>
<c:set var="pageTitle" value="Hóa đơn của tôi — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container sz-section">
    <div class="sz-pagehead">
        <div>
            <p class="sz-kicker sz-kicker-dark mb-1">Thanh toán</p>
            <h1>Hóa đơn của tôi</h1>
            <p class="sz-lead-sm">Chỉ hiển thị booking đã được lập hóa đơn. Booking chưa thanh toán sẽ không có trong danh sách này.</p>
        </div>
    </div>

    <!-- DEEPSEEK: bind invoiceList — mỗi item kèm: invoiceId, invoiceCode, invoiceDate,
         bookingId, courtName, slotDate, finalAmount, paymentStatus, paymentMethod (NULL nếu Unpaid) -->
    <c:choose>
        <c:when test="${not empty invoiceList}">
            <div class="table-responsive">
                <table class="table table-bordered align-middle sz-table sz-table-cards">
                    <thead>
                        <tr>
                            <th>Mã hóa đơn</th>
                            <th>Ngày lập</th>
                            <th>Booking</th>
                            <th class="num">Số tiền</th>
                            <th class="mid">Trạng thái</th>
                            <th class="mid">Phương thức</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="inv" items="${invoiceList}">
                            <tr class="${inv.paymentStatus == 'Cancelled' ? 'row-muted' : ''}">
                                <td data-label="Mã hóa đơn"><span class="sz-code"><c:out value="${inv.invoiceCode}"/></span></td>
                                <td data-label="Ngày lập"><fmt:formatDate value="${inv.invoiceDate}" pattern="dd/MM/yyyy"/></td>
                                <td data-label="Booking">
                                    <c:out value="${inv.courtName}"/> — <fmt:formatDate value="${inv.slotDate}" pattern="dd/MM/yyyy"/>
                                </td>
                                <td class="num" data-label="Số tiền"><fmt:formatNumber value="${inv.finalAmount}" type="number" groupingUsed="true"/> đ</td>
                                <td class="mid" data-label="Trạng thái">
                                    <c:choose>
                                        <c:when test="${inv.paymentStatus == 'Paid'}"><span class="sz-badge sz-badge-paid">Đã thanh toán</span></c:when>
                                        <c:when test="${inv.paymentStatus == 'Unpaid'}"><span class="sz-badge sz-badge-unpaid">Chưa thanh toán</span></c:when>
                                        <c:otherwise><span class="sz-badge sz-badge-cancelled">Đã hủy</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="mid" data-label="Phương thức">
                                    <%-- U18: PaymentMethod NULL khi Unpaid → hiển thị dấu gạch --%>
                                    <c:choose>
                                        <c:when test="${inv.paymentMethod == 'BankTransfer'}">Chuyển khoản</c:when>
                                        <c:when test="${inv.paymentMethod == 'Cash'}">Tiền mặt</c:when>
                                        <c:otherwise><span class="text-muted">—</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="sz-td-actions">
                                    <a class="btn btn-outline-success btn-sm" href="${ctx}/invoice?id=${inv.invoiceId}">Xem</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <div class="sz-empty">
                <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
                <h2>Chưa có hóa đơn nào</h2>
                <p>Khi bạn thanh toán, quầy sẽ lập hóa đơn và hiển thị tại đây.</p>
                <a class="btn btn-sz" href="${ctx}/bookings">Xem booking của tôi</a>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
