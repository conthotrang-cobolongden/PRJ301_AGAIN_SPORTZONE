<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Hóa Đơn Điện Tử #${invoice.invoiceCode} — SportZone" scope="request"/>
<c:set var="pageCss" value="invoice-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-invoice" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- ==============================================
     INVOICE DETAIL / PRINTABLE E-RECEIPT (U15)
     ============================================== -->
<div class="container" style="padding-top:40px; padding-bottom:60px;">
    <!-- Top Actions Bar -->
    <div style="display:flex; justify-content:space-between; align-items:center; max-width:840px; margin:0 auto 24px; flex-wrap:wrap; gap:12px;">
        <a class="btn login sm" href="${ctx}/MainController?action=${sessionScope.roleName == 'Admin' ? 'adminBookings' : 'invoices'}">
            &larr; Quay lại danh sách
        </a>
        <button class="btn green sm" type="button" onclick="window.print()">
            In Hóa Đơn / Xuất PDF
        </button>
    </div>

    <!-- Printable Luxury E-Receipt Sheet -->
    <div class="invoice-sheet">
        <!-- Invoice Sheet Top Header -->
        <div class="invoice-top">
            <div>
                <div class="brand" style="margin-bottom:8px;">
                    <svg viewBox="0 0 48 48" aria-hidden="true" style="width:38px; height:38px;">
                        <rect x="2" y="2" width="44" height="44" rx="14" fill="#0b1911"/>
                        <path d="M12 24h24M24 12v24" stroke="#caa54b" stroke-width="1.6"/>
                        <path d="M16 14h16a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H16a2 2 0 0 1-2-2V16a2 2 0 0 1 2-2Z" fill="none" stroke="#fff" stroke-width="1.5"/>
                        <circle cx="24" cy="24" r="3.5" fill="#1b5e3b"/>
                    </svg>
                    <span>Sport<span class="zone">Zone</span></span>
                </div>
                <div style="font-size:12.5px; color:var(--muted); font-weight:600;">Tổ Hợp Thể Thao Cao Cấp TP. Hồ Chí Minh</div>
                <div style="font-size:12px; color:var(--muted); margin-top:2px;">Khu Công Nghệ Cao, TP. Thủ Đức, TP.HCM · Hotline: (+84) 901 234 567</div>
            </div>

            <div style="text-align:right;">
                <h1 style="font-size:22px; color:var(--green); margin-bottom:4px; font-weight:900;">HÓA ĐƠN ĐIỆN TỬ</h1>
                <div style="font-family:'Montserrat',sans-serif; font-size:15px; font-weight:800; color:var(--ink);">
                    Mã HĐ: <c:out value="${invoice.invoiceCode}"/>
                </div>
                <div style="font-size:12.5px; color:var(--muted); margin-top:4px;">
                    Ngày lập: <fmt:formatDate value="${invoice.paidAtValue}" pattern="dd/MM/yyyy HH:mm"/>
                </div>
                <div style="margin-top:6px;">
                    <span class="sz-badge sz-badge-paid">Đã thanh toán</span>
                </div>
            </div>
        </div>

        <!-- Meta Grid -->
        <div class="invoice-meta-grid">
            <div style="background:var(--cream); padding:16px 20px; border-radius:14px; border:1px solid var(--line);">
                <div style="font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); margin-bottom:4px;">
                    KHÁCH HÀNG
                </div>
                <div style="font-size:15px; font-weight:800; color:var(--ink);"><c:out value="${invoice.accountName}"/></div>
                <div style="font-size:12.5px; color:var(--muted); margin-top:2px;">Mã tài khoản hệ thống SportZone</div>
            </div>

            <div style="background:var(--cream); padding:16px 20px; border-radius:14px; border:1px solid var(--line);">
                <div style="font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); margin-bottom:4px;">
                    THÔNG TIN BOOKING GỐC
                </div>
                <div style="font-size:14px; font-weight:800; color:var(--ink);">Mã Đơn #${invoice.bookingId} · <c:out value="${invoice.courtName}"/></div>
                <div style="font-size:12.5px; color:var(--muted); margin-top:2px;">
                    <fmt:formatDate value="${invoice.slotDateValue}" pattern="dd/MM/yyyy"/> · <fmt:formatDate value="${invoice.startTimeValue}" pattern="HH:mm"/> – <fmt:formatDate value="${invoice.endTimeValue}" pattern="HH:mm"/>
                </div>
            </div>
        </div>

        <!-- Line Items Table -->
        <table class="receipt-table" style="width:100%; border-collapse:collapse; margin-bottom:28px;">
            <thead>
                <tr>
                    <th style="padding:14px 16px; background:var(--cream); font-size:11.5px; font-weight:800; text-transform:uppercase; letter-spacing:.06em; color:var(--muted); border-bottom:1px solid var(--line);">Khoản Mục / Dịch Vụ</th>
                    <th style="padding:14px 16px; background:var(--cream); font-size:11.5px; font-weight:800; text-transform:uppercase; letter-spacing:.06em; color:var(--muted); border-bottom:1px solid var(--line); text-align:center;">Số Lượng</th>
                    <th style="padding:14px 16px; background:var(--cream); font-size:11.5px; font-weight:800; text-transform:uppercase; letter-spacing:.06em; color:var(--muted); border-bottom:1px solid var(--line); text-align:right;">Thành Tiền</th>
                </tr>
            </thead>
            <tbody>
                <!-- Court Rental Line -->
                <tr>
                    <td style="padding:16px; border-bottom:1px solid rgba(11,25,17,.06); font-size:14px;">
                        <strong>Thuê Sân <c:out value="${invoice.courtName}"/></strong>
                        <small style="display:block; color:var(--muted); font-size:12px; margin-top:2px;">
                            Khung giờ <fmt:formatDate value="${invoice.startTimeValue}" pattern="HH:mm"/> – <fmt:formatDate value="${invoice.endTimeValue}" pattern="HH:mm"/> (<fmt:formatDate value="${invoice.slotDateValue}" pattern="dd/MM/yyyy"/>)
                        </small>
                    </td>
                    <td style="padding:16px; border-bottom:1px solid rgba(11,25,17,.06); text-align:center; font-weight:700;">
                        1 giờ
                    </td>
                    <td style="padding:16px; border-bottom:1px solid rgba(11,25,17,.06); text-align:right; font-family:'Montserrat',sans-serif; font-weight:800; color:var(--green);">
                        <fmt:formatNumber value="${invoice.courtAmount}" groupingUsed="true"/> đ
                    </td>
                </tr>

                <!-- Additional Service Lines -->
                <c:forEach var="line" items="${serviceLines}">
                    <tr>
                        <td style="padding:16px; border-bottom:1px solid rgba(11,25,17,.06); font-size:14px;">
                            <c:out value="${line.serviceName}"/>
                        </td>
                        <td style="padding:16px; border-bottom:1px solid rgba(11,25,17,.06); text-align:center; font-weight:700;">
                            × ${line.quantity}
                        </td>
                        <td style="padding:16px; border-bottom:1px solid rgba(11,25,17,.06); text-align:right; font-family:'Montserrat',sans-serif; font-weight:800; color:var(--green);">
                            <fmt:formatNumber value="${line.lineTotal}" groupingUsed="true"/> đ
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <!-- Totals & Payment Summary -->
        <div style="display:flex; justify-content:space-between; align-items:flex-end; flex-wrap:wrap; gap:20px; padding-top:10px;">
            <div style="background:var(--cream); padding:16px 20px; border-radius:14px; border:1px solid var(--line); min-width:280px;">
                <div style="font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); margin-bottom:4px;">
                    HÌNH THỨC THANH TOÁN
                </div>
                <div style="font-size:14px; font-weight:800; color:var(--green);">
                    ${invoice.paymentMethod == 'Cash' ? 'Tiền mặt tại quầy' : 'Chuyển khoản QR'}
                </div>
                <div style="font-size:12px; color:var(--muted); margin-top:2px;">
                    Trạng thái: <strong style="color:var(--success);">ĐÃ THANH TOÁN THÀNH CÔNG</strong>
                </div>
            </div>

            <div style="text-align:right;">
                <div style="font-size:12.5px; color:var(--muted); margin-bottom:6px;">Thuế GTGT (VAT 8%): Đã bao gồm trong giá</div>
                <div style="font-family:'Montserrat',sans-serif; font-size:26px; font-weight:900; color:var(--green);">
                    Tổng Cộng: <fmt:formatNumber value="${invoice.finalAmount}" groupingUsed="true"/> đ
                </div>
            </div>
        </div>
    </div>

    <!-- Bottom Linked Booking Action -->
    <div style="text-align:center; margin-top:28px;">
        <a class="btn login sm" href="${ctx}/MainController?action=${sessionScope.roleName == 'Admin' ? 'adminBookings' : 'bookingDetail'}&amp;id=${invoice.bookingId}">
            Xem chi tiết booking liên kết →
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
