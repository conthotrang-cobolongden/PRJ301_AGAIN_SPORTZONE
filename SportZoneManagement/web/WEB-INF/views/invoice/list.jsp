<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Hóa đơn đã thanh toán — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container sz-section">
    <div class="sz-pagehead"><div><p class="sz-kicker sz-kicker-dark mb-1">Thanh toán</p><h1>Hóa đơn đã thanh toán</h1><p class="sz-lead-sm">Chỉ hiển thị hóa đơn có trạng thái Paid.</p></div></div>
    <c:choose>
        <c:when test="${not empty invoiceList}">
            <div class="table-responsive"><table class="table table-bordered align-middle sz-table">
                <thead><tr><th>Mã hóa đơn</th><th>Ngày thanh toán</th><th>Sân</th><th class="num">Số tiền</th><th></th></tr></thead>
                <tbody><c:forEach var="inv" items="${invoiceList}"><tr>
                    <td><span class="sz-code"><c:out value="${inv.invoiceCode}"/></span></td>
                    <td><fmt:formatDate value="${inv.paidAtValue}" pattern="dd/MM/yyyy HH:mm"/></td>
                    <td><c:out value="${inv.courtName}"/></td>
                    <td class="num"><fmt:formatNumber value="${inv.finalAmount}" groupingUsed="true"/> đ</td>
                    <td><a class="btn btn-outline-success btn-sm" href="${ctx}/MainController?action=invoiceDetail&amp;bookingId=${inv.bookingId}">Xem</a></td>
                </tr></c:forEach></tbody>
            </table></div>
        </c:when>
        <c:otherwise><div class="sz-empty"><h2>Chưa có hóa đơn đã thanh toán</h2><a class="btn btn-sz" href="${ctx}/MainController?action=bookings">Xem booking của tôi</a></div></c:otherwise>
    </c:choose>
</section>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
