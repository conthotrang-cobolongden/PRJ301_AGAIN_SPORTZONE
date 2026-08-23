<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- U10: Tạo booking (KHÔNG sinh hóa đơn — rule 1) --%>
<c:set var="pageTitle" value="Đặt sân — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container sz-section">
    <div class="sz-pagehead">
        <div>
            <p class="sz-kicker sz-kicker-dark mb-1">SportZone</p>
            <h1>Đặt sân</h1>
            <p class="sz-lead-sm">Chọn sân, ngày, khung giờ và dịch vụ kèm theo. Thanh toán tại quầy hoặc chuyển khoản.</p>
        </div>
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

    <div class="row g-4">
        <!-- ===== FORM ===== -->
        <div class="col-lg-7">
            <!-- Bước 1+2: chọn sân & ngày — GET reload lại lưới slot -->
            <div class="sz-card mb-4">
                <form method="get" action="${ctx}/booking/create">
                    <div class="row g-3 align-items-end">
                        <div class="col-md-5">
                            <p class="sz-step-label mb-2"><span class="sz-step-num">1</span> Chọn sân</p>
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
                            <p class="sz-step-label mb-2"><span class="sz-step-num">2</span> Chọn ngày</p>
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
            <form method="post" action="${ctx}/booking/create" id="szBookingForm"
                  data-court-price="${selectedCourtPrice}" novalidate>
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                <input type="hidden" name="courtId" value="${selectedCourtId}"/>
                <input type="hidden" name="date" value="${selectedDate}"/>

                <!-- Bước 3: khung giờ -->
                <div class="sz-card mb-4">
                    <p class="sz-step-label"><span class="sz-step-num">3</span> Chọn khung giờ</p>
                    <c:choose>
                        <c:when test="${not empty slotList}">
                            <div class="sz-slot-grid">
                                <c:forEach var="s" items="${slotList}">
                                    <c:choose>
                                        <c:when test="${s.status == 'Available'}">
                                            <label class="sz-slot is-available">
                                                <input class="sz-slot-radio" type="radio" name="slotId"
                                                       value="${s.timeSlotId}"
                                                       data-start="${s.startTime}"
                                                       data-end="${s.endTime}"
                                                       ${s.timeSlotId == selectedSlotId ? 'checked' : ''}>
                                                <span class="sz-slot-time"><c:out value="${s.startTime}"/></span>
                                                <span class="sz-slot-label">Trống</span>
                                            </label>
                                        </c:when>
                                        <c:when test="${s.status == 'Booked'}">
                                            <span class="sz-slot is-booked">
                                                <span class="sz-slot-time"><c:out value="${s.startTime}"/></span>
                                                <span class="sz-slot-label">Đã đặt</span>
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="sz-slot is-closed">
                                                <span class="sz-slot-time"><c:out value="${s.startTime}"/></span>
                                                <span class="sz-slot-label">Đóng</span>
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
                    <p class="sz-step-label"><span class="sz-step-num">4</span> Dịch vụ kèm theo <span class="text-muted fw-normal">(không bắt buộc)</span></p>
                    <!-- DEEPSEEK: serviceList = dịch vụ IsActive=1; mỗi dòng data-price cho JS -->
                    <c:choose>
                        <c:when test="${not empty serviceList}">
                            <c:forEach var="sv" items="${serviceList}">
                                <div class="sz-service-row" data-price="${sv.price}">
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
                <button class="btn btn-sz btn-lg w-100" type="submit" ${sessionScope.frozen ? 'disabled' : ''}>
                    Xác nhận đặt sân
                </button>
            </form>
        </div>

        <!-- ===== TÓM TẮT ĐƠN (sticky) ===== -->
        <div class="col-lg-5">
            <div class="sz-card sz-summary-card">
                <h2 class="h5 mb-3">Tóm tắt đơn</h2>
                <div class="sz-summary-row"><span class="lbl">Sân</span><span class="val" id="szSumCourt">—</span></div>
                <div class="sz-summary-row"><span class="lbl">Ngày</span><span class="val" id="szSumDate">—</span></div>
                <div class="sz-summary-row"><span class="lbl">Khung giờ</span><span class="val" id="szSumSlot">—</span></div>
                <div class="sz-summary-row"><span class="lbl">Tiền sân</span><span class="val" id="szSumCourtPrice">0 đ</span></div>
                <div class="sz-summary-row"><span class="lbl">Dịch vụ</span><span class="val" id="szSumService">0 đ</span></div>
                <div class="sz-total-row"><span>Tổng cộng</span><span id="szSumTotal">0 đ</span></div>
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
            document.querySelectorAll('label.sz-slot').forEach(function (l) {
                l.classList.remove('is-selected');
            });
            if (checked) {
                checked.closest('label').classList.add('is-selected');
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
