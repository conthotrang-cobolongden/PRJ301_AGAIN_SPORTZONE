<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="SportZone — Tổ Hợp Thể Thao Tennis &amp; Pickleball Cao Cấp" scope="request"/>
<c:set var="pageCss" value="home-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-home" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- HERO SECTION -->
<section class="hero" id="home">
  <div class="shell hero-grid">
    <div data-reveal>
      <div class="kicker"><span class="dot"></span> SportZone · Tổ Hợp Tennis &amp; Pickleball Cao Cấp</div>
      <h1>
        Khung Giờ Vàng.<br>
        <span class="accent-text">Sẵn Sàng Ra Sân.</span>
      </h1>
      <p class="lead">
        Hệ thống ${tennisCourtCount + pickleballCourtCount} sân Tennis &amp; Pickleball tiêu chuẩn quốc tế tại TP. Hồ Chí Minh. Tra cứu lịch trống theo thời gian thực, giữ chỗ tức thì trong 60 giây và tận hưởng không gian thể thao đỉnh cao.
      </p>
      
      <div class="hero-actions">
        <a class="btn green" href="${ctx}/MainController?action=courts">Xem danh sách sân <span>→</span></a>
        <a class="btn" href="#booking">Quy trình đặt sân</a>
      </div>
      
      <div class="trust-row">
        <span><i>✓</i> <b>${tennisCourtCount} Sân Tennis</b> Acrylic Chuẩn Giải Đấu</span>
        <span><i>✓</i> <b>${pickleballCourtCount} Sân Pickleball</b> Đèn LED Chống Lóa</span>
        <span><i>✓</i> Mở Cửa 06:00 – 21:00 Hằng Ngày</span>
      </div>
    </div>

    <div class="hero-visual" data-reveal="right">
      <div class="photo-main">
        <img src="https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?auto=format&amp;fit=crop&amp;w=1920&amp;q=88" alt="Sân Tennis cao cấp tại SportZone" />
        <div class="court-coordinate">ZONE A · TENNIS COMPLEX</div>
      </div>
      
      <!-- Dynamic Stat Floating Card -->
      <div class="hero-stat-floating">
        <div class="stat-head">
          <span class="sz-badge sz-badge-gold">TỔ HỢP THI ĐẤU</span>
          <span class="sz-badge sz-badge-green">LIVE STATUS</span>
        </div>
        <div class="stat-num">${tennisCourtCount + pickleballCourtCount} Sân Active</div>
        <p>${tennisCourtCount} Sân Tennis Zone A + ${pickleballCourtCount} Sân Pickleball Zone B sẵn sàng phục vụ hôm nay.</p>
      </div>
    </div>
  </div>
  <div class="scroll-cue"><span>Cuộn để khám phá</span><i></i></div>
</section>

<!-- TICKER BAR -->
<div class="ticker" aria-hidden="true">
  <div class="ticker-track">
    <span>Tennis Zone A · ${tennisCourtCount} Sân</span>
    <span>Pickleball Zone B · ${pickleballCourtCount} Sân</span>
    <span>Mặt Sân Acrylic Chuẩn Quốc Tế</span>
    <span>Hệ Thống Đèn LED Chống Lóa 500 Lux</span>
    <span>Giữ Chỗ Thời Gian Thực 100%</span>
    <span>Mở Cửa 06:00 – 21:00</span>
    
    <span>Tennis Zone A · ${tennisCourtCount} Sân</span>
    <span>Pickleball Zone B · ${pickleballCourtCount} Sân</span>
    <span>Mặt Sân Acrylic Chuẩn Quốc Tế</span>
    <span>Hệ Thống Đèn LED Chống Lóa 500 Lux</span>
    <span>Giữ Chỗ Thời Gian Thực 100%</span>
    <span>Mở Cửa 06:00 – 21:00</span>
  </div>
</div>

<!-- ========================================================
     GRAND KINETIC STADIUM ENTRANCE GATE
     ======================================================== -->
<section class="gateway" id="gateway" aria-label="Chuyển cảnh vào khu sân thể thao">
  <div class="gateway-stage">
    <div class="gateway-scene" id="gatewayScene">
      <img id="gatewayImage" src="https://images.unsplash.com/photo-1554068865-24cecd4e34b8?auto=format&amp;fit=crop&amp;w=1920&amp;q=90" alt="Toàn cảnh cụm sân SportZone" />
    </div>
    
    <div class="gateway-copy">
      <div class="gateway-copy-inner" id="gatewayCopy">
        <div class="kicker"><span class="dot"></span> Không Gian Thể Thao Đỉnh Cao</div>
        <h2>Bước Vào Trận Đấu Của Bạn.</h2>
        <p>Zone A Tennis bứt phá tốc độ, Zone B Pickleball sôi động gắn kết. Đặt sân trực tuyến tiện lợi — sẵn sàng bùng nổ trên từng đường bóng.</p>
      </div>
    </div>
    
    <!-- LEFT DOOR: ZONE A TENNIS ARENA -->
    <div class="door left" id="doorLeft">
      <div class="door-center-laser"></div>
      
      <div class="door-texture">
        <div class="door-top-light"></div>
        
        <div class="door-badge-plate">
          <div class="door-crest">★ SPORTZONE GRAND ARENA ★</div>
          <div class="door-main-title">ZONE A</div>
          <div class="door-subtitle">TENNIS COMPLEX · ${tennisCourtCount} SÂN ACRYLIC</div>
          
          <!-- Laser Tennis Court Wireframe -->
          <div class="door-court-svg">
            <svg viewBox="0 0 220 120" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="10" y="10" width="200" height="100" rx="3" stroke="#caa54b" stroke-width="1.8" stroke-opacity="0.9"/>
              <line x1="10" y1="24" x2="210" y2="24" stroke="#caa54b" stroke-width="1.2" stroke-opacity="0.6"/>
              <line x1="10" y1="96" x2="210" y2="96" stroke="#caa54b" stroke-width="1.2" stroke-opacity="0.6"/>
              <line x1="55" y1="24" x2="55" y2="96" stroke="#caa54b" stroke-width="1.2" stroke-opacity="0.7"/>
              <line x1="165" y1="24" x2="165" y2="96" stroke="#caa54b" stroke-width="1.2" stroke-opacity="0.7"/>
              <line x1="55" y1="60" x2="165" y2="60" stroke="#caa54b" stroke-width="1.2" stroke-opacity="0.7"/>
              <line x1="110" y1="6" x2="110" y2="114" stroke="#ffffff" stroke-width="2.5" stroke-dasharray="4 3"/>
              <circle cx="110" cy="60" r="4" fill="#caa54b"/>
            </svg>
          </div>
          
          <div class="door-status-pill">
            <span class="door-status-dot"></span> SẴN SÀNG THI ĐẤU
          </div>
        </div>
        
        <div style="font-size:11px; font-weight:800; letter-spacing:.16em; color:#859a8e; text-align:center;">
          ITF STANDARD · NIGHT LIGHTING 500 LUX
        </div>
      </div>
      
      <!-- Titanium Center Vault Lock (Left Half) -->
      <div class="vault-handle">
        <div class="vault-handle-inner">✦</div>
      </div>
    </div>
    
    <!-- RIGHT DOOR: ZONE B PICKLEBALL COMPLEX -->
    <div class="door right" id="doorRight">
      <div class="door-center-laser"></div>
      
      <div class="door-texture">
        <div class="door-top-light"></div>
        
        <div class="door-badge-plate">
          <div class="door-crest">★ SPORTZONE GRAND ARENA ★</div>
          <div class="door-main-title">ZONE B</div>
          <div class="door-subtitle">PICKLEBALL · ${pickleballCourtCount} SÂN ĐA NĂNG</div>
          
          <!-- Laser Pickleball Court Wireframe -->
          <div class="door-court-svg">
            <svg viewBox="0 0 220 120" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="10" y="10" width="200" height="100" rx="3" stroke="#caa54b" stroke-width="1.8" stroke-opacity="0.9"/>
              <rect x="70" y="10" width="80" height="100" fill="rgba(202,165,75,0.12)" stroke="#caa54b" stroke-width="1.4" stroke-opacity="0.8"/>
              <line x1="10" y1="60" x2="70" y2="60" stroke="#caa54b" stroke-width="1.2" stroke-opacity="0.7"/>
              <line x1="150" y1="60" x2="210" y2="60" stroke="#caa54b" stroke-width="1.2" stroke-opacity="0.7"/>
              <line x1="110" y1="6" x2="110" y2="114" stroke="#ffffff" stroke-width="2.5" stroke-dasharray="4 3"/>
              <circle cx="110" cy="60" r="4" fill="#caa54b"/>
            </svg>
          </div>
          
          <div class="door-status-pill">
            <span class="door-status-dot"></span> SẴN SÀNG THI ĐẤU
          </div>
        </div>
        
        <div style="font-size:11px; font-weight:800; letter-spacing:.16em; color:#859a8e; text-align:center;">
          DYNAMIC SURFACE · OUTDOOR FLOODLIGHT
        </div>
      </div>
      
      <!-- Titanium Center Vault Lock (Right Half) -->
      <div class="vault-handle">
        <div class="vault-handle-inner">✦</div>
      </div>
    </div>
    
    <div class="gateway-progress"><i id="gatewayBar"></i></div>
  </div>
</section>

<!-- COURTS CATALOG SECTION (VISUAL VIEW & PRICING SHOWCASE) -->
<section class="section courts" id="courts">
  <div class="shell">
    <div class="section-head" data-reveal>
      <div>
        <div class="eyebrow"><span class="dot"></span> Không Gian &amp; Bảng Giá</div>
        <h2>Hệ Thống ${tennisCourtCount + pickleballCourtCount} Sân Tiêu Chuẩn Quốc Tế.</h2>
      </div>
      <p>Mặt sân Acrylic giảm chấn thương, hệ thống chiếu sáng tiêu chuẩn giải đấu và dịch vụ tiện ích tại chỗ giúp bạn hoàn toàn an tâm thi đấu.</p>
    </div>
    
    <!-- 2 Main Zone Banners -->
    <div class="zone-banners">
      <!-- Zone A Banner -->
      <div class="zone-card">
        <img src="https://t4.ftcdn.net/jpg/09/88/84/25/360_F_988842500_3QxDF8zdI7Tywxak4eZJYeD1piKokUrE.jpg" alt="Tennis Zone A" />
        <div class="zone-pill"><span class="sz-badge sz-badge-white">ZONE A · TENNIS COMPLEX</span></div>
        <div class="zone-content">
          <h3>${tennisCourtCount} Sân Tennis Acrylic</h3>
          <p>Mặt sân cứng 8 lớp chuẩn ITF, trang bị hệ thống dàn đèn LED 500 Lux thi đấu ban đêm.</p>
          <a class="btn gold sm" href="${ctx}/MainController?action=courts&amp;type=tennis">Xem ${tennisCourtCount} sân Tennis ↓</a>
        </div>
      </div>

      <!-- Zone B Banner -->
      <div class="zone-card">
        <img src="https://img.magnific.com/premium-photo/pickleball-court-sports-court-with-net-3d-rendering_747516-496.jpg" alt="Pickleball Zone B" />
        <div class="zone-pill"><span class="sz-badge sz-badge-white">ZONE B · PICKLEBALL COMPLEX</span></div>
        <div class="zone-content">
          <h3>${pickleballCourtCount} Sân Pickleball Đa Năng</h3>
          <p>Môn thể thao hiện đại, mặt sân êm ái chống trơn trượt, phù hợp giao lưu và rèn luyện thể lực.</p>
          <a class="btn gold sm" href="${ctx}/MainController?action=courts&amp;type=pickleball">Xem ${pickleballCourtCount} sân Pickleball ↓</a>
        </div>
      </div>
    </div>

    <!-- Interactive Filter Bar -->
    <div class="courts-filter-bar">
      <div class="filter-tabs">
        <button class="filter-tab active" id="tabAll" type="button" onclick="filterCourts('all')">Tất cả <span class="count">${tennisCourtCount + pickleballCourtCount}</span></button>
        <button class="filter-tab" id="tabTennis" type="button" onclick="filterCourts('tennis')">Tennis Zone A <span class="count">${tennisCourtCount}</span></button>
        <button class="filter-tab" id="tabPickleball" type="button" onclick="filterCourts('pickleball')">Pickleball Zone B <span class="count">${pickleballCourtCount}</span></button>
      </div>
      <div class="d-flex align-items-center gap-2" style="font-size:13px; font-weight:700; color:var(--muted)">
        <span style="color:var(--green)">●</span> 09 Sân Sẵn Sàng &nbsp;|&nbsp; <span style="color:#b91c1c">●</span> 01 Sân Bảo Trì
      </div>
    </div>

    <!-- 10 DETAILED VIEW-ONLY COURTS LIST WITH PRICING & VISUALS -->
    <div class="court-grid" id="courtGrid">
      
      <!-- TENNIS 1 -->
      <article class="court-item-card" data-type="tennis">
        <div class="court-thumb">
          <img src="https://t4.ftcdn.net/jpg/09/88/84/25/360_F_988842500_3QxDF8zdI7Tywxak4eZJYeD1piKokUrE.jpg" alt="Tennis Court 1" />
          <div class="thumb-type"><span class="sz-badge sz-badge-tennis">Tennis</span></div>
          <div class="thumb-status"><span class="sz-badge sz-badge-available">Trống</span></div>
        </div>
        <div class="court-item-body">
          <h4>Tennis Court 1</h4>
          <div class="court-loc">📍 Zone A · Sân số 1</div>
          <div class="court-specs">
            <span>Acrylic 8 Lớp</span>
            <span>Ngoài Trời</span>
            <span>Khung Giờ 06-21h</span>
          </div>
          <div class="court-item-footer">
            <div class="price">150.000 đ <span>/ giờ</span></div>
            <a class="btn green sm" href="${ctx}/MainController?action=court&amp;id=1">Xem chi tiết →</a>
          </div>
        </div>
      </article>

      <!-- TENNIS 2 -->
      <article class="court-item-card" data-type="tennis">
        <div class="court-thumb">
          <img src="https://t4.ftcdn.net/jpg/09/88/84/25/360_F_988842500_3QxDF8zdI7Tywxak4eZJYeD1piKokUrE.jpg" alt="Tennis Court 2" />
          <div class="thumb-type"><span class="sz-badge sz-badge-tennis">Tennis</span></div>
          <div class="thumb-status"><span class="sz-badge sz-badge-available">Trống</span></div>
        </div>
        <div class="court-item-body">
          <h4>Tennis Court 2</h4>
          <div class="court-loc">📍 Zone A · Sân số 2</div>
          <div class="court-specs">
            <span>Acrylic 8 Lớp</span>
            <span>Độ Nảy Chuẩn</span>
            <span>Khung Giờ 06-21h</span>
          </div>
          <div class="court-item-footer">
            <div class="price">150.000 đ <span>/ giờ</span></div>
            <a class="btn green sm" href="${ctx}/MainController?action=court&amp;id=2">Xem chi tiết →</a>
          </div>
        </div>
      </article>

      <!-- TENNIS 3 -->
      <article class="court-item-card" data-type="tennis">
        <div class="court-thumb">
          <img src="https://t4.ftcdn.net/jpg/09/88/84/25/360_F_988842500_3QxDF8zdI7Tywxak4eZJYeD1piKokUrE.jpg" alt="Tennis Court 3" />
          <div class="thumb-type"><span class="sz-badge sz-badge-tennis">Tennis</span></div>
          <div class="thumb-status"><span class="sz-badge sz-badge-available">Trống</span></div>
        </div>
        <div class="court-item-body">
          <h4>Tennis Court 3</h4>
          <div class="court-loc">📍 Zone A · Sân số 3</div>
          <div class="court-specs">
            <span>Hard Court</span>
            <span>Tiêu Chuẩn ITF</span>
            <span>Khung Giờ 06-21h</span>
          </div>
          <div class="court-item-footer">
            <div class="price">150.000 đ <span>/ giờ</span></div>
            <a class="btn green sm" href="${ctx}/MainController?action=court&amp;id=3">Xem chi tiết →</a>
          </div>
        </div>
      </article>

      <!-- TENNIS 4 -->
      <article class="court-item-card" data-type="tennis">
        <div class="court-thumb">
          <img src="https://t4.ftcdn.net/jpg/09/88/84/25/360_F_988842500_3QxDF8zdI7Tywxak4eZJYeD1piKokUrE.jpg" alt="Tennis Court 4" />
          <div class="thumb-type"><span class="sz-badge sz-badge-tennis">Tennis</span></div>
          <div class="thumb-status"><span class="sz-badge sz-badge-available">Trống</span></div>
        </div>
        <div class="court-item-body">
          <h4>Tennis Court 4</h4>
          <div class="court-loc">📍 Zone A · Sân số 4 (Đèn Đêm)</div>
          <div class="court-specs">
            <span>Dàn Đèn LED 500 Lux</span>
            <span>Chống Lóa</span>
            <span>Khung Giờ 06-21h</span>
          </div>
          <div class="court-item-footer">
            <div class="price">180.000 đ <span>/ giờ</span></div>
            <a class="btn green sm" href="${ctx}/MainController?action=court&amp;id=4">Xem chi tiết →</a>
          </div>
        </div>
      </article>

      <!-- TENNIS 5 (MAINTENANCE) -->
      <article class="court-item-card" data-type="tennis">
        <div class="court-thumb">
          <img src="https://t4.ftcdn.net/jpg/09/88/84/25/360_F_988842500_3QxDF8zdI7Tywxak4eZJYeD1piKokUrE.jpg" alt="Tennis Court 5" style="filter:grayscale(.7)" />
          <div class="thumb-type"><span class="sz-badge sz-badge-tennis">Tennis</span></div>
          <div class="thumb-status"><span class="sz-badge sz-badge-maintenance">Bảo Trì</span></div>
        </div>
        <div class="court-item-body">
          <h4>Tennis Court 5</h4>
          <div class="court-loc">📍 Zone A · Sân số 5 (VIP)</div>
          <div class="court-specs">
            <span>Bảo Trì Mặt Sân</span>
            <span>Tạm Khóa Slot</span>
          </div>
          <div class="court-item-footer">
            <div class="price">150.000 đ <span>/ giờ</span></div>
            <a class="btn sm" href="${ctx}/MainController?action=court&amp;id=5" style="opacity:.7">Xem chi tiết →</a>
          </div>
        </div>
      </article>

      <!-- PICKLEBALL 1 -->
      <article class="court-item-card" data-type="pickleball">
        <div class="court-thumb">
          <img src="https://img.magnific.com/premium-photo/pickleball-court-sports-court-with-net-3d-rendering_747516-496.jpg" alt="Pickleball Court 1" />
          <div class="thumb-type"><span class="sz-badge sz-badge-pickle">Pickleball</span></div>
          <div class="thumb-status"><span class="sz-badge sz-badge-available">Trống</span></div>
        </div>
        <div class="court-item-body">
          <h4>Pickleball Court 1</h4>
          <div class="court-loc">📍 Zone B · Sân số 1</div>
          <div class="court-specs">
            <span>Sân Đa Năng</span>
            <span>Chống Trơn Trượt</span>
            <span>Khung Giờ 06-21h</span>
          </div>
          <div class="court-item-footer">
            <div class="price">120.000 đ <span>/ giờ</span></div>
            <a class="btn green sm" href="${ctx}/MainController?action=court&amp;id=6">Xem chi tiết →</a>
          </div>
        </div>
      </article>

      <!-- PICKLEBALL 2 -->
      <article class="court-item-card" data-type="pickleball">
        <div class="court-thumb">
          <img src="https://img.magnific.com/premium-photo/pickleball-court-sports-court-with-net-3d-rendering_747516-496.jpg" alt="Pickleball Court 2" />
          <div class="thumb-type"><span class="sz-badge sz-badge-pickle">Pickleball</span></div>
          <div class="thumb-status"><span class="sz-badge sz-badge-available">Trống</span></div>
        </div>
        <div class="court-item-body">
          <h4>Pickleball Court 2</h4>
          <div class="court-loc">📍 Zone B · Sân số 2</div>
          <div class="court-specs">
            <span>Mặt Sân Êm</span>
            <span>Vạch Sơn Sắc Nét</span>
            <span>Khung Giờ 06-21h</span>
          </div>
          <div class="court-item-footer">
            <div class="price">120.000 đ <span>/ giờ</span></div>
            <a class="btn green sm" href="${ctx}/MainController?action=court&amp;id=7">Xem chi tiết →</a>
          </div>
        </div>
      </article>

      <!-- PICKLEBALL 3 -->
      <article class="court-item-card" data-type="pickleball">
        <div class="court-thumb">
          <img src="https://img.magnific.com/premium-photo/pickleball-court-sports-court-with-net-3d-rendering_747516-496.jpg" alt="Pickleball Court 3" />
          <div class="thumb-type"><span class="sz-badge sz-badge-pickle">Pickleball</span></div>
          <div class="thumb-status"><span class="sz-badge sz-badge-available">Trống</span></div>
        </div>
        <div class="court-item-body">
          <h4>Pickleball Court 3</h4>
          <div class="court-loc">📍 Zone B · Sân số 3</div>
          <div class="court-specs">
            <span>Không Gian Thoáng</span>
            <span>Lưới Tiêu Chuẩn</span>
            <span>Khung Giờ 06-21h</span>
          </div>
          <div class="court-item-footer">
            <div class="price">120.000 đ <span>/ giờ</span></div>
            <a class="btn green sm" href="${ctx}/MainController?action=court&amp;id=8">Xem chi tiết →</a>
          </div>
        </div>
      </article>

      <!-- PICKLEBALL 4 -->
      <article class="court-item-card" data-type="pickleball">
        <div class="court-thumb">
          <img src="https://img.magnific.com/premium-photo/pickleball-court-sports-court-with-net-3d-rendering_747516-496.jpg" alt="Pickleball Court 4" />
          <div class="thumb-type"><span class="sz-badge sz-badge-pickle">Pickleball</span></div>
          <div class="thumb-status"><span class="sz-badge sz-badge-available">Trống</span></div>
        </div>
        <div class="court-item-body">
          <h4>Pickleball Court 4</h4>
          <div class="court-loc">📍 Zone B · Sân số 4 (Đèn Đêm)</div>
          <div class="court-specs">
            <span>Dàn Đèn LED 400 Lux</span>
            <span>Chơi Tối Tiện Lợi</span>
            <span>Khung Giờ 06-21h</span>
          </div>
          <div class="court-item-footer">
            <div class="price">140.000 đ <span>/ giờ</span></div>
            <a class="btn green sm" href="${ctx}/MainController?action=court&amp;id=9">Xem chi tiết →</a>
          </div>
        </div>
      </article>

      <!-- PICKLEBALL 5 -->
      <article class="court-item-card" data-type="pickleball">
        <div class="court-thumb">
          <img src="https://img.magnific.com/premium-photo/pickleball-court-sports-court-with-net-3d-rendering_747516-496.jpg" alt="Pickleball Court 5" />
          <div class="thumb-type"><span class="sz-badge sz-badge-pickle">Pickleball</span></div>
          <div class="thumb-status"><span class="sz-badge sz-badge-available">Trống</span></div>
        </div>
        <div class="court-item-body">
          <h4>Pickleball Court 5</h4>
          <div class="court-loc">📍 Zone B · Sân số 5</div>
          <div class="court-specs">
            <span>Mặt Sân Mới 100%</span>
            <span>Rộng Rãi</span>
            <span>Khung Giờ 06-21h</span>
          </div>
          <div class="court-item-footer">
            <div class="price">120.000 đ <span>/ giờ</span></div>
            <a class="btn green sm" href="${ctx}/MainController?action=court&amp;id=10">Xem chi tiết →</a>
          </div>
        </div>
      </article>

    </div>
  </div>
</section>

<!-- BENEFITS SECTION -->
<section class="section benefits">
  <div class="shell">
    <div class="section-head" data-reveal>
      <div>
        <div class="eyebrow"><span class="dot"></span> Trải Nghiệm Đặt Sân Thông Minh</div>
        <h2>Giữ Chỗ Nhanh Chóng · Tối Đa Thời Gian Thi Đấu.</h2>
      </div>
      <p>Loại bỏ hoàn toàn rủi ro trùng lịch hay chờ đợi xác nhận thủ công. Mọi thao tác đều được tự động hóa chính xác trên hệ thống.</p>
    </div>
    
    <div class="benefit-grid" data-reveal>
      <article class="benefit">
        <div class="n">01</div>
        <h3>Lịch Trống Thời Gian Thực</h3>
        <p>Tra cứu trực quan khung giờ còn trống trong 7 ngày liên tiếp theo từng sân cụ thể.</p>
      </article>
      
      <article class="benefit">
        <div class="n">02</div>
        <h3>Giữ Chỗ Tức Thì 60s</h3>
        <p>Xác nhận giữ sân ngay lập tức trên hệ thống với thuật toán chống xung đột lịch 100%.</p>
      </article>
      
      <article class="benefit">
        <div class="n">03</div>
        <h3>Dịch Vụ Phụ Kiện Tại Chỗ</h3>
        <p>Dễ dàng đặt thêm vợt thi đấu cao cấp, bóng nỉ chuẩn giải và nước giải khát theo nhu cầu.</p>
      </article>
      
      <article class="benefit">
        <div class="n">04</div>
        <h3>Thanh Toán Linh Hoạt</h3>
        <p>Linh hoạt thanh toán tiền mặt hoặc chuyển khoản QR tiện lợi tại quầy lễ tân khi ra sân.</p>
      </article>
    </div>
  </div>
</section>

<!-- PROCESS / HOW TO BOOK -->
<section class="section" id="booking">
  <div class="shell process-wrap">
    <div class="process-image" data-reveal>
      <img src="https://images.unsplash.com/photo-1560012057-4372e14c5085?auto=format&amp;fit=crop&amp;w=1600&amp;q=88" alt="Phụ kiện và bóng tennis tại SportZone" />
    </div>
    
    <div data-reveal="right">
      <div class="eyebrow"><span class="dot"></span> Quy Trình 4 Bước</div>
      <h2 style="font-size:clamp(36px,4.2vw,56px);line-height:1.08">Từ Tra Cứu Đến Ra Sân Chỉ Trong 4 Bước.</h2>
      
      <div class="steps">
        <div class="step">
          <div class="num">01</div>
          <div>
            <h3>Chọn Môn Thể Thao</h3>
            <p>Lựa chọn Tennis Zone A hoặc Pickleball Zone B tùy theo sở thích và số lượng người chơi.</p>
          </div>
        </div>
        
        <div class="step">
          <div class="num">02</div>
          <div>
            <h3>Chọn Ngày &amp; Khung Giờ Vàng</h3>
            <p>Xem bảng ma trận slot giờ trống và chọn thời gian thi đấu phù hợp nhất với bạn.</p>
          </div>
        </div>
        
        <div class="step">
          <div class="num">03</div>
          <div>
            <h3>Xác Nhận &amp; Nhận Mã Đặt Sân</h3>
            <p>Hệ thống lập tức khóa khung giờ của bạn và tạo đơn đặt sân để bạn theo dõi trong hồ sơ.</p>
          </div>
        </div>
        
        <div class="step">
          <div class="num">04</div>
          <div>
            <h3>Đến Sân &amp; Bắt Đầu Trận Đấu</h3>
            <p>Xuất trình mã đặt sân tại quầy lễ tân SportZone TP.HCM và tự tin bước vào trận đấu.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</section>

<!-- CALL TO ACTION -->
<section class="cta">
  <div class="shell">
    <div class="cta-box" data-reveal>
      <div>
        <div class="eyebrow" style="color:var(--gold-light)"><span class="dot" style="background:var(--gold-light)"></span> Sẵn Sàng Cho Trận Đấu Hôm Nay?</div>
        <h2>Giữ Khung Giờ Đẹp Ngay Trước Khi Kín Lịch!</h2>
        <p>Hệ thống cập nhật liên tục các khung giờ sáng sớm và chiều tối. Xem sân và giữ chỗ ngay.</p>
      </div>
      <a class="btn gold" href="${ctx}/MainController?action=courts" style="font-size:15px; padding:15px 32px;">Khám phá tất cả sân →</a>
    </div>
  </div>
</section>

<!-- INLINE SCRIPT FOR HOME KINETIC GATE & FILTERS -->
<script>
  // Interactive Court Filter Tabs (All / Tennis / Pickleball)
  function filterCourts(type){
    var tabs = document.querySelectorAll('.filter-tab');
    tabs.forEach(function(t){ t.classList.remove('active'); });
    
    if(type === 'tennis') {
      var tTab = document.getElementById('tabTennis');
      if(tTab) tTab.classList.add('active');
    } else if(type === 'pickleball') {
      var pTab = document.getElementById('tabPickleball');
      if(pTab) pTab.classList.add('active');
    } else {
      var aTab = document.getElementById('tabAll');
      if(aTab) aTab.classList.add('active');
    }

    var cards = document.querySelectorAll('.court-item-card');
    cards.forEach(function(card){
      var ctype = card.getAttribute('data-type');
      if(type === 'all' || ctype === type){
        card.style.display = 'flex';
      } else {
        card.style.display = 'none';
      }
    });
  }

  // High-Precision Physics Inertial LERP Engine for Stadium Gate
  (function(){
    var clamp = function(n,min,max){ return Math.max(min,Math.min(max,n)); };
    var gateway = document.getElementById('gateway');
    var left = document.getElementById('doorLeft');
    var right = document.getElementById('doorRight');
    var copy = document.getElementById('gatewayCopy');
    var image = document.getElementById('gatewayImage');
    var bar = document.getElementById('gatewayBar');

    if(!gateway || !left || !right) return;

    var targetProgress = 0;
    var currentProgress = 0;
    var isLoopRunning = false;

    function smoothstep(min, max, value) {
      var x = Math.max(0, Math.min(1, (value - min) / (max - min)));
      return x * x * (3 - 2 * x);
    }

    function calculateTargetProgress() {
      var rect = gateway.getBoundingClientRect();
      var scrollable = gateway.offsetHeight - window.innerHeight;
      if (scrollable <= 0) return;
      targetProgress = clamp((-rect.top) / scrollable, 0, 1);
      
      if (!isLoopRunning) {
        isLoopRunning = true;
        requestAnimationFrame(renderPhysicsLoop);
      }
    }

    function renderPhysicsLoop() {
      if (window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
        isLoopRunning = false;
        return;
      }

      var diff = targetProgress - currentProgress;
      currentProgress += diff * 0.082;
      var p = currentProgress;

      var openEased = smoothstep(0.03, 0.72, p);
      var travel = openEased * 104;
      var swingAngle = openEased * 9.0;

      if(left) left.style.transform = 'translate3d(-' + travel + '%, 0, 0) rotateY(' + swingAngle + 'deg)';
      if(right) right.style.transform = 'translate3d(' + travel + '%, 0, 0) rotateY(-' + swingAngle + 'deg)';
      
      var doorOpacity = 1 - smoothstep(0.70, 0.96, p) * 0.35;
      if(left) left.style.opacity = doorOpacity;
      if(right) right.style.opacity = doorOpacity;

      if(copy){
        var copyEased = smoothstep(0.20, 0.58, p);
        copy.style.opacity = copyEased;
        var translateY = 42 * (1 - copyEased);
        var scale = 0.95 + (0.05 * copyEased);
        copy.style.transform = 'translate3d(0, ' + translateY + 'px, 0) scale(' + scale + ')';
      }

      if(image){
        var imgScale = 1.20 - (openEased * 0.16);
        var imgY = (p - 0.5) * -4;
        image.style.transform = 'translate3d(0, ' + imgY + '%, 0) scale(' + imgScale + ')';
      }

      if(bar){
        bar.style.width = (p * 100) + '%';
      }

      if (Math.abs(diff) > 0.0001) {
        requestAnimationFrame(renderPhysicsLoop);
      } else {
        currentProgress = targetProgress;
        isLoopRunning = false;
      }
    }

    window.addEventListener('scroll', calculateTargetProgress, { passive: true });
    window.addEventListener('resize', calculateTargetProgress);
    calculateTargetProgress();
  })();
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
