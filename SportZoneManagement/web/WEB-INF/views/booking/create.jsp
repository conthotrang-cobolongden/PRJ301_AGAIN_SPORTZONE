<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- U10: Tạo booking (KHÔNG sinh hóa đơn — rule 1) --%>
<c:set var="pageTitle" value="Đặt sân — SportZone" scope="request"/>
<c:set var="pageCss" value="booking-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-booking" scope="request"/>
<c:set var="headerVariant" value="standard" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container view-section active" id="viewCreate">
    <c:if test="${sessionScope.roleName == 'Admin'}">
        <div class="alert alert-info">Booking này được ghi nhận cho <strong>Khách vãng lai</strong>.</div>
    </c:if>
    <div class="booking-page-intro">
        <span class="sz-badge sz-badge-completed">ĐẶT SÂN ONLINE TRỰC TUYẾN</span>
        <h1>Đặt Lịch Sân Thi Đấu</h1>
        <p>Chọn sân, khung giờ và dịch vụ đi kèm. Thanh toán dễ dàng qua chuyển khoản QR hoặc tại quầy lễ tân.</p>
    </div>

    <!-- Rule 9: Frozen đăng nhập được nhưng không được đặt sân -->
    <c:if test="${sessionScope.frozen}">
        <div class="alert alert-danger" role="alert">
            Tài khoản đang bị khóa — không thể đặt sân. Vui lòng liên hệ quầy để được hỗ trợ.
        </div>
    </c:if>

    <!-- DEEPSEEK: lỗi nghiệp vụ chung (trùng slot, sai validate...) bind vào biến error -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
    </c:if>

    <div class="booking-create-grid">
        <!-- ===== FORM ===== -->
        <div>
            <!-- Bước 1+2: chọn sân & ngày — GET reload lại lưới slot -->
            <div class="sz-card mb-4">
                <form method="get" action="${ctx}/MainController">
                    <input type="hidden" name="action" value="bookingCreate"/>
                    <div class="row g-3 align-items-end">
                        <div class="col-md-5">
                            <p class="sz-step-label mb-2"><span class="step-num">1</span> Chọn sân đấu</p>
                            <!-- DEEPSEEK: courtList = chỉ sân Available; option cần data-price cho JS tính tổng -->
                            <select class="form-select" name="courtId" aria-label="Chọn sân">
                                <c:forEach var="c" items="${courtList}">
                                    <option value="${c.courtId}" data-price="${c.pricePerHour}"
                                            ${c.courtId == selectedCourtId ? 'selected' : ''}>
                                        <c:out value="${c.courtName}"/> — <fmt:formatNumber value="${c.pricePerHour}" type="number" groupingUsed="true"/> đ/giờ
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <p class="sz-step-label mb-2"><span class="step-num">2</span> Chọn ngày chơi</p>
                            <input class="form-control" type="date" name="date"
                                   value="${selectedDate}" min="${minDate}" max="${maxDate}" aria-label="Chọn ngày">
                        </div>
                        <div class="col-md-3 d-grid">
                            <button class="btn btn-outline-success" type="submit">Xem khung giờ</button>
                        </div>
                    </div>
                </form>
            </div>

            <!-- DEEPSEEK: form POST đặt sân. selectedSlotId để pre-select khi vào từ court/detail.
                 fieldErrors.slotId → alert lỗi chọn slot. -->
            <form method="post" action="${ctx}/MainController?action=bookingCreate" id="szBookingForm"
                  data-court-price="${selectedCourtPrice}" novalidate>
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                <input type="hidden" name="courtId" value="${selectedCourtId}"/>
                <input type="hidden" name="date" value="${selectedDate}"/>

                <!-- Bước 3: khung giờ -->
                <div class="sz-card mb-4">
                    <p class="sz-step-label"><span class="step-num">3</span> Chọn khung giờ trống</p>
                    <c:choose>
                        <c:when test="${not empty slotList}">
                            <div class="slot-grid-radio">
                                <c:forEach var="s" items="${slotList}">
                                    <c:choose>
                                        <c:when test="${s.status == 'Available'}">
                                            <label class="slot-radio-label">
                                                <input class="sz-slot-radio" type="radio" name="slotId"
                                                       value="${s.timeSlotId}"
                                                       data-start="${s.startTime}"
                                                       data-end="${s.endTime}"
                                                       ${s.timeSlotId == selectedSlotId ? 'checked' : ''}>
                                                <span class="slot-display-time"><c:out value="${s.startTime}"/> – <c:out value="${s.endTime}"/></span><span class="slot-display-state">Trống</span>
                                            </label>
                                        </c:when>
                                        <c:when test="${s.status == 'Booked'}">
                                            <span class="slot-radio-label is-booked"><span class="slot-display-time"><c:out value="${s.startTime}"/> – <c:out value="${s.endTime}"/></span><span class="slot-display-state">Đã đặt</span>
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="slot-radio-label is-booked"><span class="slot-display-time"><c:out value="${s.startTime}"/> – <c:out value="${s.endTime}"/></span><span class="slot-display-state">Đóng</span>
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </div>
                            <div class="sz-slot-legend">
                                <span><span class="swatch swatch-available"></span>Trống</span>
                                <span><span class="swatch swatch-booked"></span>Đã đặt</span>
                                <span><span class="swatch swatch-closed"></span>Đóng</span>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="sz-empty">
                                <svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                                <h2>Chưa mở khung giờ ngày này</h2>
                                <p>Chọn sân và ngày ở trên rồi bấm "Xem khung giờ".</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Bước 4: dịch vụ kèm theo -->
                <div class="sz-card mb-4">
                    <p class="sz-step-label"><span class="step-num">4</span> Dịch vụ bổ sung <span class="text-muted fw-normal">(Tùy chọn)</span></p>
                    <!-- DEEPSEEK: serviceList = dịch vụ IsActive=1; mỗi dòng data-price cho JS -->
                    <c:choose>
                        <c:when test="${not empty serviceList}">
                            <c:forEach var="sv" items="${serviceList}">
                                <div class="sz-service-row service-item-row" data-price="${sv.price}">
                                    <div class="form-check">
                                        <input class="form-check-input"
                                               type="checkbox"
                                               name="serviceId"
                                               value="${sv.serviceId}"
                                               id="svc${sv.serviceId}"
                                               ${not empty selectedServiceQuantities[sv.serviceId] ? 'checked' : ''}>
                                        <label class="form-check-label" for="svc${sv.serviceId}">
                                            <c:out value="${sv.serviceName}"/>
                                        </label>
                                    </div>
                                    <span class="sz-svc-price"><fmt:formatNumber value="${sv.price}" type="number" groupingUsed="true"/> đ</span>
                                    <input class="form-control form-control-sm sz-qty"
                                           type="number"
                                           name="qty_${sv.serviceId}"
                                           value="${empty selectedServiceQuantities[sv.serviceId]
                                                    ? 1
                                                    : selectedServiceQuantities[sv.serviceId]}"
                                           min="1"
                                           max="20"
                                           ${empty selectedServiceQuantities[sv.serviceId] ? 'disabled' : ''}
                                           aria-label="Số lượng dịch vụ">
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted mb-0">Hiện chưa có dịch vụ kèm theo.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="sz-card mb-4">
                    <label class="form-label fw-bold" for="notes">
                        Ghi chú
                        <span class="text-muted fw-normal">(không bắt buộc)</span>
                    </label>

                    <textarea class="form-control"
                              id="notes"
                              name="notes"
                              maxlength="255"
                              rows="3"><c:out value="${param.notes}"/></textarea>
                </div>
                <button class="btn-action-green booking-submit" type="submit" ${sessionScope.frozen ? 'disabled' : ''}>
                    Xác nhận đặt sân
                </button>
            </form>
        </div>

        <!-- ===== TÓM TẮT ĐƠN (sticky) ===== -->
        <div>
            <div class="summary-card">
                <h2>Tóm Tắt Đặt Sân</h2>
                <div class="sum-row"><span>Sân thi đấu</span><strong id="szSumCourt">—</strong></div>
                <div class="sum-row"><span>Ngày chơi</span><strong id="szSumDate">—</strong></div>
                <div class="sum-row"><span>Khung giờ</span><strong id="szSumSlot">—</strong></div>
                <div class="sum-row"><span>Tiền thuê sân</span><strong id="szSumCourtPrice">0 đ</strong></div>
                <div class="sum-row"><span>Dịch vụ kèm theo</span><strong id="szSumService">0 đ</strong></div>
                <div class="sum-row total"><span>Tổng thanh toán</span><strong id="szSumTotal">0 đ</strong></div>
                <!-- Rule 1: U10 không tạo hóa đơn -->
                <p class="text-muted small mt-3 mb-0">
                    Booking ở trạng thái <span class="sz-badge sz-badge-pending">Chờ xác nhận</span> —
                    hóa đơn sẽ được lập tại quầy khi bạn thanh toán.
                </p>
            </div>
        </div>
    </div>
</section>

<script>
// Preview tổng tiền + đồng bộ ô slot được chọn (không thêm lib ngoài — plan 2.2)
    (function () {
        var form = document.getElementById('szBookingForm');
        if (!form)
            return;
        var fmt = new Intl.NumberFormat('vi-VN');

        function money(n) {
            return fmt.format(n) + ' đ';
        }

        function updateTotal() {
            var courtPrice = parseFloat(form.getAttribute('data-court-price')) || 0;
            var svcTotal = 0;
            document.querySelectorAll('.sz-service-row').forEach(function (row) {
                var cb = row.querySelector('input[type="checkbox"]');
                var qty = row.querySelector('.sz-qty');
                if (cb.checked) {
                    var q = Math.max(1, parseInt(qty.value, 10) || 1);
                    svcTotal += (parseFloat(row.getAttribute('data-price')) || 0) * q;
                }
            });
            document.getElementById('szSumCourtPrice').textContent = money(courtPrice);
            document.getElementById('szSumService').textContent = money(svcTotal);
            document.getElementById('szSumTotal').textContent = money(courtPrice + svcTotal);
        }

        function updateSlotSummary() {
            var checked = form.querySelector('.sz-slot-radio:checked');
            document.querySelectorAll('label.slot-radio-label').forEach(function (l) {
                l.classList.remove('is-selected');
            });
            if (checked) {
                checked.closest('.slot-radio-label').classList.add('is-selected');
                document.getElementById('szSumSlot').textContent =
                        checked.getAttribute('data-start') + '–' + checked.getAttribute('data-end');
            } else {
                document.getElementById('szSumSlot').textContent = '—';
            }
        }

        function updateCourtSummary() {
            var sel = document.querySelector('select[name="courtId"]');
            var dateInput = document.querySelector('input[name="date"]');
            if (sel && sel.selectedIndex >= 0) {
                document.getElementById('szSumCourt').textContent =
                        sel.options[sel.selectedIndex].textContent.split(' — ')[0].trim();
            }
            if (dateInput && dateInput.value) {
                var p = dateInput.value.split('-');
                document.getElementById('szSumDate').textContent = p[2] + '/' + p[1] + '/' + p[0];
            }
        }

        form.addEventListener('change', function (e) {
            var t = e.target;
            if (t.classList.contains('sz-slot-radio'))
                updateSlotSummary();
            if (t.type === 'checkbox') {
                var qty = t.closest('.sz-service-row').querySelector('.sz-qty');
                qty.disabled = !t.checked;
            }
            updateTotal();
        });

        // Feedback khi submit (UX guideline: Submit Feedback)
        form.addEventListener('submit', function (e) {
            if (!form.querySelector('.sz-slot-radio:checked')) {
                e.preventDefault();
                alert('Vui lòng chọn một khung giờ trống.');
                return;
            }
            var btn = form.querySelector('button[type="submit"]');
            btn.disabled = true;
            btn.textContent = 'Đang xử lý…';
        });

        updateCourtSummary();
        updateSlotSummary();
        updateTotal();
    })();
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
