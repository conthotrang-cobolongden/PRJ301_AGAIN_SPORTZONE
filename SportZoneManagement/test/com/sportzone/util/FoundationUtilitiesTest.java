package com.sportzone.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Kiem tra contract cac utility nen tang cua Phase 1.
 */
public final class FoundationUtilitiesTest {

    private FoundationUtilitiesTest() {
    }

    public static void main(String[] args) {
        require("e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7"
                .equals(PasswordUtil.sha256Hex("Admin@123")), "Admin seed hash must match");
        require(PasswordUtil.matches("User@123",
                "3e7c19576488862816f13b512cacf3e4ba97dd97243ea0bd6a2ad1642d86ba72"),
                "User seed hash must match");

        require("account".equals(AppConstants.SESSION_ACCOUNT), "Session account key must match JSP");
        require("Admin".equals(AppConstants.ROLE_ADMIN), "Admin role must match seed");
        require("/WEB-INF/views/home/index.jsp".equals(Views.HOME), "Home view path must be centralized");

        require(Validation.trimToNull("  ") == null, "Blank input must normalize to null");
        require("abc".equals(Validation.trimToNull(" abc ")), "Input must be trimmed");
        require(Validation.isEmail("user@sportzone.local"), "Valid email must pass");
        require(!Validation.isEmail("invalid"), "Invalid email must fail");
        require(Validation.isVietnamesePhone("0901234567"), "Valid VN phone must pass");
        require(Validation.isVietnamesePhone("+84901234567"), "Valid +84 phone must pass");
        require(!Validation.isVietnamesePhone("123"), "Invalid phone must fail");

        LocalDateTime combined = DateUtil.combine(LocalDate.of(2026, 8, 18), LocalTime.of(6, 0));
        require(combined.equals(LocalDateTime.of(2026, 8, 18, 6, 0)), "Date and time must combine");
        require(DateUtil.isWithinInclusive(LocalDate.of(2026, 8, 18),
                LocalDate.of(2026, 8, 18), LocalDate.of(2026, 8, 24)), "Date range must include boundary");

        System.out.println("FoundationUtilitiesTest PASSED");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
