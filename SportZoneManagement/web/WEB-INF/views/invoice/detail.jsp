<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- U15: Chi tiết hóa đơn (dạng hóa đơn giấy) + U16/U18: trạng thái & phương thức --%>
<c:set var="pageTitle" value="Hóa đơn ${invoice.invoiceCode} — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container sz-section">
    <a class="sz-back-link" href="${ctx}/invoices">&larr; Về danh sách hóa đơn</a>

    <!-- DEEPSEEK: invoice kèm booking info (bookingId, courtName, slotDate, startTime, endTime,
         accountName) + serviceLines (serviceName, quantity, lineTotal) -->
    <div class="sz-card sz-invoice-paper">
        <div class="sz-invoice-head">
            <div>
                <span class="sz-mark">SZ</span><span class="sz-name">SportZone</span>
                <p class="text-muted small mb-0 mt-1">Tổ hợp sân Tennis &amp; Pickleball</p>
            </div>
            <div class="sz-inv-title">
                <h2>Hóa đơn</h2>
                <p class="sz-code mb-0"><c:out value="${invoice.invoiceCode}"/></p>
                <p class="text-muted small mb-0">Lập ngày <fmt:formatDate value="${invoice.invoiceDate}" pattern="dd/MM/yyyy HH:mm"/></p>
            </div>
        </div>

        <dl class="mb-3">
            <div class="sz-kv"><dt>Người thanh toán</dt><dd><c:out value="${invoice.accountName}"/></dd></div>
            <div class="sz-kv"><dt>Booking</dt><dd>#${invoice.bookingId} · <c:out value="${invoice.courtName}"/></dd></div>
            <div class="sz-kv"><dt>Buổi chơi</dt><dd><fmt:formatDate value="${invoice.slotDate}" pattern="dd/MM/yyyy"/> · <fmt:formatDate value="${invoice.startTime}" pattern="HH:mm"/>–<fmt:formatDate value="${invoice.endTime}" pattern="HH:mm"/></dd></div>
        </dl>

        <dl class="mb-3">
            <div class="sz-kv"><dt>Tiền sân</dt><dd><fmt:formatNumber value="${invoice.courtAmount}" type="number" groupingUsed="true"/> đ</dd></div>
            <c:forEach var="l" items="${serviceLines}">
                <div class="sz-kv">
                    <dt><c:out value="${l.serviceName}"/> × ${l.quantity}</dt>
                    <dd><fmt:formatNumber value="${l.lineTotal}" type="number" groupingUsed="true"/> đ</dd>
                </div>
            </c:forEach>
            <c:if test="${invoice.discountAmount > 0}">
                <div class="sz-kv"><dt>Giảm giá</dt><dd>−<fmt:formatNumber value="${invoice.discountAmount}" type="number" groupingUsed="true"/> đ</dd></div>
            </c:if>
            <div class="sz-kv sz-kv-total"><dt>Tổng cộng</dt><dd><fmt:formatNumber value="${invoice.finalAmount}" type="number" groupingUsed="true"/> đ</dd></div>
        </dl>

        <dl class="mb-0">
            <div class="sz-kv">
                <dt>Trạng thái thanh toán</dt>
                <dd>
                    <c:choose>
                        <c:when test="${invoice.paymentStatus == 'Paid'}"><span class="sz-badge sz-badge-paid">Đã thanh toán</span></c:when>
                        <c:when test="${invoice.paymentStatus == 'Unpaid'}"><span class="sz-badge sz-badge-unpaid">Chưa thanh toán</span></c:when>
                        <c:otherwise><span class="sz-badge sz-badge-cancelled">Đã hủy</span></c:otherwise>
                    </c:choose>
                </dd>
            </div>
            <%-- U18: phương thức + thời điểm chỉ hiện khi Paid --%>
            <c:if test="${invoice.paymentStatus == 'Paid'}">
                <div class="sz-kv"><dt>Phương thức</dt><dd>${invoice.paymentMethod == 'BankTransfer' ? 'Chuyển khoản' : 'Tiền mặt'}</dd></div>
                <div class="sz-kv"><dt>Thanh toán lúc</dt><dd><fmt:formatDate value="${invoice.paidAt}" pattern="dd/MM/yyyy HH:mm"/></dd></div>
            </c:if>
        </dl>

        <c:if test="${invoice.paymentStatus == 'Unpaid'}">
            <div class="alert alert-info mt-3 mb-0" role="alert">
                Hóa đơn chưa thanh toán — vui lòng thanh toán tại quầy hoặc chuyển khoản.
                <c:if test="${not empty invoice.paymentNote}">
                    <br>Ghi chú: <c:out value="${invoice.paymentNote}"/>
                </c:if>
            </div>
        </c:if>
        <c:if test="${invoice.paymentStatus == 'Cancelled'}">
            <div class="alert alert-secondary mt-3 mb-0" role="alert">
                Hóa đơn đã hủy do booking bị hủy.
            </div>
        </c:if>
    </div>

    <div class="text-center mt-3">
        <a class="btn btn-outline-success" href="${ctx}/booking?id=${invoice.bookingId}">Xem booking liên kết</a>
    </div>
</section>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
