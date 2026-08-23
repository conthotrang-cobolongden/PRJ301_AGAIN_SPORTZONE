package service;

import dao.AccountDAO;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import model.Account;
import utils.PasswordUtil;
import utils.Validation;

/**
 * Business rules for viewing/updating the current profile and changing password.
 * Tags: U4, U5, U6.
 */
public class ProfileService {

    private final AccountDAO accountDAO;

    public ProfileService() {
        this(new AccountDAO());
    }

    public ProfileService(AccountDAO accountDAO) {
        if (accountDAO == null) {
            throw new IllegalArgumentException("AccountDAO must not be null");
        }
        this.accountDAO = accountDAO;
    }

    /** U5: Validate, update and reload the current account. */
    public UpdateProfileResult updateProfile(int accountID, String fullName,
            String email, String phone) throws SQLException {
        requireValidAccountID(accountID);

        String normalizedFullName = Validation.trimToNull(fullName);
        String normalizedEmail = normalizeEmail(email);
        String normalizedPhone = normalizePhone(phone);
        LinkedHashMap<String, String> errors = new LinkedHashMap<>();

        if (normalizedFullName == null) {
            errors.put("name", "Họ tên không được để trống");
        } else if (normalizedFullName.length() > 100) {
            errors.put("name", "Họ tên tối đa 100 ký tự");
        }

        if (normalizedEmail == null) {
            errors.put("email", "Email không được để trống");
        } else if (!Validation.isEmail(normalizedEmail)) {
            errors.put("email", "Email không đúng định dạng");
        } else if (accountDAO.existsByEmailExcludingId(
                normalizedEmail, accountID)) {
            errors.put("email", "Email đã được sử dụng");
        }

        if (normalizedPhone == null) {
            errors.put("phone", "Số điện thoại không được để trống");
        } else if (!Validation.isVietnamesePhone(normalizedPhone)) {
            errors.put("phone", "Số điện thoại không đúng định dạng Việt Nam");
        } else if (accountDAO.existsByPhoneExcludingId(
                normalizedPhone, accountID)) {
            errors.put("phone", "Số điện thoại đã được sử dụng");
        }

        if (!errors.isEmpty()) {
            return new UpdateProfileResult(errors, null);
        }

        boolean updated = accountDAO.updateProfile(accountID,
                normalizedFullName, normalizedEmail, normalizedPhone);
        if (!updated) {
            throw new SQLException(
                    "Profile account does not exist: accountID=" + accountID);
        }

        Account updatedAccount = accountDAO.findById(accountID);
        if (updatedAccount == null) {
            throw new SQLException(
                    "Updated profile could not be reloaded: accountID=" + accountID);
        }
        return new UpdateProfileResult(errors, updatedAccount);
    }

    /** U6: Verify the current password and persist the SHA-256 hash of the new one. */
    public ChangePasswordResult changePassword(int accountID,
            String currentPassword, String newPassword,
            String confirmPassword) throws SQLException {
        requireValidAccountID(accountID);
        LinkedHashMap<String, String> errors = new LinkedHashMap<>();

        if (Validation.trimToNull(currentPassword) == null) {
            errors.put("currentPassword", "Mật khẩu hiện tại không được để trống");
        }

        if (Validation.trimToNull(newPassword) == null) {
            errors.put("newPassword", "Mật khẩu mới không được để trống");
        } else if (newPassword.length() < 8 || newPassword.length() > 64) {
            errors.put("newPassword", "Mật khẩu mới phải từ 8 đến 64 ký tự");
        }

        if (Validation.trimToNull(confirmPassword) == null) {
            errors.put("confirmPassword", "Nhập lại mật khẩu không được để trống");
        } else if (!confirmPassword.equals(newPassword)) {
            errors.put("confirmPassword", "Nhập lại mật khẩu không khớp");
        }

        if (!errors.isEmpty()) {
            return new ChangePasswordResult(errors);
        }

        Account storedAccount = accountDAO.findById(accountID);
        if (storedAccount == null) {
            throw new SQLException(
                    "Password account does not exist: accountID=" + accountID);
        }

        if (!PasswordUtil.matches(
                currentPassword, storedAccount.getPasswordHash())) {
            errors.put("currentPassword", "Mật khẩu hiện tại không đúng");
            return new ChangePasswordResult(errors);
        }

        boolean updated = accountDAO.updatePasswordHash(
                accountID, PasswordUtil.sha256Hex(newPassword));
        if (!updated) {
            throw new SQLException(
                    "Password account disappeared during update: accountID=" + accountID);
        }
        return new ChangePasswordResult(errors);
    }

    private static void requireValidAccountID(int accountID) {
        if (accountID <= 0) {
            throw new IllegalArgumentException("AccountID must be positive");
        }
    }

    private static String normalizeEmail(String email) {
        String normalized = Validation.trimToNull(email);
        return normalized == null
                ? null : normalized.toLowerCase(Locale.ROOT);
    }

    private static String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        return Validation.trimToNull(phone.replaceAll("[\\s-]", ""));
    }

    public static final class UpdateProfileResult {

        private final Map<String, String> fieldErrors;
        private final Account account;

        private UpdateProfileResult(Map<String, String> fieldErrors,
                Account account) {
            this.fieldErrors = immutableCopy(fieldErrors);
            this.account = account;
        }

        public boolean isSuccess() {
            return fieldErrors.isEmpty() && account != null;
        }

        public Map<String, String> getFieldErrors() {
            return fieldErrors;
        }

        public Account getAccount() {
            return account;
        }
    }

    public static final class ChangePasswordResult {

        private final Map<String, String> fieldErrors;

        private ChangePasswordResult(Map<String, String> fieldErrors) {
            this.fieldErrors = immutableCopy(fieldErrors);
        }

        public boolean isSuccess() {
            return fieldErrors.isEmpty();
        }

        public Map<String, String> getFieldErrors() {
            return fieldErrors;
        }
    }

    private static Map<String, String> immutableCopy(
            Map<String, String> source) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(source));
    }
}
