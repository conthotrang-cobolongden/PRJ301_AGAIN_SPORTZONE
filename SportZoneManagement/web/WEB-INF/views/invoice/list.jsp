<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Hóa Đơn &amp; Lịch Sử Thanh Toán — SportZone" scope="request"/>
<c:set var="pageCss" value="invoice-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-invoice" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- ==============================================
     INVOICE LIST (U14) — SPORTZONE SYNC
     ============================================== -->
<div class="container" style="padding-top:40px; padding-bottom:60px;">
    <!-- Page Header -->
    <div style="margin-bottom:32px;">
        <div class="kicker"><span class="dot"></span> TÀI CHÍNH &amp; THANH TOÁN</div>
        <h1 style="font-size:32px; color:var(--ink); margin:8px 0;">
            Hóa Đơn &amp; <span class="accent-text" style="background:linear-gradient(135deg,#1b5e3b,#caa54b); -webkit-background-clip:text; -webkit-text-fill-color:transparent;">Lịch Sử Giao Dịch</span>
        </h1>
        <p style="color:var(--muted); font-size:14.5px; margin:0;">
            Tra cứu hóa đơn điện tử, sao kê chi phí sân thi đấu và dịch vụ đã thanh toán thành công.
        </p>
    </div>

    <!-- Invoice Content -->
    <c:choose>
        <c:when test="${not empty invoiceList}">
            <div class="sz-card" style="padding:0; overflow:hidden;">
                <div class="table-responsive">
                    <table class="receipt-table" style="margin:0; width:100%;">
                        <thead>
                            <tr>
                                <th style="padding:16px 20px;">Mã HĐ</th>
                                <th style="padding:16px 20px;">Ngày Thanh Toán</th>
                                <th style="padding:16px 20px;">Sân Thi Đấu</th>
                                <th style="padding:16px 20px;">Hình Thức</th>
                                <th style="padding:16px 20px; text-align:right;">Tổng Tiền</th>
                                <th style="padding:16px 20px; text-align:center;">Trạng Thái</th>
                                <th style="padding:16px 20px; text-align:right;"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="inv" items="${invoiceList}">
                                <tr>
                                    <td style="padding:16px 20px; font-family:'Montserrat',sans-serif; font-weight:800; color:var(--gold);">
                                        <c:out value="${inv.invoiceCode}"/>
                                    </td>
                                    <td style="padding:16px 20px; font-size:13.5px;">
                                        <fmt:formatDate value="${inv.paidAtValue}" pattern="dd/MM/yyyy HH:mm"/>
                                    </td>
                                    <td style="padding:16px 20px; font-weight:700;">
                                        <c:out value="${inv.courtName}"/>
                                    </td>
                                    <td style="padding:16px 20px; font-size:13.5px;">
                                        ${inv.paymentMethod == 'Cash' ? 'Tiền mặt tại quầy' : 'Chuyển khoản QR'}
                                    </td>
                                    <td style="padding:16px 20px; text-align:right; font-family:'Montserrat',sans-serif; font-weight:800; color:var(--green);">
                                        <fmt:formatNumber value="${inv.finalAmount}" groupingUsed="true"/> đ
                                    </td>
                                    <td style="padding:16px 20px; text-align:center;">
                                        <span class="sz-badge sz-badge-paid">Đã thanh toán</span>
                                    </td>
                                    <td style="padding:16px 20px; text-align:right;">
                                        <a class="btn login sm" href="${ctx}/MainController?action=invoiceDetail&amp;bookingId=${inv.bookingId}">
                                            Xem bản in →
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <div class="sz-card" style="text-align:center; padding:60px 20px;">
                <div style="font-size:36px; margin-bottom:12px;">🧾</div>
                <h2 style="font-size:20px; font-weight:800; color:var(--ink); margin-bottom:8px;">Chưa có hóa đơn đã thanh toán</h2>
                <p style="color:var(--muted); font-size:14px; max-width:460px; margin:0 auto 24px;">
                    Hóa đơn điện tử sẽ tự động xuất hiện tại đây sau khi đơn đặt sân của bạn được thanh toán thành công.
                </p>
                <a class="btn green sm" href="${ctx}/MainController?action=bookings">
                    Xem danh sách booking của tôi →
                </a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
