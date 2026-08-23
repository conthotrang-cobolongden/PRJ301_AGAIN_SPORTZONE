<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Hóa đơn đã thanh toán — SportZone" scope="request"/>
<c:set var="pageCss" value="invoice-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-invoice" scope="request"/>
<c:set var="headerVariant" value="standard" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container view-section active" id="viewInvoiceList">
    <div class="invoice-page-head"><span class="sz-badge sz-badge-paid">TÀI CHÍNH &amp; THANH TOÁN</span><h1>Hóa Đơn &amp; Lịch Sử Thanh Toán</h1><p>Tra cứu hóa đơn điện tử, sao kê chi phí sân thi đấu và dịch vụ. Chỉ hiển thị hóa đơn có trạng thái Paid.</p></div>
    <c:choose>
        <c:when test="${not empty invoiceList}">
            <div class="sz-card"><div class="table-responsive"><table class="invoice-list-table">
                <thead><tr><th>Mã HĐ</th><th>Ngày Lập</th><th>Sân Thi Đấu</th><th>Hình Thức</th><th class="num">Tổng Tiền</th><th>Trạng Thái</th><th></th></tr></thead>
                <tbody><c:forEach var="inv" items="${invoiceList}"><tr>
                    <td><span class="sz-code"><c:out value="${inv.invoiceCode}"/></span></td>
                    <td><fmt:formatDate value="${inv.paidAtValue}" pattern="dd/MM/yyyy HH:mm"/></td>
                    <td><c:out value="${inv.courtName}"/></td>
                    <td>${inv.paymentMethod == 'Cash' ? 'Tiền mặt tại quầy' : 'Chuyển khoản QR'}</td><td class="num invoice-amount"><fmt:formatNumber value="${inv.finalAmount}" groupingUsed="true"/> đ</td><td><span class="sz-badge sz-badge-paid">Đã thanh toán</span></td><td><a class="invoice-view-btn" href="${ctx}/MainController?action=invoiceDetail&amp;bookingId=${inv.bookingId}">Xem bản in</a></td>
                </tr></c:forEach></tbody>
            </table></div></div>
        </c:when>
        <c:otherwise><div class="invoice-empty"><span class="sz-badge sz-badge-paid">HÓA ĐƠN</span><h2>Chưa có hóa đơn đã thanh toán</h2><p>Hóa đơn sẽ xuất hiện sau khi booking được thanh toán.</p><a class="invoice-view-btn" href="${ctx}/MainController?action=bookings">Xem booking của tôi</a></div></c:otherwise>
    </c:choose>
</section>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
