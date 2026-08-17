package com.sportzone.service;

import com.sportzone.dao.AccountDAO;
import com.sportzone.model.Account;
import com.sportzone.util.AppConstants;
import com.sportzone.util.PasswordUtil;
import com.sportzone.util.Validation;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

public class AuthService {

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 64;
    private static final int FULL_NAME_MAX_LENGTH = 100;
    private static final int EMAIL_MAX_LENGTH = 100;
    private static final int PHONE_MAX_LENGTH = 20;

    private final AccountDAO accountDAO = new AccountDAO();

    /**
     * Trả về account nếu đăng nhập hợp lệ, ngược lại trả null.
     * Cố tình trả null chung cho cả hai trường hợp sai email và sai mật khẩu
     * để servlet không lộ thông tin email có tồn tại hay không.
     */
    public Account login(String email, String password) throws SQLException {
        String normalizedEmail = Validation.trimToNull(email);
        if (normalizedEmail == null || Validation.isBlank(password)) {
            return null;
        }
        Account account = accountDAO.findByEmail(normalizedEmail);
        if (account == null || !PasswordUtil.matches(password, account.getPasswordHash())) {
            return null;
        }
        accountDAO.updateLastLogin(account.getAccountId());
        account.setLastLoginAt(new Timestamp(System.currentTimeMillis()));
        return account;
    }

    public RegisterResult register(String fullName, String email, String phone,
                                   String password, String confirmPassword) throws SQLException {
        RegisterResult result = new RegisterResult();
        Map<String, String> errors = result.getFieldErrors();

        String trimmedName = Validation.trimToNull(fullName);
        String trimmedEmail = normalizeEmail(email);
        String compactPhone = normalizePhone(phone);

        if (trimmedName == null) {
            errors.put("fullName", "Họ tên không được để trống");
        } else if (!Validation.maxLength(trimmedName, FULL_NAME_MAX_LENGTH)) {
            errors.put("fullName", "Họ tên tối đa " + FULL_NAME_MAX_LENGTH + " ký tự");
        }

        if (trimmedEmail == null) {
            errors.put("email", "Email không được để trống");
        } else if (!Validation.isEmail(trimmedEmail)) {
            errors.put("email", "Email không đúng định dạng");
        } else if (!Validation.maxLength(trimmedEmail, EMAIL_MAX_LENGTH)) {
            errors.put("email", "Email tối đa " + EMAIL_MAX_LENGTH + " ký tự");
        } else if (accountDAO.existsByEmail(trimmedEmail)) {
            errors.put("email", "Email đã được sử dụng");
        }

        if (compactPhone == null) {
            errors.put("phone", "Số điện thoại không được để trống");
        } else if (!Validation.isPhoneVn(compactPhone)) {
            errors.put("phone", "Số điện thoại không đúng định dạng Việt Nam");
        } else if (!Validation.maxLength(compactPhone, PHONE_MAX_LENGTH)) {
            errors.put("phone", "Số điện thoại tối đa " + PHONE_MAX_LENGTH + " ký tự");
        } else if (accountDAO.existsByPhone(compactPhone)) {
            errors.put("phone", "Số điện thoại đã được sử dụng");
        }

        if (Validation.isBlank(password)) {
            errors.put("password", "Mật khẩu không được để trống");
        } else if (password.length() < PASSWORD_MIN_LENGTH) {
            errors.put("password", "Mật khẩu tối thiểu " + PASSWORD_MIN_LENGTH + " ký tự");
        } else if (password.length() > PASSWORD_MAX_LENGTH) {
            errors.put("password", "Mật khẩu tối đa " + PASSWORD_MAX_LENGTH + " ký tự");
        }

        if (Validation.isBlank(confirmPassword)) {
            errors.put("confirmPassword", "Nhập lại mật khẩu không được để trống");
        } else if (!confirmPassword.equals(password)) {
            errors.put("confirmPassword", "Nhập lại mật khẩu không khớp");
        }

        if (!errors.isEmpty()) {
            return result;
        }

        Account account = new Account();
        account.setRoleId(AppConstants.ROLE_ID_USER);
        account.setEmail(trimmedEmail);
        account.setPhone(compactPhone);
        account.setPasswordHash(PasswordUtil.sha256Hex(password));
        account.setFullName(trimmedName);
        account.setStatus(Account.STATUS_ACTIVE);

        int accountId = accountDAO.insert(account);
        // Fetch lại để lấy roleName từ JOIN Roles — insert không trả về roleName
        Account created = accountDAO.findById(accountId);
        result.setAccount(created != null ? created : account);
        return result;
    }

    private String normalizeEmail(String email) {
        String trimmed = Validation.trimToNull(email);
        return trimmed == null ? null : trimmed.toLowerCase();
    }

    private String normalizePhone(String phone) {
        String trimmed = Validation.trimToNull(phone);
        return trimmed == null ? null : trimmed.replace(" ", "").replace("-", "");
    }

    public static class RegisterResult {

        private final Map<String, String> fieldErrors = new LinkedHashMap<>();
        private Account account;

        public boolean isSuccess() {
            return fieldErrors.isEmpty();
        }

        public Map<String, String> getFieldErrors() {
            return fieldErrors;
        }

        public Account getAccount() {
            return account;
        }

        private void setAccount(Account account) {
            this.account = account;
        }
    }
}
