<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Hóa đơn ${invoice.invoiceCode} — SportZone" scope="request"/>
<c:set var="pageCss" value="invoice-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-invoice" scope="request"/>
<c:set var="headerVariant" value="standard" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<section class="container view-section active" id="viewInvoiceDetail">
    <div class="invoice-actions"><a class="invoice-view-btn" href="${ctx}/MainController?action=invoices">&larr; Quay lại danh sách</a><button class="invoice-print-btn" type="button" onclick="window.print()">In Hóa Đơn / Xuất PDF</button></div>
    <div class="invoice-sheet">
        <div class="invoice-top"><div><div class="brand-logo invoice-brand"><span class="invoice-brand-mark">SZ</span><span>Sport<span class="zone">Zone</span></span></div><p>Tổ Hợp Thể Thao Cao Cấp Hòa Lạc · Hà Nội</p></div><div class="invoice-title"><h1>HÓA ĐƠN ĐIỆN TỬ</h1><strong>Mã HĐ: <c:out value="${invoice.invoiceCode}"/></strong><span class="sz-badge sz-badge-paid">Đã thanh toán</span></div></div>
        <div class="invoice-meta-grid"><div><span class="invoice-label">KHÁCH HÀNG</span><strong><c:out value="${invoice.accountName}"/></strong></div><div><span class="invoice-label">THÔNG TIN BOOKING GỐC</span><strong>#${invoice.bookingId} · <c:out value="${invoice.courtName}"/></strong><p><fmt:formatDate value="${invoice.slotDateValue}" pattern="dd/MM/yyyy"/> · <fmt:formatDate value="${invoice.startTimeValue}" pattern="HH:mm"/>–<fmt:formatDate value="${invoice.endTimeValue}" pattern="HH:mm"/></p></div></div>
        <dl class="invoice-detail-list">
            <div class="sz-kv"><dt>Khách hàng</dt><dd><c:out value="${invoice.accountName}"/></dd></div>
            <div class="sz-kv"><dt>Sân</dt><dd><c:out value="${invoice.courtName}"/></dd></div>
            <div class="sz-kv"><dt>Buổi chơi</dt><dd><fmt:formatDate value="${invoice.slotDateValue}" pattern="dd/MM/yyyy"/> · <fmt:formatDate value="${invoice.startTimeValue}" pattern="HH:mm"/>–<fmt:formatDate value="${invoice.endTimeValue}" pattern="HH:mm"/></dd></div>
            <div class="sz-kv"><dt>Thanh toán lúc</dt><dd><fmt:formatDate value="${invoice.paidAtValue}" pattern="dd/MM/yyyy HH:mm"/></dd></div>
            <div class="sz-kv"><dt>Phương thức</dt><dd>${invoice.paymentMethod == 'Cash' ? 'Tiền mặt' : 'Chuyển khoản'}</dd></div>
        </dl>
        <dl class="invoice-detail-list">
            <div class="sz-kv"><dt>Tiền sân</dt><dd><fmt:formatNumber value="${invoice.courtAmount}" groupingUsed="true"/> đ</dd></div>
            <c:forEach var="line" items="${serviceLines}"><div class="sz-kv"><dt><c:out value="${line.serviceName}"/> × ${line.quantity}</dt><dd><fmt:formatNumber value="${line.lineTotal}" groupingUsed="true"/> đ</dd></div></c:forEach>
            <div class="sz-kv sz-kv-total"><dt>Tổng Cộng</dt><dd><fmt:formatNumber value="${invoice.finalAmount}" groupingUsed="true"/> đ</dd></div>
        </dl>
    </div>
    <div class="invoice-linked"><a class="invoice-view-btn" href="${ctx}/MainController?action=${sessionScope.roleName == 'Admin' ? 'adminBookings' : 'bookingDetail'}&amp;id=${invoice.bookingId}">Xem booking liên kết</a></div>
</section>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
