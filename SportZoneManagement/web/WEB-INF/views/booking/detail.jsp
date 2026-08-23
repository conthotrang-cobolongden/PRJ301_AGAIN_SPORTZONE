<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- U12: Chi tiet booking cua chinh user --%>

<c:set var="pageTitle"
       value="Chi tiết booking — SportZone"
       scope="request"/>

<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<c:set var="ctx"
       value="${pageContext.request.contextPath}"/>

<section class="container sz-section">
    <a class="sz-back-link" href="${ctx}/bookings">
        &larr; Về danh sách booking
    </a>

    <c:if test="${not empty success}">
        <div class="alert alert-success mt-3"
             role="alert">
            <c:out value="${success}"/>
        </div>
    </c:if>

    <div class="sz-pagehead mt-3">
        <div>
            <p class="sz-kicker sz-kicker-dark mb-1">
                Booking #<c:out value="${booking.bookingId}"/>
            </p>

            <h1 class="d-flex flex-wrap align-items-center gap-2">
                Chi tiết booking

                <c:choose>
                    <c:when test="${booking.status == 'Pending'}">
                        <span class="sz-badge sz-badge-pending">
                            Chờ xác nhận
                        </span>
                    </c:when>

                    <c:when test="${booking.status == 'Confirmed'}">
                        <span class="sz-badge sz-badge-confirmed">
                            Đã xác nhận
                        </span>
                    </c:when>

                    <c:when test="${booking.status == 'Completed'}">
                        <span class="sz-badge sz-badge-completed">
                            Hoàn thành
                        </span>
                    </c:when>

                    <c:when test="${booking.status == 'Cancelled'}">
                        <span class="sz-badge sz-badge-cancelled">
                            Đã hủy
                        </span>
                    </c:when>

                    <c:otherwise>
                        <span class="sz-badge sz-badge-noshow">
                            Không đến
                        </span>
                    </c:otherwise>
                </c:choose>
            </h1>
        </div>
    </div>

    <div class="row g-4">
        <div class="col-lg-7">
            <div class="sz-card">
                <h2 class="h5 mb-3">
                    Thông tin buổi chơi
                </h2>

                <dl class="mb-3">
                    <div class="sz-kv">
                        <dt>Sân</dt>
                        <dd>
                            <c:out value="${booking.courtName}"/>
                            (<c:out value="${booking.typeName}"/>)
                        </dd>
                    </div>

                    <div class="sz-kv">
                        <dt>Vị trí</dt>
                        <dd>
                            <c:out value="${booking.location}"/>
                        </dd>
                    </div>

                    <div class="sz-kv">
                        <dt>Ngày chơi</dt>
                        <dd>
                            <fmt:formatDate
                                value="${booking.slotDateValue}"
                                pattern="dd/MM/yyyy"/>
                        </dd>
                    </div>

                    <div class="sz-kv">
                        <dt>Khung giờ</dt>
                        <dd>
                            <fmt:formatDate
                                value="${booking.startTimeValue}"
                                pattern="HH:mm"/>
                            –
                            <fmt:formatDate
                                value="${booking.endTimeValue}"
                                pattern="HH:mm"/>
                        </dd>
                    </div>

                    <div class="sz-kv">
                        <dt>Đặt lúc</dt>
                        <dd>
                            <fmt:formatDate
                                value="${booking.bookingDateValue}"
                                pattern="dd/MM/yyyy HH:mm"/>
                        </dd>
                    </div>
                </dl>

                <h2 class="h6 mb-2">
                    Dịch vụ kèm theo
                </h2>

                <c:choose>
                    <c:when test="${not empty serviceLines}">
                        <dl class="mb-0">
                            <c:forEach var="line"
                                       items="${serviceLines}">
                                <div class="sz-kv">
                                    <dt>
                                        <c:out value="${line.serviceName}"/>
                                        ×
                                        <c:out value="${line.quantity}"/>
                                    </dt>

                                    <dd>
                                        <fmt:formatNumber
                                            value="${line.lineTotal}"
                                            type="number"
                                            groupingUsed="true"/>
                                        đ
                                    </dd>
                                </div>
                            </c:forEach>
                        </dl>
                    </c:when>

                    <c:otherwise>
                        <p class="text-muted mb-0">
                            Không dùng dịch vụ kèm theo.
                        </p>
                    </c:otherwise>
                </c:choose>

                <c:if test="${not empty booking.notes}">
                    <hr>

                    <p class="small mb-1 fw-bold">
                        Ghi chú
                    </p>

                    <p class="mb-0">
                        <c:out value="${booking.notes}"/>
                    </p>
                </c:if>
            </div>
        </div>

        <div class="col-lg-5">
            <div class="sz-card">
                <h2 class="h5 mb-3">
                    Tổng tiền
                </h2>

                <dl class="mb-3">
                    <div class="sz-kv">
                        <dt>Tiền sân</dt>
                        <dd>
                            <fmt:formatNumber
                                value="${booking.courtPriceAtOrder}"
                                type="number"
                                groupingUsed="true"/>
                            đ
                        </dd>
                    </div>

                    <div class="sz-kv">
                        <dt>Tiền dịch vụ</dt>
                        <dd>
                            <fmt:formatNumber
                                value="${booking.serviceTotalAtOrder}"
                                type="number"
                                groupingUsed="true"/>
                            đ
                        </dd>
                    </div>

                    <div class="sz-kv sz-kv-total">
                        <dt>Tổng cộng</dt>
                        <dd>
                            <fmt:formatNumber
                                value="${booking.totalAmount}"
                                type="number"
                                groupingUsed="true"/>
                            đ
                        </dd>
                    </div>
                </dl>

                <h2 class="h6 mb-2">
                    Thanh toán
                </h2>

                <c:choose>
                    <c:when test="${booking.paymentStatus == 'Paid'}">
                        <span class="sz-badge sz-badge-paid">
                            Đã thanh toán
                        </span>
                    </c:when>

                    <c:when test="${booking.paymentStatus == 'Unpaid'}">
                        <span class="sz-badge sz-badge-unpaid">
                            Chưa thanh toán
                        </span>
                    </c:when>

                    <c:when test="${booking.paymentStatus == 'Cancelled'}">
                        <span class="sz-badge sz-badge-cancelled">
                            Hóa đơn đã hủy
                        </span>
                    </c:when>

                    <c:otherwise>
                        <span class="sz-badge sz-badge-none">
                            Chưa có hóa đơn
                        </span>

                        <p class="text-muted small mt-2 mb-0">
                            Hóa đơn sẽ được phát triển và xử lý
                            trong phase chức năng tiếp theo.
                        </p>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="alert alert-info mt-4 mb-0">
                User hiện chỉ có thể xem booking.
                Chức năng tự hủy booking không được hỗ trợ.
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>