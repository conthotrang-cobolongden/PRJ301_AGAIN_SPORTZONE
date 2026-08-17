package com.sportzone.service;

import com.sportzone.dao.AccountDAO;
import com.sportzone.model.Account;
import com.sportzone.util.PasswordUtil;
import com.sportzone.util.Validation;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * P4 — U5 cập nhật hồ sơ + U6 đổi mật khẩu.
 * Admin dùng chung cả hai chức năng này (sửa chính mình).
 */
public class ProfileService {

    private static final int FULL_NAME_MAX_LENGTH = 100;
    private static final int EMAIL_MAX_LENGTH = 100;
    private static final int PHONE_MAX_LENGTH = 20;
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 64;

    private final AccountDAO accountDAO = new AccountDAO();

    /**
     * U5: validate + lưu hồ sơ. Trả về account đã cập nhật nếu thành công, null nếu lỗi.
     * Lỗi theo từng field trong result.getFieldErrors() (key: name/email/phone — khớp JSP).
     */
    public ProfileResult updateProfile(int accountId, String fullName, String email, String phone)
            throws SQLException {
        ProfileResult result = new ProfileResult();
        Map<String, String> errors = result.getFieldErrors();

        String trimmedName = Validation.trimToNull(fullName);
        String trimmedEmail = normalizeEmail(email);
        String compactPhone = normalizePhone(phone);

        if (trimmedName == null) {
            errors.put("name", "Họ tên không được để trống");
        } else if (!Validation.maxLength(trimmedName, FULL_NAME_MAX_LENGTH)) {
            errors.put("name", "Họ tên tối đa " + FULL_NAME_MAX_LENGTH + " ký tự");
        }

        if (trimmedEmail == null) {
            errors.put("email", "Email không được để trống");
        } else if (!Validation.isEmail(trimmedEmail)) {
            errors.put("email", "Email không đúng định dạng");
        } else if (!Validation.maxLength(trimmedEmail, EMAIL_MAX_LENGTH)) {
            errors.put("email", "Email tối đa " + EMAIL_MAX_LENGTH + " ký tự");
        } else if (accountDAO.existsByEmailExcluding(trimmedEmail, accountId)) {
            errors.put("email", "Email đã được sử dụng bởi tài khoản khác");
        }

        if (compactPhone == null) {
            errors.put("phone", "Số điện thoại không được để trống");
        } else if (!Validation.isPhoneVn(compactPhone)) {
            errors.put("phone", "Số điện thoại không đúng định dạng Việt Nam");
        } else if (!Validation.maxLength(compactPhone, PHONE_MAX_LENGTH)) {
            errors.put("phone", "Số điện thoại tối đa " + PHONE_MAX_LENGTH + " ký tự");
        } else if (accountDAO.existsByPhoneExcluding(compactPhone, accountId)) {
            errors.put("phone", "Số điện thoại đã được sử dụng bởi tài khoản khác");
        }

        if (!errors.isEmpty()) {
            return result;
        }

        accountDAO.updateProfile(accountId, trimmedName, trimmedEmail, compactPhone);
        // Fetch lại để lấy roleName + createdAt mới nhất cho session
        result.setAccount(accountDAO.findById(accountId));
        return result;
    }

    /**
     * U6: đổi mật khẩu — bắt buộc đúng mật khẩu hiện tại, mật khẩu mới 8–64 ký tự,
     * nhập lại phải khớp. Hash SHA-256 (D3) tính tại đây, DAO chỉ nhận hash.
     */
    public ProfileResult changePassword(int accountId, String currentPassword,
                                        String newPassword, String confirmPassword) throws SQLException {
        ProfileResult result = new ProfileResult();
        Map<String, String> errors = result.getFieldErrors();

        Account account = accountDAO.findById(accountId);
        if (account == null) {
            result.setGeneralError("Tài khoản không tồn tại");
            return result;
        }

        if (Validation.isBlank(currentPassword)) {
            errors.put("currentPassword", "Nhập mật khẩu hiện tại");
        } else if (!PasswordUtil.matches(currentPassword, account.getPasswordHash())) {
            errors.put("currentPassword", "Mật khẩu hiện tại không đúng");
        }

        if (Validation.isBlank(newPassword)) {
            errors.put("newPassword", "Nhập mật khẩu mới");
        } else if (newPassword.length() < PASSWORD_MIN_LENGTH) {
            errors.put("newPassword", "Mật khẩu tối thiểu " + PASSWORD_MIN_LENGTH + " ký tự");
        } else if (newPassword.length() > PASSWORD_MAX_LENGTH) {
            errors.put("newPassword", "Mật khẩu tối đa " + PASSWORD_MAX_LENGTH + " ký tự");
        }

        if (Validation.isBlank(confirmPassword)) {
            errors.put("confirmPassword", "Nhập lại mật khẩu mới");
        } else if (!confirmPassword.equals(newPassword)) {
            errors.put("confirmPassword", "Mật khẩu nhập lại không khớp");
        }

        if (!errors.isEmpty()) {
            return result;
        }

        accountDAO.updatePasswordHash(accountId, PasswordUtil.sha256Hex(newPassword));
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

    public static class ProfileResult {

        private final Map<String, String> fieldErrors = new LinkedHashMap<>();
        private Account account;
        private String generalError;

        public boolean isSuccess() {
            return fieldErrors.isEmpty() && generalError == null;
        }

        public Map<String, String> getFieldErrors() {
            return fieldErrors;
        }

        public Account getAccount() {
            return account;
        }

        public String getGeneralError() {
            return generalError;
        }

        private void setAccount(Account account) {
            this.account = account;
        }

        private void setGeneralError(String generalError) {
            this.generalError = generalError;
        }
    }
}
