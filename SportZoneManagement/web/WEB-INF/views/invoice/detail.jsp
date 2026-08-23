<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Hóa đơn ${invoice.invoiceCode} — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<section class="container sz-section">
    <a class="sz-back-link" href="${ctx}/MainController?action=invoices">&larr; Về danh sách hóa đơn</a>
    <div class="sz-card sz-invoice-paper mt-3">
        <div class="sz-invoice-head"><div><span class="sz-mark">SZ</span><span class="sz-name">SportZone</span></div><div class="sz-inv-title"><h2>Hóa đơn</h2><p class="sz-code mb-0"><c:out value="${invoice.invoiceCode}"/></p><span class="sz-badge sz-badge-paid">Đã thanh toán</span></div></div>
        <dl class="mb-3">
            <div class="sz-kv"><dt>Khách hàng</dt><dd><c:out value="${invoice.accountName}"/></dd></div>
            <div class="sz-kv"><dt>Sân</dt><dd><c:out value="${invoice.courtName}"/></dd></div>
            <div class="sz-kv"><dt>Buổi chơi</dt><dd><fmt:formatDate value="${invoice.slotDateValue}" pattern="dd/MM/yyyy"/> · <fmt:formatDate value="${invoice.startTimeValue}" pattern="HH:mm"/>–<fmt:formatDate value="${invoice.endTimeValue}" pattern="HH:mm"/></dd></div>
            <div class="sz-kv"><dt>Thanh toán lúc</dt><dd><fmt:formatDate value="${invoice.paidAtValue}" pattern="dd/MM/yyyy HH:mm"/></dd></div>
            <div class="sz-kv"><dt>Phương thức</dt><dd>${invoice.paymentMethod == 'Cash' ? 'Tiền mặt' : 'Chuyển khoản'}</dd></div>
        </dl>
        <dl class="mb-0">
            <div class="sz-kv"><dt>Tiền sân</dt><dd><fmt:formatNumber value="${invoice.courtAmount}" groupingUsed="true"/> đ</dd></div>
            <c:forEach var="line" items="${serviceLines}"><div class="sz-kv"><dt><c:out value="${line.serviceName}"/> × ${line.quantity}</dt><dd><fmt:formatNumber value="${line.lineTotal}" groupingUsed="true"/> đ</dd></div></c:forEach>
            <div class="sz-kv sz-kv-total"><dt>Tổng cộng</dt><dd><fmt:formatNumber value="${invoice.finalAmount}" groupingUsed="true"/> đ</dd></div>
        </dl>
    </div>
    <div class="text-center mt-3"><a class="btn btn-outline-success" href="${ctx}/MainController?action=bookingDetail&amp;id=${invoice.bookingId}">Xem booking liên kết</a></div>
</section>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
